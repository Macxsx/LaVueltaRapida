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
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web;
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

        // 1. Navegar al landing page
        driver.get(BASE_URL + "/");

        // 2. Click en "INICIAR SESIÓN" del header
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("loginBtn")));
        driver.findElement(By.id("loginBtn")).click();

        // 3. Intentar login con credenciales incorrectas
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario")));
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).sendKeys("usuarioIncorrecto");
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).sendKeys("claveIncorrecta");
        driver.findElement(By.id("IniciarSesionBtn")).click();

        // 4. Verificar que se muestra el mensaje de error de credenciales
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-error")));
        Assertions.assertThat(
                driver.findElement(By.className("login-error")).isDisplayed()).isTrue();

        // 5. Ingresar correctamente con credenciales de admin1
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).clear();
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).clear();
        driver.findElement(By.xpath("//app-input-icon-field[@id='usuario']//input")).sendKeys("admin1");
        driver.findElement(By.xpath("//app-input-icon-field[2]//input")).sendKeys("123");
        WebElement botonLogin = driver.findElement(By.id("IniciarSesionBtn"));

        Actions actions = new Actions(driver);

        actions.moveToElement(botonLogin).click().perform();
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
WebElement trigger = driver.findElement(
        By.cssSelector(".cs-trigger"));

JavascriptExecutor js = (JavascriptExecutor) driver;

// Abrir dropdown
js.executeScript("arguments[0].click();", trigger);

wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
        By.cssSelector(".cs-option"), 0));

List<WebElement> opciones = driver.findElements(
        By.cssSelector(".cs-option"));

for (WebElement opcion : opciones) {

    if (opcion.getText().trim().equals("Clásicas")) {

        js.executeScript("arguments[0].click();", opcion);

        break;
    }
}


        WebElement imageInput = driver.findElement(By.id("image"));

        actions = new Actions(driver);

        actions.moveToElement(imageInput).perform();

        imageInput.sendKeys(
        "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg"
        );

        WebElement submitBtn = driver.findElement(By.cssSelector(".btn-submit"));

        actions = new Actions(driver);

        actions.moveToElement(submitBtn).click().perform();

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
        WebElement checkbox = wait.until(
    ExpectedConditions.presenceOfElementLocated(
        By.xpath(
            "//label[contains(@class,'categoria-check') and .//span[contains(text(),'Clásicas')]]//input"
        )
    )
);

JavascriptExecutor js1 = (JavascriptExecutor) driver;

js1.executeScript("arguments[0].checked = true;", checkbox);
js1.executeScript("arguments[0].dispatchEvent(new Event('change'));", checkbox);
        
        WebElement submitAdicionalBtn = driver.findElement(By.cssSelector(".btn-submit"));

        actions = new Actions(driver);

        actions.moveToElement(submitAdicionalBtn).click().perform();
        driver.findElement(By.cssSelector(".btn-submit")).click();

        // 12. Crear segundo adicional asignado a "Clásicas"
        wait.until(ExpectedConditions.urlContains("/admin/adicionales?success=added"));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add-hero")));
        driver.findElement(By.cssSelector(".btn-add-hero")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys("Adicional Prueba 2");
        driver.findElement(By.id("price")).sendKeys("2500");
        WebElement checkbox1 = wait.until(
    ExpectedConditions.presenceOfElementLocated(
        By.xpath(
            "//label[contains(@class,'categoria-check') and .//span[contains(text(),'Clásicas')]]//input"
        )
    )
);

JavascriptExecutor js2 = (JavascriptExecutor) driver;

js2.executeScript("arguments[0].checked = true;", checkbox1);
js2.executeScript("arguments[0].dispatchEvent(new Event('change'));", checkbox1);
       WebElement submitAdicional2Btn = driver.findElement(By.cssSelector(".btn-submit"));

        actions = new Actions(driver);

        actions.moveToElement(submitAdicional2Btn).click().perform();

        // 13. Abrir nueva pestaña y navegar a la sección de menú del cliente
        wait.until(ExpectedConditions.urlContains("/admin/adicionales?success=added"));
        String adminTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(BASE_URL + "/menu");

        // 14. Encontrar la pizza creada en las cards del menú y abrirla
WebElement pizzaCard = driver.findElement(
    By.xpath("/html/body/app-root/app-menu/main/section[1]/div/app-cards[12]//a[contains(@class,'pizza-card')]")
);

JavascriptExecutor j = (JavascriptExecutor) driver;

j.executeScript(
    "arguments[0].scrollIntoView({block:'center'});",
    pizzaCard
);

j.executeScript("arguments[0].click();", pizzaCard);

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
        WebElement checkbox3 = wait.until(
    ExpectedConditions.presenceOfElementLocated(
        By.xpath(
            "//label[contains(@class,'categoria-check') and .//span[contains(text(),'Clásicas')]]//input"
        )
    )
);

JavascriptExecutor js3 = (JavascriptExecutor) driver;

js3.executeScript("arguments[0].checked = true;", checkbox3);
js3.executeScript("arguments[0].dispatchEvent(new Event('change'));", checkbox3);
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

    @AfterEach
    void tearDown() {

       // if (driver != null) {
         //   driver.quit();
       // }
    }
}