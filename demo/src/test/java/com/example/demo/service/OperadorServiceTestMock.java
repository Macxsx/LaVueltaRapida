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

import com.example.demo.entitys.Operador;
import com.example.demo.repository.OperadorRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OperadorServiceTestMock {

    @InjectMocks
    private OperadorServiceImpl service;

    @Mock
    OperadorRepository repo;


    @Test
    public void OperadorService_findAll_ReturnsAllOperadores() {
        when(repo.findAll()).thenReturn(List.of(
                new Operador(1L, "Op Uno",  "op1", "123"),
                new Operador(2L, "Op Dos",  "op2", "123"),
                new Operador(3L, "Op Tres", "op3", "123")
        ));

        List<Operador> result = service.findAll();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(3);
    }

    @Test
    public void OperadorService_findById_ReturnsOperadorWhenExists() {
        Operador op = new Operador(1L, "Op Uno", "op1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(op));

        Optional<Operador> result = service.findById(1L);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("op1");
    }

    @Test
    public void OperadorService_findById_ReturnsEmptyWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Optional<Operador> result = service.findById(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void OperadorService_findByUsuario_ReturnsOperadorWhenExists() {
        Operador op = new Operador(1L, "Op Uno", "op1", "123");
        when(repo.findByUsuario("op1")).thenReturn(Optional.of(op));

        Optional<Operador> result = service.findByUsuario("op1");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getNombre()).isEqualTo("Op Uno");
    }

    @Test
    public void OperadorService_findByUsuario_ReturnsEmptyWhenNotExists() {
        when(repo.findByUsuario("noexiste")).thenReturn(Optional.empty());

        Optional<Operador> result = service.findByUsuario("noexiste");

        Assertions.assertThat(result).isEmpty();
    }


    @Test
    public void OperadorService_create_SavesOperadorWhenUsuarioIsAvailable() {
        Operador nuevo = new Operador(null, "Op Nuevo", "opNuevo", "123");
        Operador saved = new Operador(10L,  "Op Nuevo", "opNuevo", "123");

        when(repo.findByUsuario("opNuevo")).thenReturn(Optional.empty());
        when(repo.save(nuevo)).thenReturn(saved);

        Operador result = service.create(nuevo);

        Assertions.assertThat(result).isSameAs(saved);
        verify(repo).save(nuevo);
    }

    @Test
    public void OperadorService_create_ThrowsWhenUsuarioAlreadyExists() {
        Operador nuevo = new Operador(null, "Op Nuevo", "op1", "123");
        Operador otro  = new Operador(1L,   "Op Uno",   "op1", "123");
        when(repo.findByUsuario("op1")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(() -> service.create(nuevo))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Operador.class));
    }


    @Test
    public void OperadorService_update_UpdatesAllFields() {
        Operador stored = new Operador(1L, "Op Uno", "op1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("opNuevo")).thenReturn(Optional.empty());
        when(repo.save(stored)).thenReturn(stored);

        Operador result = service.update(1L, "Op Nuevo", "opNuevo", "456", "123");

        Assertions.assertThat(result.getNombre()).isEqualTo("Op Nuevo");
        Assertions.assertThat(result.getUsuario()).isEqualTo("opNuevo");
        Assertions.assertThat(result.getContrasena()).isEqualTo("456");
    }

    @Test
    public void OperadorService_update_KeepsOldContrasenaWhenNewIsBlank() {
        Operador stored = new Operador(1L, "Op Uno", "op1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("opNuevo")).thenReturn(Optional.empty());
        when(repo.save(stored)).thenReturn(stored);

        Operador result = service.update(1L, "Op Nuevo", "opNuevo", "   ", "123");

        Assertions.assertThat(result.getContrasena()).isEqualTo("123");
    }

    @Test
    public void OperadorService_update_AllowsSameUsuarioForSameOperador() {
        Operador stored = new Operador(1L, "Op Uno", "op1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("op1")).thenReturn(Optional.of(stored));
        when(repo.save(stored)).thenReturn(stored);

        Operador result = service.update(1L, "Op Uno Mod", "op1", "456", "123");

        Assertions.assertThat(result.getNombre()).isEqualTo("Op Uno Mod");
        Assertions.assertThat(result.getContrasena()).isEqualTo("456");
    }

    @Test
    public void OperadorService_update_ThrowsWhenOperadorDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> service.update(999L, "n", "u", "p", null))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).save(any(Operador.class));
    }

    @Test
    public void OperadorService_update_ThrowsWhenCurrentPasswordIsWrong() {
        Operador stored = new Operador(1L, "Op Uno", "op1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));

        Assertions.assertThatThrownBy(
                () -> service.update(1L, "Op Uno", "op1", "456", "incorrecta"))
                .isInstanceOf(SecurityException.class);

        verify(repo, never()).save(any(Operador.class));
    }

    @Test
    public void OperadorService_update_ThrowsWhenUsuarioBelongsToAnother() {
        Operador stored = new Operador(1L, "Op Uno", "op1", "123");
        Operador otro   = new Operador(2L, "Op Dos", "op2", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("op2")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(
                () -> service.update(1L, "Op Uno", "op2", "456", "123"))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Operador.class));
    }


    @Test
    public void OperadorService_delete_DeletesWhenExists() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    public void OperadorService_delete_ThrowsWhenNotExists() {
        when(repo.existsById(999L)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).deleteById(anyLong());
    }
}
