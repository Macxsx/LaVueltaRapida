package com.example.demo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.config.MercadoPagoConfig;
import com.example.demo.dto.CardPaymentRequest;
import com.example.demo.dto.MpPreferenceRequest;
import com.example.demo.entitys.Comida;
import com.example.demo.entitys.EstadoPedido;
import com.example.demo.entitys.LineaPedido;
import com.example.demo.entitys.MetodoPago;
import com.example.demo.entitys.Pedido;
import com.example.demo.repository.PedidoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MercadoPagoServiceTestMock {

    private MercadoPagoServiceImpl service;
    private MercadoPagoConfig mpConfig;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private PreferenceClient preferenceClient;

    @Mock
    private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        mpConfig = new MercadoPagoConfig();
        ReflectionTestUtils.setField(mpConfig, "frontendUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(mpConfig, "backendUrl", "http://localhost:8090");
        ReflectionTestUtils.setField(mpConfig, "webhookSecret", "");

        service = new MercadoPagoServiceImpl();
        ReflectionTestUtils.setField(service, "pedidoRepository", pedidoRepository);
        ReflectionTestUtils.setField(service, "pedidoService", pedidoService);
        ReflectionTestUtils.setField(service, "mpConfig", mpConfig);
        ReflectionTestUtils.setField(service, "preferenceClient", preferenceClient);
        ReflectionTestUtils.setField(service, "paymentClient", paymentClient);
    }

    // ── crearPreferencia ──────────────────────────────────────────────────────

    @Test
    public void MercadoPagoService_crearPreferencia_ThrowsWhenReqIsNull() {
        Assertions.assertThatThrownBy(() -> service.crearPreferencia(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_crearPreferencia_ThrowsWhenPedidoIdIsNull() {
        MpPreferenceRequest req = new MpPreferenceRequest();

        Assertions.assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_crearPreferencia_ThrowsWhenPedidoNotFound() throws Exception {
        when(pedidoRepository.findByIdConLineas(999L)).thenReturn(Optional.empty());

        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(999L);
        req.setOrigin("https://mi-app.com");

        Assertions.assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(NoSuchElementException.class);

        verify(preferenceClient, never()).create(any());
    }

    @Test
    public void MercadoPagoService_crearPreferencia_ThrowsWhenOriginBlankAndFallbackBlank() {
        ReflectionTestUtils.setField(mpConfig, "frontendUrl", "");

        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(1L);
        req.setOrigin("");

        Assertions.assertThatThrownBy(() -> service.crearPreferencia(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_crearPreferencia_SetsMpPreferenceIdAndMetodoPago() throws Exception {
        Pedido pedido = buildPedidoConLinea(1L);
        when(pedidoRepository.findByIdConLineas(1L)).thenReturn(Optional.of(pedido));

        Preference fakePreference = buildFakePreference("PREF-ABC", "https://mp.com/checkout", "https://sandbox.mp.com/checkout");
        when(preferenceClient.create(any())).thenReturn(fakePreference);

        MpPreferenceRequest req = new MpPreferenceRequest();
        req.setPedidoId(1L);
        req.setOrigin("https://mi-app.com");

        Map<String, Object> result = service.crearPreferencia(req);

        Assertions.assertThat(result.get("id")).isEqualTo("PREF-ABC");
        Assertions.assertThat(result.get("init_point")).isEqualTo("https://mp.com/checkout");
        Assertions.assertThat(result.get("sandbox_init_point")).isEqualTo("https://sandbox.mp.com/checkout");
        Assertions.assertThat(pedido.getMpPreferenceId()).isEqualTo("PREF-ABC");
        Assertions.assertThat(pedido.getMetodoPago()).isEqualTo(MetodoPago.MP_ONLINE);
        verify(pedidoRepository).save(pedido);
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
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenTokenIsBlank() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.setToken("   ");

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenTransactionAmountIsNull() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.setTransactionAmount(null);

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
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenPayerIsNull() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.setPayer(null);

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenPayerEmailIsBlank() {
        CardPaymentRequest req = buildCardPaymentRequest();
        req.getPayer().setEmail("   ");

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_ThrowsWhenMpReturns4xx() throws Exception {
        MPResponse fakeResp = new MPResponse(422, null, "{\"message\":\"bad request\"}");
        when(paymentClient.create(any(), any())).thenThrow(new MPApiException("bad request", fakeResp));

        Assertions.assertThatThrownBy(() -> service.procesarPagoConTarjeta(buildCardPaymentRequest(), "device-123", "192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_UpdatesPedidoWhenApproved() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(paymentClient.create(any(), any())).thenReturn(buildFakePayment(55555L, "approved", "visa", "credit_card"));

        CardPaymentRequest req = buildCardPaymentRequest();
        req.setPedidoId(1L);

        service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1");

        Assertions.assertThat(pedido.getEstadoPago()).isEqualTo("APROBADO");
        Assertions.assertThat(pedido.getMpPaymentId()).isEqualTo("55555");
        Assertions.assertThat(pedido.getMpPaymentMethod()).isEqualTo("visa");
        Assertions.assertThat(pedido.getMpPaymentType()).isEqualTo("credit_card");
        Assertions.assertThat(pedido.getMetodoPago()).isEqualTo(MetodoPago.TARJETA);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_DoesNotUpdatePedidoWhenNoPedidoId() throws Exception {
        when(paymentClient.create(any(), any())).thenReturn(buildFakePayment(55555L, "approved", "visa", "credit_card"));

        service.procesarPagoConTarjeta(buildCardPaymentRequest(), "device-123", "192.168.1.1");

        verify(pedidoRepository, never()).findById(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void MercadoPagoService_procesarPagoConTarjeta_MapsRejectedStatusToRechazado() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(paymentClient.create(any(), any())).thenReturn(buildFakePayment(55555L, "rejected", "visa", "credit_card"));

        CardPaymentRequest req = buildCardPaymentRequest();
        req.setPedidoId(1L);

        service.procesarPagoConTarjeta(req, "device-123", "192.168.1.1");

        Assertions.assertThat(pedido.getEstadoPago()).isEqualTo("RECHAZADO");
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

        Assertions.assertThatThrownBy(() -> service.consultarPago("123"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void MercadoPagoService_consultarPago_ReturnsMapWithExpectedFields() throws Exception {
        Payment fakePayment = buildFakePayment(123L, "approved", "visa", "credit_card");
        ReflectionTestUtils.setField(fakePayment, "currencyId", "COP");
        ReflectionTestUtils.setField(fakePayment, "externalReference", "1");
        when(paymentClient.get(anyLong())).thenReturn(fakePayment);

        Map<String, Object> result = service.consultarPago("123");

        Assertions.assertThat(result.get("id")).isEqualTo(123L);
        Assertions.assertThat(result.get("status")).isEqualTo("approved");
        Assertions.assertThat(result.get("payment_method_id")).isEqualTo("visa");
        Assertions.assertThat(result.get("payment_type_id")).isEqualTo("credit_card");
        Assertions.assertThat(result.get("currency_id")).isEqualTo("COP");
        Assertions.assertThat(result.get("external_reference")).isEqualTo("1");
    }

    // ── procesarWebhook ───────────────────────────────────────────────────────

    @Test
    public void MercadoPagoService_procesarWebhook_ReturnsSilentlyWhenDataIdIsNull() throws Exception {
        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, null, "payment", null));

        verify(paymentClient, never()).get(anyLong());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void MercadoPagoService_procesarWebhook_ReturnsSilentlyWhenSignatureInvalid() throws Exception {
        ReflectionTestUtils.setField(mpConfig, "webhookSecret", "mi_secreto_real");

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, "123456", "payment", null));

        verify(paymentClient, never()).get(anyLong());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void MercadoPagoService_procesarWebhook_ReturnsSilentlyWhenTypeIsNotPayment() throws Exception {
        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, "123456", "subscription", null));

        verify(paymentClient, never()).get(anyLong());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void MercadoPagoService_procesarWebhook_ReturnsSilentlyWhenDataIdIsNotNumeric() throws Exception {
        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, "no-es-numero", "payment", null));

        verify(paymentClient, never()).get(anyLong());
    }

    @Test
    public void MercadoPagoService_procesarWebhook_UpdatesPedidoWhenValid() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Payment fakePayment = buildFakePayment(77777L, "approved", "visa", "credit_card");
        ReflectionTestUtils.setField(fakePayment, "externalReference", "1");
        when(paymentClient.get(77777L)).thenReturn(fakePayment);

        service.procesarWebhook(null, null, "77777", "payment", null);

        Assertions.assertThat(pedido.getEstadoPago()).isEqualTo("APROBADO");
        Assertions.assertThat(pedido.getMpPaymentId()).isEqualTo("77777");
        Assertions.assertThat(pedido.getMpPaymentMethod()).isEqualTo("visa");
        Assertions.assertThat(pedido.getMetodoPago()).isEqualTo(MetodoPago.MP_ONLINE);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    public void MercadoPagoService_procesarWebhook_AbsorbsExceptions() throws Exception {
        when(paymentClient.get(anyLong())).thenThrow(new RuntimeException("conexión rechazada"));

        Assertions.assertThatNoException()
                .isThrownBy(() -> service.procesarWebhook(null, null, "77777", "payment", null));

        verify(pedidoRepository, never()).save(any());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Pedido buildPedidoConLinea(Long pedidoId) {
        Comida comida = new Comida();
        comida.setId(1L);
        comida.setName("Pizza Test");
        comida.setPrice(25000);

        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setFechaCreacion(LocalDateTime.now());

        LineaPedido linea = new LineaPedido(2, comida, null, pedido);
        pedido.getLineasPedido().add(linea);

        return pedido;
    }

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

    private Payment buildFakePayment(Long id, String status, String methodId, String typeId) {
        Payment p = new Payment();
        ReflectionTestUtils.setField(p, "id", id);
        ReflectionTestUtils.setField(p, "status", status);
        ReflectionTestUtils.setField(p, "statusDetail", "accredited");
        ReflectionTestUtils.setField(p, "paymentMethodId", methodId);
        ReflectionTestUtils.setField(p, "paymentTypeId", typeId);
        ReflectionTestUtils.setField(p, "transactionAmount", new BigDecimal("45000"));
        return p;
    }

    private Preference buildFakePreference(String id, String initPoint, String sandboxInitPoint) {
        Preference p = new Preference();
        ReflectionTestUtils.setField(p, "id", id);
        ReflectionTestUtils.setField(p, "initPoint", initPoint);
        ReflectionTestUtils.setField(p, "sandboxInitPoint", sandboxInitPoint);
        return p;
    }
}
