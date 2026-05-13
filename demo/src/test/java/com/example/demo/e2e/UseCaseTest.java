package com.example.demo.e2e;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
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
        chromeOptions.addArguments("--start-maximized");

        // chromeOptions.addArguments("--headless");

        driver = new ChromeDriver(chromeOptions);

        wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

   

    @Test
    public void adminRegistraProductoConAdicionalesTest() {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);
        String xpathCheckbox = "//label[contains(@class,'categoria-check')"
                + " and .//span[contains(text(),'Clásicas')]]//input";
        String xpathPizza = "//a[contains(@class,'pizza-card') and contains(.,'Pizza Velocidad E2E')]";

        // 1. Landing page → login con credenciales incorrectas
        driver.get(BASE_URL + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("loginBtn")));
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario")));
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).sendKeys("usuarioIncorrecto");
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).sendKeys("claveIncorrecta");
        actions.moveToElement(driver.findElement(By.id("IniciarSesionBtn"))).click().perform();

        // 2. Verificar error de credenciales
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-error")));
        Assertions.assertThat(driver.findElement(By.className("login-error")).isDisplayed()).isTrue();

        // 3. Login correcto como admin1
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).clear();
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).clear();
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).sendKeys("admin1");
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).sendKeys("123");
        actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.id("IniciarSesionBtn"))).click().perform();
        wait.until(ExpectedConditions.urlContains("/admin/comidas"));

        // 4. Ir a agregar nuevo producto
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();

        // 5. Llenar formulario del producto
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Pizza Velocidad E2E");
        driver.findElement(By.id("description")).sendKeys("Pizza de prueba para test automatizado");
        driver.findElement(By.id("price")).sendKeys("25000");

        js.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".cs-trigger")));
        js.executeScript("arguments[0].click();", wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class,'cs-option') and normalize-space(.)='Clásicas']"))));

        driver.findElement(By.id("image")).sendKeys(
                "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg");
        actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.cssSelector(".btn-submit"))).click().perform();
        wait.until(ExpectedConditions.urlContains("/admin/comidas"));

        // 6. Navegar a Adicionales
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("ADICIONALES")));
        driver.findElement(By.linkText("ADICIONALES")).click();
        wait.until(ExpectedConditions.urlContains("/admin/adicionales"));

        // 7. Crear primer adicional para "Clásicas"
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 1");
        driver.findElement(By.id("price")).sendKeys("2000");
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathCheckbox)));
        js.executeScript("arguments[0].checked = true;", checkbox);
        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", checkbox);
        actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.cssSelector(".btn-submit"))).click().perform();

        // 8. Crear segundo adicional para "Clásicas"
        wait.until(ExpectedConditions.urlContains("/admin/adicionales?success=added"));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 2");
        driver.findElement(By.id("price")).sendKeys("2500");
        checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathCheckbox)));
        js.executeScript("arguments[0].checked = true;", checkbox);
        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", checkbox);
        actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.cssSelector(".btn-submit"))).click().perform();

        // 9. Abrir pestaña de menú y verificar los 2 adicionales
        wait.until(ExpectedConditions.urlContains("/admin/adicionales?success=added"));
        String adminTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(BASE_URL + "/menu");

        WebElement pizzaCard = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathPizza)));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", pizzaCard);
        js.executeScript("arguments[0].click();", pizzaCard);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-name")));
        List<String> adicionalesEnMenu = driver.findElements(By.cssSelector(".pd-option-name"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(adicionalesEnMenu).contains("Adicional Prueba 1", "Adicional Prueba 2");

        // 10. Volver al admin y agregar el tercer adicional
        driver.switchTo().window(adminTab);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 3");
        driver.findElement(By.id("price")).sendKeys("3000");
        checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathCheckbox)));
        js.executeScript("arguments[0].checked = true;", checkbox);
        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", checkbox);
        actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.cssSelector(".btn-submit"))).click().perform();

        // 11. Volver al menú y verificar los 3 adicionales
        wait.until(ExpectedConditions.urlContains("/admin/adicionales"));
        String menuTab = driver.getWindowHandles().stream()
                .filter(h -> !h.equals(adminTab)).findFirst().get();
        driver.switchTo().window(menuTab);
        driver.get(BASE_URL + "/menu");

        WebElement pizzaCard2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathPizza)));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", pizzaCard2);
        js.executeScript("arguments[0].click();", pizzaCard2);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-name")));
        adicionalesEnMenu = driver.findElements(By.cssSelector(".pd-option-name"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(adicionalesEnMenu)
                .contains("Adicional Prueba 1", "Adicional Prueba 2", "Adicional Prueba 3");
    }

    @Test
    public void clienteRealizaPedidoYOperadorCompletaFlujoTest() {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        // ═══ USUARIO: Inicio de sesión ════════════════════════════════════════
        driver.get(BASE_URL + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("loginBtn")));
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario")));
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).sendKeys("pablo123");
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).sendKeys("123456");

        WebElement botonLogin = driver.findElement(By.id("IniciarSesionBtn"));
        actions.moveToElement(botonLogin).click().perform();
        wait.until(ExpectedConditions.urlContains("/menu"));

        // ═══ USUARIO: Agregar comida 1 con 2 adicionales ═════════════════════
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pizza-card")));
        List<WebElement> cards = driver.findElements(By.cssSelector(".pizza-card"));
        String nombreComida1 = cards.get(0).findElement(By.tagName("h4")).getAttribute("textContent").trim();
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", cards.get(0));
        js.executeScript("arguments[0].click();", cards.get(0));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-item")));
        List<WebElement> opciones1 = driver.findElements(By.cssSelector(".pd-option-item"));
        String adicional1a = opciones1.get(0).findElement(By.cssSelector(".pd-option-name")).getText();
        String adicional1b = opciones1.get(1).findElement(By.cssSelector(".pd-option-name")).getText();
        js.executeScript("arguments[0].click();", opciones1.get(0));
        js.executeScript("arguments[0].click();", opciones1.get(1));

        WebElement btnAgregar1 = driver.findElement(By.cssSelector(".pd-btn-order"));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btnAgregar1);
        js.executeScript("arguments[0].click();", btnAgregar1);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-notification.show")));

        // ═══ USUARIO: Agregar comida 2 con 2 adicionales ═════════════════════
        driver.get(BASE_URL + "/menu");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pizza-card")));
        cards = driver.findElements(By.cssSelector(".pizza-card"));
        String nombreComida2 = cards.get(1).findElement(By.tagName("h4")).getAttribute("textContent").trim();
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", cards.get(1));
        js.executeScript("arguments[0].click();", cards.get(1));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pd-option-item")));
        List<WebElement> opciones2 = driver.findElements(By.cssSelector(".pd-option-item"));
        String adicional2a = opciones2.get(0).findElement(By.cssSelector(".pd-option-name")).getText();
        String adicional2b = opciones2.get(1).findElement(By.cssSelector(".pd-option-name")).getText();
        js.executeScript("arguments[0].click();", opciones2.get(0));
        js.executeScript("arguments[0].click();", opciones2.get(1));

        WebElement btnAgregar2 = driver.findElement(By.cssSelector(".pd-btn-order"));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btnAgregar2);
        js.executeScript("arguments[0].click();", btnAgregar2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-notification.show")));

        // ═══ USUARIO: Verificar carrito antes del pedido ═════════════════════
        driver.get(BASE_URL + "/carrito");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cart-table")));

        List<WebElement> filasCarrito = driver.findElements(By.cssSelector(".cart-table tbody tr"));
        Assertions.assertThat(filasCarrito.size()).isEqualTo(2);

        List<WebElement> tagsAdicionales = driver.findElements(By.cssSelector(".cart-adicional-tag"));
        Assertions.assertThat(tagsAdicionales.size()).isGreaterThanOrEqualTo(4);

        // Capturar total del carrito (sin hardcodear) para verificarlo al final
        String totalCarritoTexto = driver.findElement(By.cssSelector(".cart-total-value")).getText();
        long totalEsperado = Long.parseLong(totalCarritoTexto.replaceAll("[^0-9]", ""));

        // ═══ USUARIO: Realizar pedido y pagar con efectivo ═══════════════════
        WebElement btnPedir = driver.findElement(By.cssSelector(".cart-btn-pedir"));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btnPedir);
        js.executeScript("arguments[0].click();", btnPedir);
        wait.until(ExpectedConditions.urlContains("/pago/"));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pg-metodo-card")));
        WebElement btnEfectivo = driver.findElements(By.cssSelector(".pg-metodo-card")).stream()
                .filter(btn -> btn.getText().contains("EFECTIVO"))
                .findFirst().get();
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", btnEfectivo);
        js.executeScript("arguments[0].click();", btnEfectivo);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".pg-title-ok")));
        Assertions.assertThat(driver.findElement(By.cssSelector(".pg-title-ok")).getText())
                .isEqualTo("PEDIDO CONFIRMADO");

        // Capturar el ID del pedido confirmado
        String pedidoId = driver.findElements(By.cssSelector(".pg-state-sub strong"))
                .get(0).getText().replace("#", "").trim();

        // ═══ OPERADOR: Navegador independiente (localStorage aislado) ══════════
        ChromeOptions opOptions = new ChromeOptions();
        opOptions.addArguments("--disable-notifications");
        opOptions.addArguments("--disable-extensions");
        opOptions.addArguments("--start-maximized");
        WebDriver operadorDriver = new ChromeDriver(opOptions);
        WebDriverWait waitOp = new WebDriverWait(operadorDriver, Duration.ofSeconds(25));
        JavascriptExecutor jsOp = (JavascriptExecutor) operadorDriver;

        operadorDriver.get(BASE_URL + "/login");
        waitOp.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario")));
        operadorDriver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).sendKeys("op1");
        operadorDriver.findElement(By.xpath("//app-input-icon-field[2]//input")).sendKeys("123");
        WebElement botonLoginOp = operadorDriver.findElement(By.id("IniciarSesionBtn"));
        Actions actionsOp = new Actions(operadorDriver);
        actionsOp.moveToElement(botonLoginOp).click().perform();
        waitOp.until(ExpectedConditions.urlContains("/operador/inicio"));

        // ═══ OPERADOR: Cambiar estado a COCINANDO desde la tabla ════════════
        String xpathFila    = "//span[contains(@class,'pedido-id') and normalize-space(text())='#"
                + pedidoId + "']/ancestor::tr[contains(@class,'fila-pedido')]";
        String xpathTrigger = xpathFila + "//td[contains(@class,'accion-cell')]//button[contains(@class,'cs-trigger')]";
        String xpathBadge   = xpathFila + "//td[contains(@class,'estado-cell')]//span[contains(@class,'estado-badge')]";
        String xpathBtnPagar= xpathFila + "//button[contains(@class,'btn-pagar')]";

        waitOp.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathFila)));

        // → COCINANDO
        WebElement triggerCocinando = operadorDriver.findElement(By.xpath(xpathTrigger));
        jsOp.executeScript("arguments[0].scrollIntoView({block:'center'});", triggerCocinando);
        jsOp.executeScript("arguments[0].click();", triggerCocinando);
        WebElement optCocinando = waitOp.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class,'cs-option') and normalize-space(text())='COCINANDO']")));
        jsOp.executeScript("arguments[0].click();", optCocinando);
        waitOp.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpathBadge), "COCINANDO"));

        // ═══ USUARIO: Verificar cambio de estado en perfil ═══════════════════
        // driver conserva la sesión de pablo123 intacta (localStorage independiente)
        driver.get(BASE_URL + "/perfil");
        String xpathEstadoPerfil = "//span[contains(@class,'pf-pedido-id') and normalize-space(text())='Pedido #"
                + pedidoId + "']/ancestor::li//span[contains(@class,'pf-pedido-estado')]";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathEstadoPerfil)));
        Assertions.assertThat(driver.findElement(By.xpath(xpathEstadoPerfil)).getText())
                .isEqualTo("COCINANDO");

        // ═══ OPERADOR: ENVIADO → marcar pagado → ENTREGADO ═══════════════════
        // → ENVIADO
        WebElement triggerEnviado = operadorDriver.findElement(By.xpath(xpathTrigger));
        jsOp.executeScript("arguments[0].scrollIntoView({block:'center'});", triggerEnviado);
        jsOp.executeScript("arguments[0].click();", triggerEnviado);
        WebElement optEnviado = waitOp.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class,'cs-option') and normalize-space(text())='ENVIADO']")));
        jsOp.executeScript("arguments[0].click();", optEnviado);
        waitOp.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpathBadge), "ENVIADO"));

        // Marcar pago contra entrega (habilita ENTREGADO)
        WebElement btnPagar = waitOp.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathBtnPagar)));
        jsOp.executeScript("arguments[0].scrollIntoView({block:'center'});", btnPagar);
        jsOp.executeScript("arguments[0].click();", btnPagar);
        waitOp.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpathBtnPagar)));

        // → ENTREGADO
        WebElement triggerEntregado = operadorDriver.findElement(By.xpath(xpathTrigger));
        jsOp.executeScript("arguments[0].scrollIntoView({block:'center'});", triggerEntregado);
        jsOp.executeScript("arguments[0].click();", triggerEntregado);
        WebElement optEntregado = waitOp.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class,'cs-option') and normalize-space(text())='ENTREGADO']")));
        jsOp.executeScript("arguments[0].click();", optEntregado);
        // El callback de cambiarEstado llama triggerSuccess() → añade clase .visible al toast
        waitOp.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".op-toast.visible")));
        operadorDriver.quit();

        // ═══ USUARIO: Verificar historial con productos, adicionales y total ══
        // El fragmento #mis-pedidos hace que Angular haga scroll directo a la sección
        driver.get(BASE_URL + "/perfil#mis-pedidos");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-tabs")));

        // Ir a la pestaña "Historial"
        WebElement tabHistorial = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'pf-tab') and contains(.,'Historial')]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", tabHistorial);
        js.executeScript("arguments[0].click();", tabHistorial);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-pedidos-list")));

        // Expandir el pedido entregado
        String xpathPedidoHistorial = "//span[contains(@class,'pf-pedido-id') and normalize-space(text())='Pedido #"
                + pedidoId + "']/ancestor::li[contains(@class,'pf-pedido-item')]";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathPedidoHistorial)));
        WebElement pedidoHistorial = driver.findElement(By.xpath(xpathPedidoHistorial));
        WebElement summaryBtn = pedidoHistorial.findElement(By.cssSelector(".pf-pedido-summary"));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", summaryBtn);
        js.executeScript("arguments[0].click();", summaryBtn);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".pf-pedido-detalle")));

        // Verificar los 2 productos
        List<String> nombresProductos = driver.findElements(By.cssSelector(".pf-linea-nombre"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(nombresProductos).contains(nombreComida1, nombreComida2);

        // Verificar los 4 adicionales seleccionados
        List<String> adicionalesEnHistorial = driver
                .findElements(By.cssSelector(".pf-linea-adicionales li"))
                .stream().map(WebElement::getText).collect(Collectors.toList());
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional1a))).isTrue();
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional1b))).isTrue();
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional2a))).isTrue();
        Assertions.assertThat(adicionalesEnHistorial.stream().anyMatch(s -> s.contains(adicional2b))).isTrue();

        // Verificar total sin hardcodear: debe coincidir con el capturado del carrito
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