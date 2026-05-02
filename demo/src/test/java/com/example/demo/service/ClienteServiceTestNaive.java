package com.example.demo.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Cliente;
import com.example.demo.exception.CuentaDesactivadaException;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ClienteServiceTestNaive {

    @Autowired
    private ClienteService service;

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    public void ClienteService_findAll_ReturnsAllClientes() {

        Collection<Cliente> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isNotEmpty();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    public void ClienteService_findById_ReturnsClienteWhenExists() {
        Long id = service.findByUsername("pablo123").getId();

        Cliente result = service.findById(id);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void ClienteService_findById_ThrowsWhenNotExists() {

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── findByUsername ────────────────────────────────────────────────────────

    @Test
    public void ClienteService_findByUsername_ReturnsClienteWhenExists() {

        Cliente result = service.findByUsername("pablo123");

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo("pablo123");
    }

    @Test
    public void ClienteService_findByUsername_ReturnsNullWhenNotExists() {

        Cliente result = service.findByUsername("noexiste");

        Assertions.assertThat(result).isNull();
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    public void ClienteService_create_CreatesSuccessfully() {
        Cliente nuevo = new Cliente("Nuevo", "Cliente", "nuevo@mail.com", "nuevou", "newPass", "Calle Z", "3009999999");

        Cliente result = service.create(nuevo);

        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo("nuevou");
    }

    @Test
    public void ClienteService_create_ThrowsWhenUsernameTaken() {
        Cliente duplicado = new Cliente("X", "X", "otro@mail.com", "pablo123", "pass", "Calle Z", "3009999998");

        Assertions.assertThatThrownBy(() -> service.create(duplicado))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void ClienteService_create_ThrowsWhenEmailTaken() {
        Cliente duplicado = new Cliente("X", "X", "PabloGarcia21@gmail.com", "otrousuario", "pass", "Calle Z", "3009999997");

        Assertions.assertThatThrownBy(() -> service.create(duplicado))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    public void ClienteService_update_UpdatesSuccessfully() {
        Long id = service.findByUsername("pablo123").getId();

        Cliente result = service.update(id, "PabloNuevo", "García", "pablo.nuevo@mail.com",
                "pablo123", "nuevaPass", "Cra 7", "3001234567", "123456");

        Assertions.assertThat(result.getName()).isEqualTo("PabloNuevo");
        Assertions.assertThat(result.getPassword()).isEqualTo("nuevaPass");
    }

    @Test
    public void ClienteService_update_ThrowsWhenIdNotFound() {

        Assertions.assertThatThrownBy(() -> service.update(999L, "X", "X", "x@mail.com",
                "x", "pass", "X", "0", "pass"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ClienteService_update_ThrowsWhenCurrentPasswordIsWrong() {
        Long id = service.findByUsername("pablo123").getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "Pablo", "García",
                "PabloGarcia21@gmail.com", "pablo123", null, "Cra 7", "3001234567", "wrongPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void ClienteService_update_ThrowsWhenUsernameTaken() {
        Long id = service.findByUsername("pablo123").getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "Pablo", "García",
                "PabloGarcia21@gmail.com", "maria123", null, "Cra 7", "3001234567", "123456"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void ClienteService_update_ThrowsWhenEmailTaken() {
        Long id = service.findByUsername("pablo123").getId();

        Assertions.assertThatThrownBy(() -> service.update(id, "Pablo", "García",
                "maria.gomez@email.com", "pablo123", null, "Cra 7", "3001234567", "123456"))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── loginCliente ──────────────────────────────────────────────────────────

    @Test
    public void ClienteService_loginCliente_ReturnsClienteWhenCredentialsCorrect() {

        Cliente result = service.loginCliente("pablo123", "123456");

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo("pablo123");
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenPasswordWrong() {

        Assertions.assertThatThrownBy(() -> service.loginCliente("pablo123", "wrongPass"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenUsernameNull() {

        Assertions.assertThatThrownBy(() -> service.loginCliente(null, "123456"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenPasswordNull() {

        Assertions.assertThatThrownBy(() -> service.loginCliente("pablo123", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenInactive() {

        Assertions.assertThatThrownBy(() -> service.loginCliente("inactivo", "inacPass"))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── deleteOrDeactivate ────────────────────────────────────────────────────

    @Test
    public void ClienteService_deleteOrDeactivate_DeletesWhenNoPedidos() {
        Long id = service.findByUsername("inactivo").getId();

        service.deleteOrDeactivate(id);

        Assertions.assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ClienteService_deleteOrDeactivate_ThrowsCuentaDesactivadaWhenOnlyHistorial() {
        Long id = service.findByUsername("historial").getId();

        Assertions.assertThatThrownBy(() -> service.deleteOrDeactivate(id))
                .isInstanceOf(CuentaDesactivadaException.class);
    }

    @Test
    public void ClienteService_deleteOrDeactivate_ThrowsIllegalStateWhenActiveOrders() {
        Long id = service.findByUsername("pablo123").getId();

        Assertions.assertThatThrownBy(() -> service.deleteOrDeactivate(id))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void ClienteService_deleteOrDeactivate_ThrowsWhenNotFound() {

        Assertions.assertThatThrownBy(() -> service.deleteOrDeactivate(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
