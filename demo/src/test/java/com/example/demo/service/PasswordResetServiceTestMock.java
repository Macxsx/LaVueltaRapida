package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.PasswordResetToken;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PasswordResetTokenRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTestMock {

    private PasswordResetServiceImpl service;

    @Mock
    private PasswordResetTokenRepository tokenRepo;

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        service = new PasswordResetServiceImpl();
        ReflectionTestUtils.setField(service, "tokenRepo", tokenRepo);
        ReflectionTestUtils.setField(service, "clienteRepo", clienteRepo);
        ReflectionTestUtils.setField(service, "mailSender", mailSender);
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(service, "mailFrom", "noreply@test.com");
    }

    // ── solicitarRecuperacion ─────────────────────────────────────────────────

    @Test
    public void PasswordResetService_solicitarRecuperacion_ThrowsWhenClienteNoExiste() {
        when(clienteRepo.findByEmail("noexiste@test.com")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> service.solicitarRecuperacion("noexiste@test.com"))
                .isInstanceOf(NoSuchElementException.class);

        verify(tokenRepo, never()).save(any());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void PasswordResetService_solicitarRecuperacion_EliminaTokenPrevioSiExiste() {
        Cliente cliente = buildCliente(1L, "Juan", "juan@test.com");
        PasswordResetToken tokenExistente = buildToken("token-viejo", "juan@test.com", LocalDateTime.now().plusHours(1));
        when(clienteRepo.findByEmail("juan@test.com")).thenReturn(cliente);
        when(tokenRepo.findByEmail("juan@test.com")).thenReturn(Optional.of(tokenExistente));

        service.solicitarRecuperacion("juan@test.com");

        verify(tokenRepo).delete(tokenExistente);
    }

    @Test
    public void PasswordResetService_solicitarRecuperacion_GuardaNuevoTokenConExpiryDeUnaHora() {
        Cliente cliente = buildCliente(1L, "Juan", "juan@test.com");
        when(clienteRepo.findByEmail("juan@test.com")).thenReturn(cliente);
        when(tokenRepo.findByEmail("juan@test.com")).thenReturn(Optional.empty());

        service.solicitarRecuperacion("juan@test.com");

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepo).save(captor.capture());
        PasswordResetToken guardado = captor.getValue();
        Assertions.assertThat(guardado.getToken()).isNotBlank();
        Assertions.assertThat(guardado.getEmail()).isEqualTo("juan@test.com");
        Assertions.assertThat(guardado.getExpiry()).isAfter(LocalDateTime.now().plusMinutes(59));
        Assertions.assertThat(guardado.getExpiry()).isBefore(LocalDateTime.now().plusHours(1).plusSeconds(5));
    }

    @Test
    public void PasswordResetService_solicitarRecuperacion_EnviaCorreoConEnlaceCorrecto() {
        Cliente cliente = buildCliente(1L, "Juan", "juan@test.com");
        when(clienteRepo.findByEmail("juan@test.com")).thenReturn(cliente);
        when(tokenRepo.findByEmail("juan@test.com")).thenReturn(Optional.empty());
        when(tokenRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.solicitarRecuperacion("juan@test.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        Assertions.assertThat(msg.getTo()).contains("juan@test.com");
        Assertions.assertThat(msg.getFrom()).isEqualTo("noreply@test.com");
        Assertions.assertThat(msg.getText()).contains("http://localhost:4200/reset-password?token=");
        Assertions.assertThat(msg.getText()).contains("Juan");
    }

    @Test
    public void PasswordResetService_solicitarRecuperacion_ThrowsWhenMailSenderEsNull() {
        ReflectionTestUtils.setField(service, "mailSender", null);
        Cliente cliente = buildCliente(1L, "Juan", "juan@test.com");
        when(clienteRepo.findByEmail("juan@test.com")).thenReturn(cliente);
        when(tokenRepo.findByEmail("juan@test.com")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.solicitarRecuperacion("juan@test.com"))
                .isInstanceOf(IllegalStateException.class);

        verify(tokenRepo).save(any());
    }

    @Test
    public void PasswordResetService_solicitarRecuperacion_ThrowsWhenFallaElEnvioDelCorreo() {
        Cliente cliente = buildCliente(1L, "Juan", "juan@test.com");
        when(clienteRepo.findByEmail("juan@test.com")).thenReturn(cliente);
        when(tokenRepo.findByEmail("juan@test.com")).thenReturn(Optional.empty());
        org.mockito.Mockito.doThrow(new org.springframework.mail.MailSendException("SMTP down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        Assertions.assertThatThrownBy(() -> service.solicitarRecuperacion("juan@test.com"))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── resetContrasena ───────────────────────────────────────────────────────

    @Test
    public void PasswordResetService_resetContrasena_ThrowsWhenTokenNoExiste() {
        when(tokenRepo.findByToken("token-invalido")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.resetContrasena("token-invalido", "pass"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(clienteRepo, never()).save(any());
    }

    @Test
    public void PasswordResetService_resetContrasena_ThrowsYEliminaTokenCuandoEstaExpirado() {
        PasswordResetToken prt = buildToken("token-exp", "juan@test.com", LocalDateTime.now().minusMinutes(1));
        when(tokenRepo.findByToken("token-exp")).thenReturn(Optional.of(prt));

        Assertions.assertThatThrownBy(() -> service.resetContrasena("token-exp", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expirado");

        verify(tokenRepo).delete(prt);
        verify(clienteRepo, never()).save(any());
    }

    @Test
    public void PasswordResetService_resetContrasena_ThrowsWhenClienteNoExistePorEmail() {
        PasswordResetToken prt = buildToken("token-ok", "fantasma@test.com", LocalDateTime.now().plusHours(1));
        when(tokenRepo.findByToken("token-ok")).thenReturn(Optional.of(prt));
        when(clienteRepo.findByEmail("fantasma@test.com")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> service.resetContrasena("token-ok", "pass"))
                .isInstanceOf(NoSuchElementException.class);

        verify(clienteRepo, never()).save(any());
    }

    @Test
    public void PasswordResetService_resetContrasena_ActualizaPasswordYEliminaToken() {
        Cliente cliente = buildCliente(1L, "Juan", "juan@test.com");
        PasswordResetToken prt = buildToken("token-ok", "juan@test.com", LocalDateTime.now().plusHours(1));
        when(tokenRepo.findByToken("token-ok")).thenReturn(Optional.of(prt));
        when(clienteRepo.findByEmail("juan@test.com")).thenReturn(cliente);

        service.resetContrasena("token-ok", "nuevaPass123");

        Assertions.assertThat(cliente.getPassword()).isEqualTo("nuevaPass123");
        verify(clienteRepo).save(cliente);
        verify(tokenRepo).delete(prt);
    }

    @Test
    public void PasswordResetService_resetContrasena_NoEliminaTokenSiClienteNoExiste() {
        PasswordResetToken prt = buildToken("token-ok", "fantasma@test.com", LocalDateTime.now().plusHours(1));
        when(tokenRepo.findByToken("token-ok")).thenReturn(Optional.of(prt));
        when(clienteRepo.findByEmail("fantasma@test.com")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> service.resetContrasena("token-ok", "pass"))
                .isInstanceOf(NoSuchElementException.class);

        verify(tokenRepo, never()).delete(prt);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Cliente buildCliente(Long id, String nombre, String email) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setName(nombre);
        c.setEmail(email);
        c.setPassword("pass123");
        return c;
    }

    private PasswordResetToken buildToken(String token, String email, LocalDateTime expiry) {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setEmail(email);
        prt.setExpiry(expiry);
        return prt;
    }
}
