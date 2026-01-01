package com.wms.e2e;

import com.wms.entity.User;
import com.wms.enums.Role;
import com.wms.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginE2ETest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    static void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Bekleme süresi artırıldı
    }

    @BeforeEach
    void setupTestData() {
        // Her testten önce admin kullanıcısını sil ve tekrar ekle
        userRepository.findByEmail("admin@wms.com").ifPresent(userRepository::delete);
        User admin = new User();
        admin.setEmail("admin@wms.com");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setFullName("Test Admin");
        admin.setActive(true);
        userRepository.save(admin);
        userRepository.flush(); // Değişiklikler hemen veritabanına yazılsın
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    void testLoginPage_Loads() {
        driver.get("http://localhost:" + port + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        assertNotNull(emailInput);
        assertNotNull(passwordInput);
        assertNotNull(loginButton);
        String title = driver.getTitle();
        assertNotNull(title);
        assertTrue(title.contains("Login"));
    }

    @Test
    @Order(2)
    void testLogin_Success_AdminRedirectsToAdminDashboard() throws InterruptedException {
        driver.get("http://localhost:" + port + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailInput.sendKeys("admin@wms.com");
        passwordInput.sendKeys("Admin123!");
        loginButton.click();

        // Önce localStorage'da token oluştu mu kontrol et
        Boolean tokenExists = wait.until(driver -> {
            Object token = ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return window.localStorage.getItem('token');");
            return token != null && !token.toString().isEmpty();
        });
        assertTrue(tokenExists);

        // Yönlendirme için kısa bir manuel bekleme ekle
        Thread.sleep(1500);

        // Ardından yönlendirme için url kontrolü
        String url = driver.getCurrentUrl();
        assertNotNull(url);
        assertTrue(url.contains("/admin"));
    }

    @Test
    @Order(3)
    void testLogin_InvalidCredentials_ShowsError() {
        driver.get("http://localhost:" + port + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailInput.sendKeys("invalid@test.com");
        passwordInput.sendKeys("wrongpassword");
        loginButton.click();

        // Hata mesajının görünür olmasını bekle
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertNotNull(errorMessage);
        assertTrue(errorMessage.isDisplayed(), "Hata mesajı görünür değil!");
        String errorText = errorMessage.getText();
        assertNotNull(errorText);
        assertFalse(errorText.isBlank());
        // Hata mesajı içeriği kontrolü (daha esnek)
        String errorTextLower = errorText.toLowerCase();
        assertTrue(
                errorTextLower.contains("login") ||
                        errorTextLower.contains("error") ||
                        errorTextLower.contains("failed") ||
                        errorTextLower.contains("credentials") ||
                        errorTextLower.contains("invalid") ||
                        errorTextLower.contains("password") ||
                        errorTextLower.contains("email"),
                "Hata mesajı beklenen anahtar kelimeleri içermiyor: " + errorText
        );
    }
}