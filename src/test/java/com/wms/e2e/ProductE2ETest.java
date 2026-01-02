package com.wms.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String baseUrl;
    private static String seleniumUrl;

    @BeforeAll
    static void setUpDriver() throws MalformedURLException {
        baseUrl = System.getProperty("app.url", "http://localhost:8089");
        seleniumUrl = System.getProperty("selenium.remote.url", "http://localhost:4444");

        System.out.println("🌐 App URL: " + baseUrl);
        System.out.println("🔗 Selenium URL: " + seleniumUrl);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new RemoteWebDriver(
                new URL(seleniumUrl + "/wd/hub"),
                options
        );
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsAdmin() {
        driver.get(baseUrl + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));

        emailInput.sendKeys("admin@wms.com");
        passwordInput.sendKeys("Admin123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("/products")
        ));
    }

    @BeforeEach
    void login() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    void testProductPage_Loads() {
        driver.get(baseUrl + "/products");

        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("productsTable")));
        WebElement createButton = driver.findElement(By.id("createProductBtn"));

        assertNotNull(table);
        assertNotNull(createButton);
    }

    @Test
    @Order(2)
    void testCreateProduct_Success() {
        driver.get(baseUrl + "/products");

        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("createProductBtn")));
        createButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productModal")));
        assertTrue(modal.isDisplayed());

        driver.findElement(By.id("sku")).sendKeys("E2E-TEST-001");
        driver.findElement(By.id("name")).sendKeys("E2E Test Product");
        driver.findElement(By.id("unit")).sendKeys("PCS");
        driver.findElement(By.id("unitPrice")).sendKeys("99.99");
        driver.findElement(By.id("minStockLevel")).sendKeys("10");
        driver.findElement(By.id("category")).sendKeys("Test");

        driver.findElement(By.cssSelector("#productForm button[type='submit']")).click();

        wait.until(ExpectedConditions.invisibilityOf(modal));

        WebElement tbody = driver.findElement(By.id("productsBody"));
        assertTrue(tbody.getText().contains("E2E-TEST-001"));
    }

    @Test
    @Order(3)
    void testSearchProduct() {
        driver.get(baseUrl + "/products");

        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("searchInput")));
        searchInput.sendKeys("Laptop");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("productsBody"), "Laptop"));

        WebElement tbody = driver.findElement(By.id("productsBody"));
        assertTrue(tbody.getText().contains("Laptop"));
    }
}
