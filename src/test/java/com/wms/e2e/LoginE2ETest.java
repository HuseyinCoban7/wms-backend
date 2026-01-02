package com.wms.e2e;

import com.wms.entity.User;
import com.wms.enums.Role;
import com.wms.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String baseUrl;
    private static String seleniumUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    static void setUpDriver() throws MalformedURLException {
        // Jenkinsfile'dan gelen system property'ler
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
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

        userRepository.saveAndFlush(admin);
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
        driver.get(baseUrl + "/login");

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
        driver.get(baseUrl + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailInput.sendKeys("admin@wms.com");
        passwordInput.sendKeys("Admin123!");
        loginButton.click();

        // localStorage token kontrolü
        Boolean tokenExists = wait.until(d -> {
            Object token = ((JavascriptExecutor) d)
                    .executeScript("return window.localStorage.getItem('token');");
            return token != null && !token.toString().isEmpty();
        });
        assertTrue(tokenExists, "localStorage'ta token bulunamadı!");

        Thread.sleep(1500); // kısa bir bekleme

        String url = driver.getCurrentUrl();
        assertNotNull(url);
        assertTrue(url.contains("/admin"), "Admin dashboard'a yönlenmedi. URL: " + url);
    }

    @Test
    @Order(3)
    void testLogin_InvalidCredentials_ShowsError() {
        driver.get(baseUrl + "/login");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        emailInput.sendKeys("invalid@test.com");
        passwordInput.sendKeys("wrongpassword");
        loginButton.click();

        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("message"))
        );
        assertNotNull(errorMessage);
        assertTrue(errorMessage.isDisplayed(), "Hata mesajı görünür değil!");

        String errorText = errorMessage.getText();
        assertNotNull(errorText);
        assertFalse(errorText.isBlank());

        String errorTextLower = errorText.toLowerCase();
        assertTrue(
                errorTextLower.contains("login") ||
                errorTextLower.contains("error") ||
                errorTextLower.contains("failed") ||
                errorTextLower.contains("credentials") ||
                errorTextLower.contains("invalid") ||
                errorTextLower.contains("password") ||
                errorTextLower.contains("email"),
                "Hata mesajı beklenen kelimeleri içermiyor: " + errorText
        );
    }
}
