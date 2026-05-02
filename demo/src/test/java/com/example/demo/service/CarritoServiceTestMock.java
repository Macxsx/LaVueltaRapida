package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.example.demo.entitys.Adicional;
import com.example.demo.entitys.Carrito;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.LineaPedidoAdicional;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.LineaPedidoRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class CarritoServiceTestMock {

    @InjectMocks
    private CarritoServiceImpl service;

    @Mock
    CarritoRepository carritoRepository;

    @Mock
    LineaPedidoRepository lineaPedidoRepository;

    @Mock
    ComidaService comidaService;

    @Mock
    AdicionalService adicionalService;


    @Test
    public void CarritoService_findById_ReturnsCarritoWhenExists() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        Optional<Carrito> result = service.findById(1L);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    public void CarritoService_findById_ReturnsEmptyWhenNotExists() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Carrito> result = service.findById(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void CarritoService_findByClienteId_ReturnsCarritoWhenExists() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoRepository.findByClienteId(7L)).thenReturn(Optional.of(carrito));

        Optional<Carrito> result = service.findByClienteId(7L);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    public void CarritoService_findByClienteId_ReturnsEmptyWhenNotExists() {
        when(carritoRepository.findByClienteId(999L)).thenReturn(Optional.empty());

        Optional<Carrito> result = service.findByClienteId(999L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void CarritoService_save_DelegatesToRepository() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoRepository.save(carrito)).thenReturn(carrito);

        Carrito result = service.save(carrito);

        Assertions.assertThat(result).isSameAs(carrito);
        verify(carritoRepository).save(carrito);
    }


    @Test
    public void CarritoService_addProducto_CreatesNewLineaWhenCarritoIsEmpty() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        Comida comida = new Comida();
        comida.setId(10L);

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(comidaService.findById(10L)).thenReturn(comida);
        when(lineaPedidoRepository.findByCarritoIdAndComidaId(1L, 10L)).thenReturn(List.of());

        service.addProducto(1L, 10L, 2, null);

        ArgumentCaptor<LineaPedido> captor = ArgumentCaptor.forClass(LineaPedido.class);
        verify(lineaPedidoRepository).save(captor.capture());
        LineaPedido saved = captor.getValue();
        Assertions.assertThat(saved.getCantidad()).isEqualTo(2);
        Assertions.assertThat(saved.getComida()).isSameAs(comida);
        Assertions.assertThat(saved.getCarrito()).isSameAs(carrito);
        Assertions.assertThat(saved.getAdicionales()).isEmpty();
    }

    @Test
    public void CarritoService_addProducto_MergesWhenSameComidaAndSameAdicionales() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        Comida comida = new Comida();
        comida.setId(10L);
        Adicional adicional = new Adicional(5L, "Queso", 3000.0, true);

        LineaPedido existente = new LineaPedido(1, comida, carrito, null);
        existente.setId(100L);
        existente.getAdicionales().add(new LineaPedidoAdicional(existente, adicional));

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(comidaService.findById(10L)).thenReturn(comida);
        when(adicionalService.findById(5L)).thenReturn(adicional);
        when(lineaPedidoRepository.findByCarritoIdAndComidaId(1L, 10L))
                .thenReturn(List.of(existente));

        service.addProducto(1L, 10L, 3, List.of(5L));

        Assertions.assertThat(existente.getCantidad()).isEqualTo(4);
        verify(lineaPedidoRepository).save(existente);
        verify(lineaPedidoRepository, times(1)).save(any(LineaPedido.class));
    }

    @Test
    public void CarritoService_addProducto_CreatesSeparateLineaWhenAdicionalesDiffer() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        Comida comida = new Comida();
        comida.setId(10L);
        Adicional queso = new Adicional(5L, "Queso", 3000.0, true);
        Adicional jamon = new Adicional(6L, "Jamón", 3000.0, true);

        LineaPedido existente = new LineaPedido(1, comida, carrito, null);
        existente.setId(100L);
        existente.getAdicionales().add(new LineaPedidoAdicional(existente, queso));

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));
        when(comidaService.findById(10L)).thenReturn(comida);
        when(adicionalService.findById(6L)).thenReturn(jamon);
        when(lineaPedidoRepository.findByCarritoIdAndComidaId(1L, 10L))
                .thenReturn(List.of(existente));

        service.addProducto(1L, 10L, 1, List.of(6L));

        Assertions.assertThat(existente.getCantidad()).isEqualTo(1);
        ArgumentCaptor<LineaPedido> captor = ArgumentCaptor.forClass(LineaPedido.class);
        verify(lineaPedidoRepository).save(captor.capture());
        LineaPedido nueva = captor.getValue();
        Assertions.assertThat(nueva).isNotSameAs(existente);
        Assertions.assertThat(nueva.getAdicionales()).hasSize(1);
        Assertions.assertThat(nueva.getAdicionales().get(0).getAdicional()).isSameAs(jamon);
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenComidaIdIsNull() {
        Assertions.assertThatThrownBy(() -> service.addProducto(1L, null, 1, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(carritoRepository, never()).findById(anyLong());
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenCantidadIsNullOrNotPositive() {
        Assertions.assertThatThrownBy(() -> service.addProducto(1L, 10L, null, null))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> service.addProducto(1L, 10L, 0, null))
                .isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatThrownBy(() -> service.addProducto(1L, 10L, -3, null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(carritoRepository, never()).findById(anyLong());
    }

    @Test
    public void CarritoService_addProducto_ThrowsWhenCarritoDoesNotExist() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.addProducto(999L, 10L, 1, null))
                .isInstanceOf(NoSuchElementException.class);

        verify(lineaPedidoRepository, never()).save(any(LineaPedido.class));
    }


    @Test
    public void CarritoService_removeProducto_DeletesLineaWhenExistsInCarrito() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        Comida comida = new Comida();
        comida.setId(10L);
        LineaPedido linea = new LineaPedido(1, comida, carrito, null);
        linea.setId(100L);

        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(100L)).thenReturn(Optional.of(linea));
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        service.removeProducto(1L, 100L);

        verify(lineaPedidoRepository).deleteById(100L);
    }

    @Test
    public void CarritoService_removeProducto_ThrowsWhenCarritoDoesNotExist() {
        when(carritoRepository.existsById(999L)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> service.removeProducto(999L, 100L))
                .isInstanceOf(NoSuchElementException.class);

        verify(lineaPedidoRepository, never()).deleteById(anyLong());
    }

    @Test
    public void CarritoService_removeProducto_ThrowsWhenLineaDoesNotExist() {
        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.removeProducto(1L, 999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(lineaPedidoRepository, never()).deleteById(anyLong());
    }

    @Test
    public void CarritoService_removeProducto_ThrowsWhenLineaBelongsToAnotherCarrito() {
        Carrito otroCarrito = new Carrito();
        otroCarrito.setId(2L);
        LineaPedido linea = new LineaPedido(1, new Comida(), otroCarrito, null);
        linea.setId(100L);

        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(100L)).thenReturn(Optional.of(linea));

        Assertions.assertThatThrownBy(() -> service.removeProducto(1L, 100L))
                .isInstanceOf(NoSuchElementException.class);

        verify(lineaPedidoRepository, never()).deleteById(anyLong());
    }


    @Test
    public void CarritoService_aumentarCantidad_AddsOneToLinea() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        LineaPedido linea = new LineaPedido(2, new Comida(), carrito, null);
        linea.setId(100L);

        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(100L)).thenReturn(Optional.of(linea));
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        service.aumentarCantidad(1L, 100L);

        Assertions.assertThat(linea.getCantidad()).isEqualTo(3);
        verify(lineaPedidoRepository).save(linea);
        verify(lineaPedidoRepository, never()).deleteById(anyLong());
    }

    @Test
    public void CarritoService_disminuirCantidad_SubtractsOneFromLinea() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        LineaPedido linea = new LineaPedido(3, new Comida(), carrito, null);
        linea.setId(100L);

        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(100L)).thenReturn(Optional.of(linea));
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        service.disminuirCantidad(1L, 100L);

        Assertions.assertThat(linea.getCantidad()).isEqualTo(2);
        verify(lineaPedidoRepository).save(linea);
        verify(lineaPedidoRepository, never()).deleteById(anyLong());
    }

    @Test
    public void CarritoService_disminuirCantidad_DeletesLineaWhenReachesZero() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        LineaPedido linea = new LineaPedido(1, new Comida(), carrito, null);
        linea.setId(100L);

        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(100L)).thenReturn(Optional.of(linea));
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        service.disminuirCantidad(1L, 100L);

        verify(lineaPedidoRepository).deleteById(100L);
        verify(lineaPedidoRepository, never()).save(any(LineaPedido.class));
    }

    @Test
    public void CarritoService_aumentarCantidad_ThrowsWhenCarritoDoesNotExist() {
        when(carritoRepository.existsById(999L)).thenReturn(false);

        Assertions.assertThatThrownBy(() -> service.aumentarCantidad(999L, 100L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void CarritoService_aumentarCantidad_ThrowsWhenLineaBelongsToAnotherCarrito() {
        Carrito otro = new Carrito();
        otro.setId(2L);
        LineaPedido linea = new LineaPedido(1, new Comida(), otro, null);
        linea.setId(100L);

        when(carritoRepository.existsById(1L)).thenReturn(true);
        when(lineaPedidoRepository.findById(100L)).thenReturn(Optional.of(linea));

        Assertions.assertThatThrownBy(() -> service.aumentarCantidad(1L, 100L))
                .isInstanceOf(NoSuchElementException.class);
    }


    @Test
    public void CarritoService_vaciar_ClearsLineasWhenCarritoExists() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.getLineasPedido().add(new LineaPedido(1, new Comida(), carrito, null));
        carrito.getLineasPedido().add(new LineaPedido(2, new Comida(), carrito, null));

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        service.vaciar(1L);

        Assertions.assertThat(carrito.getLineasPedido()).isEmpty();
        verify(carritoRepository).save(carrito);
    }

    @Test
    public void CarritoService_vaciar_ThrowsWhenCarritoDoesNotExist() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.vaciar(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(carritoRepository, never()).save(any(Carrito.class));
    }
}
