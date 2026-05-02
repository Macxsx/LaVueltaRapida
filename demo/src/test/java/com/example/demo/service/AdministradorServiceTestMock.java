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

import com.example.demo.entitys.Administrador;
import com.example.demo.repository.AdministradorRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AdministradorServiceTestMock {

    @InjectMocks
    private AdministradorServiceImpl service;

    @Mock
    AdministradorRepository repo;


    @Test
    public void AdministradorService_findAll_ReturnsAllAdministradores() {

        when(repo.findAll()).thenReturn(List.of(
                new Administrador(1L, "admin1", "123"),
                new Administrador(2L, "admin2", "123"),
                new Administrador(3L, "admin3", "123"),
                new Administrador(4L, "admin4", "123"),
                new Administrador(5L, "admin5", "123")
        ));

        List<Administrador> administradores = service.findAll();

        Assertions.assertThat(administradores).isNotNull();
        Assertions.assertThat(administradores).hasSize(5);
    }

    @Test
    public void AdministradorService_findById_ReturnsAdministradorWhenExists() {
        Administrador admin = new Administrador(1L, "admin1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(admin));

        Optional<Administrador> result = service.findById(1L);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(1L);
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("admin1");
    }

    @Test
    public void AdministradorService_findById_ReturnsEmptyWhenNotExists() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Optional<Administrador> result = service.findById(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void AdministradorService_findByUsuario_ReturnsAdministradorWhenExists() {
        Administrador admin = new Administrador(1L, "admin1", "123");
        when(repo.findByUsuario("admin1")).thenReturn(Optional.of(admin));

        Optional<Administrador> result = service.findByUsuario("admin1");

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getUsuario()).isEqualTo("admin1");
    }

    @Test
    public void AdministradorService_findByUsuario_ReturnsEmptyWhenNotExists() {
        when(repo.findByUsuario("noexiste")).thenReturn(Optional.empty());

        Optional<Administrador> result = service.findByUsuario("noexiste");

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void AdministradorService_update_UpdatesUsuarioAndContrasena() {
        Administrador stored = new Administrador(1L, "admin1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("adminNuevo")).thenReturn(Optional.empty());
        when(repo.save(stored)).thenReturn(stored);

        Administrador result = service.update(1L, "adminNuevo", "456", "123");

        Assertions.assertThat(result.getUsuario()).isEqualTo("adminNuevo");
        Assertions.assertThat(result.getContrasena()).isEqualTo("456");
        verify(repo).save(stored);
    }

    @Test
    public void AdministradorService_update_KeepsOldContrasenaWhenNewIsBlank() {
        Administrador stored = new Administrador(1L, "admin1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("adminNuevo")).thenReturn(Optional.empty());
        when(repo.save(stored)).thenReturn(stored);

        Administrador result = service.update(1L, "adminNuevo", "   ", "123");

        Assertions.assertThat(result.getUsuario()).isEqualTo("adminNuevo");
        Assertions.assertThat(result.getContrasena()).isEqualTo("123");
    }

    @Test
    public void AdministradorService_update_AllowsSameUsuarioForSameAdmin() {
        Administrador stored = new Administrador(1L, "admin1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("admin1")).thenReturn(Optional.of(stored));
        when(repo.save(stored)).thenReturn(stored);

        Administrador result = service.update(1L, "admin1", "456", "123");

        Assertions.assertThat(result.getUsuario()).isEqualTo("admin1");
        Assertions.assertThat(result.getContrasena()).isEqualTo("456");
    }

    @Test
    public void AdministradorService_update_ThrowsWhenAdministradorDoesNotExist() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> service.update(999L, "x", "y", "z"))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).save(any(Administrador.class));
    }

    @Test
    public void AdministradorService_update_ThrowsWhenCurrentPasswordIsWrong() {
        Administrador stored = new Administrador(1L, "admin1", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));

        Assertions.assertThatThrownBy(
                () -> service.update(1L, "adminNuevo", "456", "incorrecta"))
                .isInstanceOf(SecurityException.class);

        verify(repo, never()).save(any(Administrador.class));
    }

    @Test
    public void AdministradorService_update_ThrowsWhenUsuarioAlreadyTaken() {
        Administrador stored = new Administrador(1L, "admin1", "123");
        Administrador otro    = new Administrador(2L, "admin2", "123");
        when(repo.findById(1L)).thenReturn(Optional.of(stored));
        when(repo.findByUsuario("admin2")).thenReturn(Optional.of(otro));

        Assertions.assertThatThrownBy(
                () -> service.update(1L, "admin2", "456", "123"))
                .isInstanceOf(IllegalStateException.class);

        verify(repo, never()).save(any(Administrador.class));
    }

    @Test
    public void AdministradorService_delete_DeletesWhenExists() {
        when(repo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    public void AdministradorService_delete_ThrowsWhenNotExists() {
        when(repo.existsById(999L)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(repo, never()).deleteById(anyLong());
    }
}
