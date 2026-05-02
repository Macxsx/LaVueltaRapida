package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entitys.Domiciliario;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.repository.DomiciliarioRepository;
import com.example.demo.repository.PedidoRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DomiciliarioServiceTestMock {

    @InjectMocks
    private DomiciliarioServiceImpl service;

    @Mock
    DomiciliarioRepository repo;

    @Mock
    PedidoRepository pedidoRepository;


    @Test
    public void DomiciliarioService_findAll_ReturnsAllDomiciliarios() {
        when(repo.findAll()).thenReturn(List.of(
                new Domiciliario(1L, "Juan",  "111", "3001", true),
                new Domiciliario(2L, "Pedro", "222", "3002", true),
                new Domiciliario(3L, "Luis",  "333", "3003", false)
        ));

        List<Domiciliario> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(3);
    }

    @Test
    public void DomiciliarioService_findById_ReturnsDomiciliarioWhenExists() {
        Domiciliario d = new Domiciliario(1L, "Juan", "111", "3001", true);
        when(repo.findById(1L)).thenReturn(Optional.of(d));

        Optional<Domiciliario> result = service.findById(1L);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getNombre()).isEqualTo("Juan");
    }

    @Test
    public void DomiciliarioService_findById_ReturnsEmptyWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Optional<Domiciliario> result = service.findById(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void DomiciliarioService_save_DelegatesToRepository() {
        Domiciliario d = new Domiciliario(1L, "Juan", "111", "3001", true);
        when(repo.save(d)).thenReturn(d);

        Domiciliario result = service.save(d);

        Assertions.assertThat(result).isSameAs(d);
        verify(repo).save(d);
    }

    @Test
    public void DomiciliarioService_findFirstDisponible_ReturnsDomiciliarioWhenAvailable() {
        Domiciliario d = new Domiciliario(1L, "Juan", "111", "3001", true);
        when(repo.findFirstByDisponibleTrueOrderByIdAsc()).thenReturn(Optional.of(d));

        Optional<Domiciliario> result = service.findFirstDisponible();

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().isDisponible()).isTrue();
    }

    @Test
    public void DomiciliarioService_findFirstDisponible_ReturnsEmptyWhenNoneAvailable() {
        when(repo.findFirstByDisponibleTrueOrderByIdAsc()).thenReturn(Optional.empty());

        Optional<Domiciliario> result = service.findFirstDisponible();

        Assertions.assertThat(result).isEmpty();
    }


    @Test
    public void DomiciliarioService_create_SavesDomiciliarioAndClearsId() {
        Domiciliario nuevo = new Domiciliario(99L, "Juan", "111", "3001", true);
        Domiciliario saved = new Domiciliario(1L, "Juan", "111", "3001", true);

        when(repo.findByCedula("111")).thenReturn(Optional.empty());
        when(repo.findByCelular("3001")).thenReturn(Optional.empty());
        when(repo.save(nuevo)).thenReturn(saved);

        Domiciliario result = service.create(nuevo);

        Assertions.assertThat(result).isSameAs(saved);
        Assertions.assertThat(nuevo.getId()).isNull();
        verify(repo).save(nuevo);
    }

    @Test
    public void DomiciliarioService_create_ThrowsWhenCedulaAlreadyExists() {
        Domiciliario nuevo = new Domiciliario(null, "Juan", "111", "3001", true);
        Domiciliario otro  = new Domiciliario(2L, "Pedro", "111", "3002", true);
        when(repo.findByCedula("111")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(() -> service.create(nuevo))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Domiciliario.class));
    }

    @Test
    public void DomiciliarioService_create_ThrowsWhenCelularAlreadyExists() {
        Domiciliario nuevo = new Domiciliario(null, "Juan", "111", "3001", true);
        Domiciliario otro  = new Domiciliario(2L, "Pedro", "222", "3001", true);
        when(repo.findByCedula("111")).thenReturn(Optional.empty());
        when(repo.findByCelular("3001")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(() -> service.create(nuevo))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Domiciliario.class));
    }


    @Test
    public void DomiciliarioService_update_UpdatesAllFields() {
        Domiciliario stored = new Domiciliario(1L, "Juan", "111", "3001", true);
        Domiciliario data   = new Domiciliario(null, "Juanito", "222", "3002", false);

        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByCedula("222")).thenReturn(Optional.empty());
        when(repo.findByCelular("3002")).thenReturn(Optional.empty());
        when(repo.save(stored)).thenReturn(stored);

        Domiciliario result = service.update(1L, data);

        Assertions.assertThat(result.getNombre()).isEqualTo("Juanito");
        Assertions.assertThat(result.getCedula()).isEqualTo("222");
        Assertions.assertThat(result.getCelular()).isEqualTo("3002");
        Assertions.assertThat(result.isDisponible()).isFalse();
    }

    @Test
    public void DomiciliarioService_update_AllowsSameCedulaAndCelularForSameDomiciliario() {
        Domiciliario stored = new Domiciliario(1L, "Juan", "111", "3001", true);
        Domiciliario data   = new Domiciliario(null, "Juanito", "111", "3001", false);

        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByCedula("111")).thenReturn(Optional.of(stored));
        when(repo.findByCelular("3001")).thenReturn(Optional.of(stored));
        when(repo.save(stored)).thenReturn(stored);

        Domiciliario result = service.update(1L, data);

        Assertions.assertThat(result.getNombre()).isEqualTo("Juanito");
        Assertions.assertThat(result.isDisponible()).isFalse();
    }

    @Test
    public void DomiciliarioService_update_ThrowsWhenDomiciliarioDoesNotExist() {
        Domiciliario data = new Domiciliario(null, "X", "0", "0", true);
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(999L, data))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).save(any(Domiciliario.class));
    }

    @Test
    public void DomiciliarioService_update_ThrowsWhenCedulaBelongsToAnother() {
        Domiciliario stored = new Domiciliario(1L, "Juan",  "111", "3001", true);
        Domiciliario otro   = new Domiciliario(2L, "Pedro", "222", "3002", true);
        Domiciliario data   = new Domiciliario(null, "Juan", "222", "3001", true);

        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByCedula("222")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(() -> service.update(1L, data))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Domiciliario.class));
    }

    @Test
    public void DomiciliarioService_update_ThrowsWhenCelularBelongsToAnother() {
        Domiciliario stored = new Domiciliario(1L, "Juan",  "111", "3001", true);
        Domiciliario otro   = new Domiciliario(2L, "Pedro", "222", "3002", true);
        Domiciliario data   = new Domiciliario(null, "Juan", "111", "3002", true);

        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByCedula("111")).thenReturn(Optional.of(stored));
        when(repo.findByCelular("3002")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(() -> service.update(1L, data))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Domiciliario.class));
    }


    @Test
    public void DomiciliarioService_delete_DeletesWhenNoActivePedidos() {
        when(repo.existsById(1L)).thenReturn(true);
        when(pedidoRepository.existsByDomiciliarioIdAndEstadoNot(1L, EstadoPedido.ENTREGADO))
                .thenReturn(false);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    public void DomiciliarioService_delete_ThrowsWhenDomiciliarioDoesNotExist() {
        when(repo.existsById(999L)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    public void DomiciliarioService_delete_ThrowsWhenHasActivePedido() {
        when(repo.existsById(1L)).thenReturn(true);
        when(pedidoRepository.existsByDomiciliarioIdAndEstadoNot(1L, EstadoPedido.ENTREGADO))
                .thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).deleteById(anyLong());
    }
}
