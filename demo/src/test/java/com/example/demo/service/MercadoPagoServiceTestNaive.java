package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.demo.dto.CardPaymentRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.example.demo.entitys.MetodoPago;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.PedidoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class MercadoPagoServiceTestNaive {

    @Autowired
    private MercadoPagoService service;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private PreferenceClient preferenceClient;

    @MockitoBean
    private PaymentClient paymentClient;

    // ── crearPreferencia ──────────────────────────────────────────────────────

    @Test
    public void MercadoPagoService_crearPreferencia_ThrowsWhenPedidoIdIsNull() {
        MpPreferenceRequest req = new MpPreferenceRequest();

        Assertions.assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_crearPreferencia_ThrowsWhenPedidoNotFound() {
        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(999L);
        req.setOrigin("https://mi-app.com");

        Assertions.assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void MercadoPagoService_crearPreferencia_SetsMpPreferenceIdAndMetodoPagoEnPedido() throws Exception {
        Long pedidoId = pedidoService.findAll(0, 1).getContent().get(0).getId();

        Preference fakePreference = mock(Preference.class);
        when(fakePreference.getId()).thenReturn("PREF-ABC");
        when(fakePreference.getInitPoint()).thenReturn("https://mp.com/checkout");
        when(fakePreference.getSandboxInitPoint()).thenReturn("https://sandbox.mp.com/checkout");
        when(preferenceClient.create(any())).thenReturn(fakePreference);

        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(pedidoId);
        req.setOrigin("https://mi-app.com");

        service.crearPreferencia(req);

        Pedido pedidoActualizado = pedidoRepository.findById(pedidoId).get();
        Assertions.assertThat(pedidoActualizado.getMpPreferenceId()).isEqualTo("PREF-ABC");
        Assertions.assertThat(pedidoActualizado.getMetodoPago()).isEqualTo(MetodoPago.MP_ONLINE);
    }

    // ── procesarPagoConTarjeta ────────────────────────────────────────────────

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenTokenIsNull() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.setToken(null);

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenTransactionAmountIsZero() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.setTransactionAmount(BigDecimal.ZERO);

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenPaymentMethodIdIsNull() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.setPaymentMethodId(null);

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenPayerEmailIsNull() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.getPayer().setEmail(null);

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_UpdatesPedidoWhenApproved() throws Exception {
        Long pedidoId = pedidoService.findAll(0, 1).getContent().get(0).getId();

        Payment fakePayment = buildMockPayment(55555L, "approved", "visa", "credit_card");
        when(paymentClient.create(any())).thenReturn(fakePayment);
        when(paymentClient.create(any(), any())).thenReturn(fakePayment);

        CardPaymentRequest req = buildCardPaymentRequest();
        req.setPedidoId(pedidoId);

        service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1");

        Pedido pedidoActualizado = pedidoRepository.findById(pedidoId).get();
        Assertions.assertThat(pedidoActualizado.getEstadoPago()).isEqualTo("APROBADO");
        Assertions.assertThat(pedidoActualizado.getMpPaymentId()).isEqualTo("55555");
        Assertions.assertThat(pedidoActualizado.getMpPaymentMethod()).isEqualTo("visa");
        Assertions.assertThat(pedidoActualizado.getMetodoPago()).isEqualTo(MetodoPago.TARJETA);
    }

    // ── consultarPago ─────────────────────────────────────────────────────────

    @Test
    public void MercadoPagoService_consultarPago_ThrowsWhenPaymentIdIsNull() {
        Assertions.assertThatThrownBy(() -> service.consultarPago(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_consultarPago_ThrowsWhenPaymentIdIsBlank() {
        Assertions.assertThatThrownBy(() -> service.consultarPago("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_consultarPago_ThrowsWhenPaymentIdIsNotNumeric() {
        Assertions.assertThatThrownBy(() -> service.consultarPago("abc"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_consultarPago_ThrowsNoSuchElementWhenMpReturns404() throws Exception {
        MPResponse fakeResp = new MPResponse(404, null, "{\"message\":\"not found\"}");
        when(paymentClient.get(anyLong())).thenThrow(new MPApiException("not found", fakeResp));

        Assertions.assertThatThrownBy(() -> service.consultarPago("999"))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── procesarWebhook ───────────────────────────────────────────────────────

    @Test
    public void MercadoPagoService_procesarWebhook_ReturnsSilentlyWhenDataIdIsNull() throws Exception {
        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, null, "payment", null));

        verify(paymentClient, never()).get(anyLong());
    }

    @Test
    public void MercadoPagoService_procesarWebhook_ReturnsSilentlyWhenTypeIsNotPayment() throws Exception {
        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, "123456", "subscription", null));

        verify(paymentClient, never()).get(anyLong());
    }

    @Test
    public void MercadoPagoService_procesarWebhook_UpdatesPedidoWhenValid() throws Exception {
        Long pedidoId = pedidoService.findAll(0, 1).getContent().get(0).getId();

        Payment fakePayment = buildMockPayment(77777L, "approved", "visa", "credit_card");
        when(fakePayment.getExternalReference()).thenReturn(String.valueOf(pedidoId));
        when(paymentClient.get(77777L)).thenReturn(fakePayment);

        service.procesarWebhook(null, null, "77777", "payment", null);

        Pedido pedidoActualizado = pedidoRepository.findById(pedidoId).get();
        Assertions.assertThat(pedidoActualizado.getEstadoPago()).isEqualTo("APROBADO");
        Assertions.assertThat(pedidoActualizado.getMpPaymentId()).isEqualTo("77777");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private CardPaymentRequest buildCardPaymentRequest() {
        CardPaymentRequest req = new CardPaymentRequest();
        req.setToken("tok_test_123");
        req.setTransactionAmount(new BigDecimal("45000"));
        req.setPaymentMethodId("visa");
        req.setInstallments(1);
        CardPaymentRequest.CardPayerRequest payer = new CardPaymentRequest.CardPayerRequest();
        payer.setEmail("juan@example.com");
        req.setPayer(payer);
        return req;
    }

    private Payment buildMockPayment(Long id, String status, String methodId, String typeId) {
        Payment p = mock(Payment.class);
        when(p.getId()).thenReturn(id);
        when(p.getStatus()).thenReturn(status);
        when(p.getStatusDetail()).thenReturn("accredited");
        when(p.getPaymentMethodId()).thenReturn(methodId);
        when(p.getPaymentTypeId()).thenReturn(typeId);
        when(p.getTransactionAmount()).thenReturn(new BigDecimal("45000"));
        when(p.getDateApproved()).thenReturn(null);
        return p;
    }
}
