package com.wms.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductE2ETest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void login() {
        driver.get("http://localhost:" + port + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));

        emailInput.sendKeys("admin@wms.com");
        passwordInput.sendKeys("Admin123!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Admin için /admin, diğer roller için /products olabilir
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("/products")
        ));
    }

    @Test
    @Order(1)
    void testProductPage_Loads() {
        driver.get("http://localhost:" + port + "/products");

        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("productsTable")));
        WebElement createButton = driver.findElement(By.id("createProductBtn"));

        assertNotNull(table);
        assertNotNull(createButton);
    }

    @Test
    @Order(2)
    void testCreateProduct_Success() {
        driver.get("http://localhost:" + port + "/products");

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
        driver.get("http://localhost:" + port + "/products");

        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("searchInput")));
        searchInput.sendKeys("Laptop");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("productsBody"), "Laptop"));

        WebElement tbody = driver.findElement(By.id("productsBody"));
        assertTrue(tbody.getText().contains("Laptop"));
    }
}