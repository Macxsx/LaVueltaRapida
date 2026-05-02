package com.example.demo.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Administrador;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.Operador;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTestMock {

    @InjectMocks
    private AuthServiceImpl service;

    @Mock
    AdministradorService administradorService;

    @Mock
    OperadorService operadorService;

    @Mock
    ClienteService clienteService;

    @Mock
    CarritoService carritoService;


    @Test
    public void AuthService_login_ReturnsAdminRole() {
        Administrador admin = new Administrador(1L, "admin1", "123");
        when(administradorService.findByUsuario("admin1")).thenReturn(Optional.of(admin));

        AuthService.LoginResult result = service.login("admin1", "123");

        Assertions.assertThat(result.role).isEqualTo("admin");
        Assertions.assertThat(result.username).isEqualTo("admin1");
        Assertions.assertThat(result.clienteId).isNull();
        Assertions.assertThat(result.carritoId).isNull();
        verify(operadorService, never()).findByUsuario("admin1");
    }

    @Test
    public void AuthService_login_ReturnsOperadorRole() {
        Operador op = new Operador(1L, "Operador Uno", "op1", "123");
        when(administradorService.findByUsuario("op1")).thenReturn(Optional.empty());
        when(operadorService.findByUsuario("op1")).thenReturn(Optional.of(op));

        AuthService.LoginResult result = service.login("op1", "123");

        Assertions.assertThat(result.role).isEqualTo("operador");
        Assertions.assertThat(result.username).isEqualTo("op1");
        Assertions.assertThat(result.clienteId).isNull();
        Assertions.assertThat(result.carritoId).isNull();
        verify(clienteService, never()).findByUsername("op1");
    }

    @Test
    public void AuthService_login_ReturnsClienteRoleWithIds() {
        Cliente cliente = new Cliente("Pablo", "Perez", "p@x.com",
                "pablo123", "123456", "calle 1", "111");
        cliente.setId(7L);
        cliente.setActivo(true);
        Carrito carrito = new Carrito();
        carrito.setId(42L);

        when(administradorService.findByUsuario("pablo123")).thenReturn(Optional.empty());
        when(operadorService.findByUsuario("pablo123")).thenReturn(Optional.empty());
        when(clienteService.findByUsername("pablo123")).thenReturn(cliente);
        when(carritoService.findByClienteId(7L)).thenReturn(Optional.of(carrito));

        AuthService.LoginResult result = service.login("pablo123", "123456");

        Assertions.assertThat(result.role).isEqualTo("cliente");
        Assertions.assertThat(result.username).isEqualTo("pablo123");
        Assertions.assertThat(result.clienteId).isEqualTo(7L);
        Assertions.assertThat(result.carritoId).isEqualTo(42L);
    }

    @Test
    public void AuthService_login_ReturnsClienteWithNullCarritoWhenCarritoMissing() {
        Cliente cliente = new Cliente("Pablo", "Perez", "p@x.com",
                "pablo123", "123456", "calle 1", "111");
        cliente.setId(7L);
        cliente.setActivo(true);

        when(administradorService.findByUsuario("pablo123")).thenReturn(Optional.empty());
        when(operadorService.findByUsuario("pablo123")).thenReturn(Optional.empty());
        when(clienteService.findByUsername("pablo123")).thenReturn(cliente);
        when(carritoService.findByClienteId(7L)).thenReturn(Optional.empty());

        AuthService.LoginResult result = service.login("pablo123", "123456");

        Assertions.assertThat(result.role).isEqualTo("cliente");
        Assertions.assertThat(result.clienteId).isEqualTo(7L);
        Assertions.assertThat(result.carritoId).isNull();
    }

    @Test
    public void AuthService_login_ThrowsWhenPasswordIsWrong() {
        Administrador admin = new Administrador(1L, "admin1", "123");
        when(administradorService.findByUsuario("admin1")).thenReturn(Optional.of(admin));
        when(operadorService.findByUsuario("admin1")).thenReturn(Optional.empty());
        when(clienteService.findByUsername("admin1")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> service.login("admin1", "wrongPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void AuthService_login_ThrowsWhenUserNotFound() {
        when(administradorService.findByUsuario("noexiste")).thenReturn(Optional.empty());
        when(operadorService.findByUsuario("noexiste")).thenReturn(Optional.empty());
        when(clienteService.findByUsername("noexiste")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> service.login("noexiste", "123"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void AuthService_login_ThrowsWhenUsuarioIsNull() {
        Assertions.assertThatThrownBy(() -> service.login(null, "123"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(administradorService, never()).findByUsuario(null);
    }

    @Test
    public void AuthService_login_ThrowsWhenContrasenaIsNull() {
        Assertions.assertThatThrownBy(() -> service.login("admin1", null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(administradorService, never()).findByUsuario("admin1");
    }

    @Test
    public void AuthService_login_ThrowsWhenClienteIsInactive() {
        Cliente cliente = new Cliente("Inac", "Tivo", "i@x.com",
                "inactivo", "inacPass", "calle", "000");
        cliente.setId(9L);
        cliente.setActivo(false);

        when(administradorService.findByUsuario("inactivo")).thenReturn(Optional.empty());
        when(operadorService.findByUsuario("inactivo")).thenReturn(Optional.empty());
        when(clienteService.findByUsername("inactivo")).thenReturn(cliente);

        Assertions.assertThatThrownBy(() -> service.login("inactivo", "inacPass"))
                .isInstanceOf(IllegalStateException.class);

        verify(carritoService, never()).findByClienteId(9L);
    }

    @Test
    public void AuthService_login_FallsThroughWhenAdminPasswordMismatchesButOperadorMatches() {
        Administrador admin = new Administrador(1L, "compartido", "adminPass");
        Operador op = new Operador(1L, "Op Uno", "compartido", "opPass");

        when(administradorService.findByUsuario("compartido")).thenReturn(Optional.of(admin));
        when(operadorService.findByUsuario("compartido")).thenReturn(Optional.of(op));

        AuthService.LoginResult result = service.login("compartido", "opPass");

        Assertions.assertThat(result.role).isEqualTo("operador");
        Assertions.assertThat(result.username).isEqualTo("compartido");
    }
}
