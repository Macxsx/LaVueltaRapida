package com.example.demo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AuthServiceTestNaive {

    @Autowired
    private AuthService service;

    @Test
    public void AuthService_login_ReturnsAdminRole() {

        AuthService.LoginResult result = service.login("admin1", "123");

        Assertions.assertThat(result.role).isEqualTo("admin");
        Assertions.assertThat(result.username).isEqualTo("admin1");
        Assertions.assertThat(result.clienteId).isNull();
        Assertions.assertThat(result.carritoId).isNull();
    }

    @Test
    public void AuthService_login_ReturnsOperadorRole() {

        AuthService.LoginResult result = service.login("op1", "123");

        Assertions.assertThat(result.role).isEqualTo("operador");
        Assertions.assertThat(result.username).isEqualTo("op1");
        Assertions.assertThat(result.clienteId).isNull();
        Assertions.assertThat(result.carritoId).isNull();
    }

    @Test
    public void AuthService_login_ReturnsClienteRoleWithIds() {

        AuthService.LoginResult result = service.login("pablo123", "123456");

        Assertions.assertThat(result.role).isEqualTo("cliente");
        Assertions.assertThat(result.username).isEqualTo("pablo123");
        Assertions.assertThat(result.clienteId).isNotNull();
        Assertions.assertThat(result.carritoId).isNotNull();
    }

    @Test
    public void AuthService_login_ThrowsWhenPasswordIsWrong() {

        Assertions.assertThatThrownBy(() -> service.login("admin1", "wrongPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void AuthService_login_ThrowsWhenUserNotFound() {

        Assertions.assertThatThrownBy(() -> service.login("noexiste", "123"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void AuthService_login_ThrowsWhenUsuarioIsNull() {

        Assertions.assertThatThrownBy(() -> service.login(null, "123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void AuthService_login_ThrowsWhenContrasenaIsNull() {

        Assertions.assertThatThrownBy(() -> service.login("admin1", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void AuthService_login_ThrowsWhenClienteIsInactive() {

        Assertions.assertThatThrownBy(() -> service.login("inactivo", "inacPass"))
                .isInstanceOf(IllegalStateException.class);
    }
}
