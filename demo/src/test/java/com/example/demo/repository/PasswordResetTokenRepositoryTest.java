package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.demo.entitys.PasswordResetToken;

@DataJpaTest
public class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @BeforeEach
    public void setUp() {
        tokenRepo.save(buildToken("token-abc", "juan@mail.com",  LocalDateTime.now().plusHours(1)));
        tokenRepo.save(buildToken("token-xyz", "ana@mail.com",   LocalDateTime.now().plusHours(1)));
        tokenRepo.save(buildToken("token-exp", "pedro@mail.com", LocalDateTime.now().minusHours(1)));
    }

    // ── findByToken ───────────────────────────────────────────────────────────

    @Test
    public void PasswordResetTokenRepository_findByToken_ReturnsPresentWhenExists() {

        Optional<PasswordResetToken> result = tokenRepo.findByToken("token-abc");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getToken()).isEqualTo("token-abc");
        Assertions.assertThat(result.get().getEmail()).isEqualTo("juan@mail.com");
    }

    @Test
    public void PasswordResetTokenRepository_findByToken_ReturnsEmptyWhenNotExists() {

        Optional<PasswordResetToken> result = tokenRepo.findByToken("token-inexistente");

        Assertions.assertThat(result).isEmpty();
    }

    // ── findByEmail ───────────────────────────────────────────────────────────

    @Test
    public void PasswordResetTokenRepository_findByEmail_ReturnsPresentWhenExists() {

        Optional<PasswordResetToken> result = tokenRepo.findByEmail("ana@mail.com");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getEmail()).isEqualTo("ana@mail.com");
        Assertions.assertThat(result.get().getToken()).isEqualTo("token-xyz");
    }

    @Test
    public void PasswordResetTokenRepository_findByEmail_ReturnsEmptyWhenNotExists() {

        Optional<PasswordResetToken> result = tokenRepo.findByEmail("noexiste@mail.com");

        Assertions.assertThat(result).isEmpty();
    }

    // ── expirado ──────────────────────────────────────────────────────────────

    @Test
    public void PasswordResetTokenRepository_findByToken_ReturnsPresentParaTokenExpirado() {
        // El repositorio no filtra por fecha; eso es responsabilidad del servicio.
        Optional<PasswordResetToken> result = tokenRepo.findByToken("token-exp");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getExpiry()).isBefore(LocalDateTime.now());
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    public void PasswordResetTokenRepository_delete_EliminaElToken() {
        PasswordResetToken prt = tokenRepo.findByToken("token-abc").get();

        tokenRepo.delete(prt);

        Assertions.assertThat(tokenRepo.findByToken("token-abc")).isEmpty();
    }

    @Test
    public void PasswordResetTokenRepository_delete_NoAfectaOtrosTokens() {
        PasswordResetToken prt = tokenRepo.findByToken("token-abc").get();

        tokenRepo.delete(prt);

        Assertions.assertThat(tokenRepo.findByToken("token-xyz")).isPresent();
        Assertions.assertThat(tokenRepo.count()).isEqualTo(2);
    }

    // ── unicidad ─────────────────────────────────────────────────────────────

    @Test
    public void PasswordResetTokenRepository_save_ThrowsWhenTokenDuplicado() {
        PasswordResetToken duplicado = buildToken("token-abc", "otro@mail.com", LocalDateTime.now().plusHours(1));

        Assertions.assertThatThrownBy(() -> tokenRepo.saveAndFlush(duplicado))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private PasswordResetToken buildToken(String token, String email, LocalDateTime expiry) {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setEmail(email);
        prt.setExpiry(expiry);
        return prt;
    }
}
