package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Cliente;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.exception.CuentaDesactivadaException;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PedidoRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClienteServiceTestMock {

    @InjectMocks
    private ClienteServiceImpl service;

    @Mock
    ClienteRepository repo;

    @Mock
    CarritoRepository carritoRepo;

    @Mock
    PedidoRepository pedidoRepo;


    private Cliente buildCliente(Long id, String username, String email, String password, boolean activo) {
        Cliente c = new Cliente("Pablo", "Perez", email, username, password, "calle 1", "111");
        c.setId(id);
        c.setActivo(activo);
        return c;
    }


    @Test
    public void ClienteService_findAll_ReturnsAllClientes() {
        when(repo.findAll()).thenReturn(List.of(
                buildCliente(1L, "u1", "u1@x.com", "p", true),
                buildCliente(2L, "u2", "u2@x.com", "p", true),
                buildCliente(3L, "u3", "u3@x.com", "p", true)
        ));

        Collection<Cliente> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(3);
    }

    @Test
    public void ClienteService_findById_ReturnsClienteWhenExists() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente result = service.findById(1L);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(1L);
        Assertions.assertThat(result.getUsername()).isEqualTo("pablo");
    }

    @Test
    public void ClienteService_findById_ThrowsWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ClienteService_findByUsername_DelegatesToRepository() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findByUsername("pablo")).thenReturn(cliente);

        Cliente result = service.findByUsername("pablo");

        Assertions.assertThat(result).isSameAs(cliente);
    }

    @Test
    public void ClienteService_findByEmail_ReturnsClienteWhenExists() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findByEmail("p@x.com")).thenReturn(cliente);

        Cliente result = service.findByEmail("p@x.com");

        Assertions.assertThat(result).isSameAs(cliente);
    }

    @Test
    public void ClienteService_findByEmail_ReturnsNullWhenNotExists() {
        when(repo.findByEmail("noexiste@x.com")).thenReturn(null);

        Cliente result = service.findByEmail("noexiste@x.com");

        Assertions.assertThat(result).isNull();
    }


    @Test
    public void ClienteService_create_SavesClienteAndCreatesInactiveCarrito() {
        Cliente nuevo = buildCliente(null, "pablo", "p@x.com", "123456", true);
        Cliente guardado = buildCliente(7L, "pablo", "p@x.com", "123456", true);

        when(repo.findByUsername("pablo")).thenReturn(null);
        when(repo.findByEmail("p@x.com")).thenReturn(null);
        when(repo.save(nuevo)).thenReturn(guardado);
        when(carritoRepo.findByClienteId(7L)).thenReturn(Optional.empty());

        Cliente result = service.create(nuevo);

        Assertions.assertThat(result).isSameAs(guardado);
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepo).save(captor.capture());
        Carrito carrito = captor.getValue();
        Assertions.assertThat(carrito.getCliente()).isSameAs(guardado);
        Assertions.assertThat(carrito.isActivo()).isFalse();
    }

    @Test
    public void ClienteService_create_DoesNotCreateCarritoWhenAlreadyExists() {
        Cliente nuevo = buildCliente(null, "pablo", "p@x.com", "123456", true);
        Cliente guardado = buildCliente(7L, "pablo", "p@x.com", "123456", true);

        when(repo.findByUsername("pablo")).thenReturn(null);
        when(repo.findByEmail("p@x.com")).thenReturn(null);
        when(repo.save(nuevo)).thenReturn(guardado);
        when(carritoRepo.findByClienteId(7L)).thenReturn(Optional.of(new Carrito()));

        service.create(nuevo);

        verify(carritoRepo, never()).save(any(Carrito.class));
    }

    @Test
    public void ClienteService_create_ThrowsWhenUsernameAlreadyExists() {
        Cliente nuevo = buildCliente(null, "pablo", "p@x.com", "123456", true);
        when(repo.findByUsername("pablo")).thenReturn(buildCliente(99L, "pablo", "x@x.com", "p", true));

        Assertions.assertThatThrownBy(() -> service.create(nuevo))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Cliente.class));
    }

    @Test
    public void ClienteService_create_ThrowsWhenEmailAlreadyExists() {
        Cliente nuevo = buildCliente(null, "pablo", "p@x.com", "123456", true);
        when(repo.findByUsername("pablo")).thenReturn(null);
        when(repo.findByEmail("p@x.com")).thenReturn(buildCliente(99L, "otro", "p@x.com", "p", true));

        Assertions.assertThatThrownBy(() -> service.create(nuevo))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Cliente.class));
    }


    @Test
    public void ClienteService_update_UpdatesAllFields() {
        Cliente stored = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsername("pabloNuevo")).thenReturn(null);
        when(repo.findByEmail("nuevo@x.com")).thenReturn(null);
        when(repo.save(stored)).thenReturn(stored);

        Cliente result = service.update(1L, "Pablo2", "Perez2", "nuevo@x.com", "pabloNuevo",
                "newPass", "calle 2", "222", "123456");

        Assertions.assertThat(result.getName()).isEqualTo("Pablo2");
        Assertions.assertThat(result.getApellido()).isEqualTo("Perez2");
        Assertions.assertThat(result.getEmail()).isEqualTo("nuevo@x.com");
        Assertions.assertThat(result.getUsername()).isEqualTo("pabloNuevo");
        Assertions.assertThat(result.getPassword()).isEqualTo("newPass");
        Assertions.assertThat(result.getDireccion()).isEqualTo("calle 2");
        Assertions.assertThat(result.getTelefono()).isEqualTo("222");
    }

    @Test
    public void ClienteService_update_KeepsOldPasswordWhenNewIsBlank() {
        Cliente stored = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsername("pablo")).thenReturn(stored);
        when(repo.findByEmail("p@x.com")).thenReturn(stored);
        when(repo.save(stored)).thenReturn(stored);

        Cliente result = service.update(1L, "Pablo2", "Perez", "p@x.com", "pablo",
                "   ", "calle", "111", "123456");

        Assertions.assertThat(result.getPassword()).isEqualTo("123456");
    }

    @Test
    public void ClienteService_update_AllowsSameUsernameAndEmailForSameCliente() {
        Cliente stored = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsername("pablo")).thenReturn(stored);
        when(repo.findByEmail("p@x.com")).thenReturn(stored);
        when(repo.save(stored)).thenReturn(stored);

        Cliente result = service.update(1L, "Pablo", "Perez", "p@x.com", "pablo",
                "newPass", "calle", "111", "123456");

        Assertions.assertThat(result.getPassword()).isEqualTo("newPass");
    }

    @Test
    public void ClienteService_update_ThrowsWhenClienteDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(999L, "n", "a", "e", "u", "p", "d", "t", null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void ClienteService_update_ThrowsWhenCurrentPasswordIsWrong() {
        Cliente stored = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));

        Assertions.assertThatThrownBy(() -> service.update(1L, "n", "a", "e", "u", "p", "d", "t", "incorrecta"))
                .isInstanceOf(SecurityException.class);

        verify(repo, never()).save(any(Cliente.class));
    }

    @Test
    public void ClienteService_update_ThrowsWhenUsernameBelongsToAnotherCliente() {
        Cliente stored = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        Cliente otro    = buildCliente(2L, "ocupado", "otro@x.com", "p", true);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsername("ocupado")).thenReturn(otro);

        Assertions.assertThatThrownBy(() -> service.update(1L, "n", "a", "e", "ocupado", "p", "d", "t", "123456"))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Cliente.class));
    }

    @Test
    public void ClienteService_update_ThrowsWhenEmailBelongsToAnotherCliente() {
        Cliente stored = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        Cliente otro    = buildCliente(2L, "otro", "ocupado@x.com", "p", true);
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsername("pablo")).thenReturn(stored);
        when(repo.findByEmail("ocupado@x.com")).thenReturn(otro);

        Assertions.assertThatThrownBy(() -> service.update(1L, "n", "a", "ocupado@x.com", "pablo", "p", "d", "t", "123456"))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Cliente.class));
    }


    @Test
    public void ClienteService_loginCliente_ReturnsClienteWhenCredentialsAreCorrect() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findByUsername("pablo")).thenReturn(cliente);

        Cliente result = service.loginCliente("pablo", "123456");

        Assertions.assertThat(result).isSameAs(cliente);
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenUsernameIsNull() {
        Assertions.assertThatThrownBy(() -> service.loginCliente(null, "123456"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).findByUsername(any());
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenPasswordIsNull() {
        Assertions.assertThatThrownBy(() -> service.loginCliente("pablo", null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).findByUsername(any());
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenUserNotFound() {
        when(repo.findByUsername("noexiste")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> service.loginCliente("noexiste", "123"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenPasswordIsWrong() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findByUsername("pablo")).thenReturn(cliente);

        Assertions.assertThatThrownBy(() -> service.loginCliente("pablo", "incorrecta"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    public void ClienteService_loginCliente_ThrowsWhenAccountIsInactive() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", false);
        when(repo.findByUsername("pablo")).thenReturn(cliente);

        Assertions.assertThatThrownBy(() -> service.loginCliente("pablo", "123456"))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    public void ClienteService_deleteOrDeactivate_DeletesWhenNoPedidos() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoRepo.existsByClienteIdAndEstadoNot(1L, EstadoPedido.ENTREGADO)).thenReturn(false);
        when(pedidoRepo.existsByClienteId(1L)).thenReturn(false);

        service.deleteOrDeactivate(1L);

        verify(repo).deleteById(1L);
        verify(repo, never()).save(any(Cliente.class));
    }

    @Test
    public void ClienteService_deleteOrDeactivate_DeactivatesWhenHasOnlyDeliveredPedidos() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoRepo.existsByClienteIdAndEstadoNot(1L, EstadoPedido.ENTREGADO)).thenReturn(false);
        when(pedidoRepo.existsByClienteId(1L)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.deleteOrDeactivate(1L))
                .isInstanceOf(CuentaDesactivadaException.class);

        Assertions.assertThat(cliente.isActivo()).isFalse();
        verify(repo).save(cliente);
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    public void ClienteService_deleteOrDeactivate_ThrowsWhenHasActivePedidos() {
        Cliente cliente = buildCliente(1L, "pablo", "p@x.com", "123456", true);
        when(repo.findById(1L)).thenReturn(Optional.of(cliente));
        when(pedidoRepo.existsByClienteIdAndEstadoNot(1L, EstadoPedido.ENTREGADO)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.deleteOrDeactivate(1L))
                .isInstanceOf(IllegalStateException.class);

        Assertions.assertThat(cliente.isActivo()).isTrue();
        verify(repo, never()).save(any(Cliente.class));
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    public void ClienteService_deleteOrDeactivate_ThrowsWhenClienteDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.deleteOrDeactivate(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).deleteById(anyLong());
    }
}
