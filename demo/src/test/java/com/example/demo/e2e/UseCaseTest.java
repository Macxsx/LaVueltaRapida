package com.example.demo.e2e;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UseCaseTest {

    private final String BASE_URL = "http://localhost:4200";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void init() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-extensions");
        //chromeOptions.addArguments("--headless");
        this.driver = new ChromeDriver(chromeOptions);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @Test
    public void adminRegistraProductoConAdicionalesTest() {

        // 1. Navegar al landing page
        driver.get(BASE_URL + "/");

        // 2. Click en "INICIAR SESIÓN" del header
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".header-btns .btn-outline")));
        driver.findElement(By.cssSelector(".header-btns .btn-outline")).click();

        // 3. Intentar login con credenciales incorrectas
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("usuario")));
        driver.findElement(By.id("usuario")).sendKeys("usuarioIncorrecto");
        driver.findElement(By.id("contrasena")).sendKeys("claveIncorrecta");
        driver.findElement(By.cssSelector(".btn-login")).click();

        // 4. Verificar que se muestra el mensaje de error de credenciales
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-error")));
        Assertions.assertThat(
                driver.findElement(By.cssSelector(".login-error")).isDisplayed()).isTrue();

        // 5. Ingresar correctamente con credenciales de admin1
        driver.findElement(By.id("usuario")).clear();
        driver.findElement(By.id("contrasena")).clear();
        driver.findElement(By.id("usuario")).sendKeys("admin1");
        driver.findElement(By.id("contrasena")).sendKeys("123");
        driver.findElement(By.cssSelector(".btn-login")).click();

        // 6. Verificar redirección al panel de administrador
        wait.until(ExpectedConditions.urlContains("/admin/comidas"));

        // 7. Ir a agregar nuevo producto
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();

        // 8. Llenar formulario del producto
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Pizza Velocidad E2E");
        driver.findElement(By.id("description")).sendKeys("Pizza de prueba para test automatizado");
        driver.findElement(By.id("price")).sendKeys("25000");

        // Seleccionar categoría "Clásicas" en el custom select
        driver.findElement(By.cssSelector(".cs-trigger")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cs-dropdown")));
        driver.findElements(By.cssSelector(".cs-option")).stream()
                .filter(opt -> opt.getText().equals("Clásicas"))
                .findFirst().get().click();

        driver.findElement(By.id("image")).sendKeys(
                "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg");
        driver.findElement(By.cssSelector(".btn-submit")).click();

        // 9. Confirmar regreso a la lista de productos
        wait.until(ExpectedConditions.urlContains("/admin/comidas"));

        // 10. Navegar a la sección de Adicionales desde el nav
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("ADICIONALES")));
        driver.findElement(By.linkText("ADICIONALES")).click();
        wait.until(ExpectedConditions.urlContains("/admin/adicionales"));

        // 11. Crear primer adicional asignado a la categoría "Clásicas"
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 1");
        driver.findElement(By.id("price")).sendKeys("2000");
        driver.findElements(By.cssSelector(".categoria-check")).stream()
                .filter(lbl -> lbl.getText().contains("Clásicas"))
                .findFirst().get().click();
        driver.findElement(By.cssSelector(".btn-submit")).click();

        // 12. Crear segundo adicional asignado a "Clásicas"
        wait.until(ExpectedConditions.urlContains("/admin/adicionales"));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 2");
        driver.findElement(By.id("price")).sendKeys("2500");
        driver.findElements(By.cssSelector(".categoria-check")).stream()
                .filter(lbl -> lbl.getText().contains("Clásicas"))
                .findFirst().get().click();
        driver.findElement(By.cssSelector(".btn-submit")).click();

        // 13. Abrir nueva pestaña y navegar a la sección de menú del cliente
        wait.until(ExpectedConditions.urlContains("/admin/adicionales"));
        String adminTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(BASE_URL + "/menu");

        // 14. Encontrar la pizza creada en las cards del menú y abrirla
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pizza-card")));
        driver.findElements(By.cssSelector(".pizza-card")).stream()
                .filter(card -> card.getText().contains("Pizza Velocidad E2E"))
                .findFirst().get().click();

        // 15. Verificar que los 2 adicionales aparecen en el detalle del producto
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-name")));
        List<String> adicionalesEnMenu = driver.findElements(By.cssSelector(".pd-option-name"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(adicionalesEnMenu)
                .contains("Adicional Prueba 1", "Adicional Prueba 2");

        // 16. Volver al portal de admin y agregar el tercer adicional que faltaba
        driver.switchTo().window(adminTab);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 3");
        driver.findElement(By.id("price")).sendKeys("3000");
        driver.findElements(By.cssSelector(".categoria-check")).stream()
                .filter(lbl -> lbl.getText().contains("Clásicas"))
                .findFirst().get().click();
        driver.findElement(By.cssSelector(".btn-submit")).click();

        // 17. Regresar a la pestaña del menú y verificar los 3 adicionales
        wait.until(ExpectedConditions.urlContains("/admin/adicionales"));
        String menuTab = driver.getWindowHandles().stream()
                .filter(h -> !h.equals(adminTab))
                .findFirst().get();
        driver.switchTo().window(menuTab);
        driver.get(BASE_URL + "/menu");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pizza-card")));
        driver.findElements(By.cssSelector(".pizza-card")).stream()
                .filter(card -> card.getText().contains("Pizza Velocidad E2E"))
                .findFirst().get().click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-name")));
        adicionalesEnMenu = driver.findElements(By.cssSelector(".pd-option-name"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(adicionalesEnMenu)
                .contains("Adicional Prueba 1", "Adicional Prueba 2", "Adicional Prueba 3");
    }

    @Test
    public void clienteRealizaPedidoYOperadorCompletaFlujoTest() {

        // ═══ USUARIO: Inicio de sesión ════════════════════════════════════════
        driver.get(BASE_URL + "/");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".header-btns .btn-outline")));
        driver.findElement(By.cssSelector(".header-btns .btn-outline")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("usuario")));
        driver.findElement(By.id("usuario")).sendKeys("pablo123");
        driver.findElement(By.id("contrasena")).sendKeys("123456");
        driver.findElement(By.cssSelector(".btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/menu"));

        // ═══ USUARIO: Agregar comida 1 con 2 adicionales ═════════════════════
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pizza-card")));
        List<WebElement> cards = driver.findElements(By.cssSelector(".pizza-card"));
        String nombreComida1 = cards.get(0).findElement(By.tagName("h4")).getText();
        cards.get(0).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-item")));
        List<WebElement> opciones1 = driver.findElements(By.cssSelector(".pd-option-item"));
        String adicional1a = opciones1.get(0).findElement(By.cssSelector(".pd-option-name")).getText();
        String adicional1b = opciones1.get(1).findElement(By.cssSelector(".pd-option-name")).getText();
        opciones1.get(0).click();
        opciones1.get(1).click();
        driver.findElement(By.cssSelector(".pd-btn-order")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".toast-notification.show")));

        // ═══ USUARIO: Agregar comida 2 con 2 adicionales ═════════════════════
        driver.get(BASE_URL + "/menu");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pizza-card")));
        cards = driver.findElements(By.cssSelector(".pizza-card"));
        String nombreComida2 = cards.get(1).findElement(By.tagName("h4")).getText();
        cards.get(1).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-item")));
        List<WebElement> opciones2 = driver.findElements(By.cssSelector(".pd-option-item"));
        String adicional2a = opciones2.get(0).findElement(By.cssSelector(".pd-option-name")).getText();
        String adicional2b = opciones2.get(1).findElement(By.cssSelector(".pd-option-name")).getText();
        opciones2.get(0).click();
        opciones2.get(1).click();
        driver.findElement(By.cssSelector(".pd-btn-order")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".toast-notification.show")));

        // ═══ USUARIO: Verificar carrito antes del pedido ═════════════════════
        driver.get(BASE_URL + "/carrito");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cart-table")));

        List<WebElement> filasCarrito = driver.findElements(By.cssSelector(".cart-table tbody tr"));
        Assertions.assertThat(filasCarrito.size()).isEqualTo(2);

        List<WebElement> tagsAdicionales = driver.findElements(By.cssSelector(".cart-adicional-tag"));
        Assertions.assertThat(tagsAdicionales.size()).isGreaterThanOrEqualTo(4);

        // Capturar total del carrito para luego compararlo (sin hardcodear)
        String totalCarritoTexto = driver.findElement(By.cssSelector(".cart-total-value")).getText();
        long totalEsperado = Long.parseLong(totalCarritoTexto.replaceAll("[^0-9]", ""));

        // ═══ USUARIO: Realizar pedido y confirmar con pago en efectivo ════════
        driver.findElement(By.cssSelector(".cart-btn-pedir")).click();
        wait.until(ExpectedConditions.urlContains("/pago/"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pg-metodo-card")));
        driver.findElements(By.cssSelector(".pg-metodo-card")).stream()
                .filter(btn -> btn.getText().contains("EFECTIVO"))
                .findFirst().get().click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pg-title-ok")));
        Assertions.assertThat(driver.findElement(By.cssSelector(".pg-title-ok")).getText())
                .isEqualTo("PEDIDO CONFIRMADO");

        // Capturar el ID del pedido confirmado
        String pedidoId = driver.findElements(By.cssSelector(".pg-state-sub strong"))
                .get(0).getText().replace("#", "").trim();

        // ═══ OPERADOR: Abrir nueva pestaña e iniciar sesión ══════════════════
        String userTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(BASE_URL + "/login");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("usuario")));
        driver.findElement(By.id("usuario")).sendKeys("op1");
        driver.findElement(By.id("contrasena")).sendKeys("123");
        driver.findElement(By.cssSelector(".btn-login")).click();
        wait.until(ExpectedConditions.urlContains("/operador/inicio"));

        // ═══ OPERADOR: Abrir el pedido y cambiar estado a COCINANDO ══════════
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".fila-pedido")));
        driver.findElements(By.cssSelector(".fila-pedido")).stream()
                .filter(row -> row.findElement(By.cssSelector(".pedido-id")).getText()
                        .equals("#" + pedidoId))
                .findFirst().get().click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".detalle-panel")));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".dp-estado-action .cs-trigger")));
        driver.findElement(By.cssSelector(".dp-estado-action .cs-trigger")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cs-dropdown")));
        driver.findElements(By.cssSelector(".cs-option")).stream()
                .filter(opt -> opt.getText().equals("COCINANDO"))
                .findFirst().get().click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".dp-meta .estado-badge"), "COCINANDO"));

        // ═══ USUARIO: Verificar cambio de estado en el perfil ════════════════
        driver.switchTo().window(userTab);
        driver.get(BASE_URL + "/perfil");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-pedido-item")));

        WebElement pedidoActivo = driver.findElements(By.cssSelector(".pf-pedido-item")).stream()
                .filter(item -> item.findElement(By.cssSelector(".pf-pedido-id")).getText()
                        .equals("Pedido #" + pedidoId))
                .findFirst().get();
        Assertions.assertThat(
                pedidoActivo.findElement(By.cssSelector(".pf-pedido-estado")).getText())
                .isEqualTo("COCINANDO");

        // ═══ OPERADOR: ENVIADO → Marcar pagado → ENTREGADO ═══════════════════
        String operadorTab = driver.getWindowHandles().stream()
                .filter(h -> !h.equals(userTab)).findFirst().get();
        driver.switchTo().window(operadorTab);

        driver.findElement(By.cssSelector(".dp-estado-action .cs-trigger")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cs-dropdown")));
        driver.findElements(By.cssSelector(".cs-option")).stream()
                .filter(opt -> opt.getText().equals("ENVIADO"))
                .findFirst().get().click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".dp-meta .estado-badge"), "ENVIADO"));

        // Marcar como pagado (habilita el estado ENTREGADO)
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-pagar-panel")));
        driver.findElement(By.cssSelector(".btn-pagar-panel")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn-pagar-panel")));

        driver.findElement(By.cssSelector(".dp-estado-action .cs-trigger")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cs-dropdown")));
        driver.findElements(By.cssSelector(".cs-option")).stream()
                .filter(opt -> opt.getText().equals("ENTREGADO"))
                .findFirst().get().click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".dp-meta .estado-badge"), "ENTREGADO"));

        // ═══ USUARIO: Verificar historial con productos, adicionales y total ══
        driver.switchTo().window(userTab);
        driver.get(BASE_URL + "/perfil");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-tabs")));

        // Ir a la pestaña "Historial"
        driver.findElements(By.cssSelector(".pf-tab")).get(1).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-pedidos-list")));

        // Expandir el pedido entregado
        WebElement pedidoHistorial = driver.findElements(By.cssSelector(".pf-pedido-item")).stream()
                .filter(item -> item.findElement(By.cssSelector(".pf-pedido-id")).getText()
                        .equals("Pedido #" + pedidoId))
                .findFirst().get();
        pedidoHistorial.findElement(By.cssSelector(".pf-pedido-summary")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-pedido-detalle")));

        // Verificar que los 2 productos estén en el pedido
        List<String> nombresProductos = driver.findElements(By.cssSelector(".pf-linea-nombre"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(nombresProductos).contains(nombreComida1, nombreComida2);

        // Verificar que los adicionales seleccionados estén en el pedido
        List<String> adicionalesEnHistorial = driver
                .findElements(By.cssSelector(".pf-linea-adicionales li"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional1a))).isTrue();
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional1b))).isTrue();
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional2a))).isTrue();
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional2b))).isTrue();

        // Verificar que el total coincide con el del carrito (sin hardcodear el valor)
        String totalHistorialTexto = driver.findElement(
                By.cssSelector(".pf-pedido-total-row span:last-child")).getText();
        long totalHistorial = Long.parseLong(totalHistorialTexto.replaceAll("[^0-9]", ""));
        Assertions.assertThat(totalHistorial).isEqualTo(totalEsperado);
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
