package orgchitic.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class SeleniumDocumentationTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testSeleniumDocumentation() {
        driver.get("https://www.google.com");
        waitForPageLoad();
        takeScreenshot("01_google_home");

        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        searchBox.sendKeys("Documentación de Selenium" + Keys.ENTER);
        waitForPageLoad();
        takeScreenshot("02_google_results");

        WebElement seleniumLink = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Selenium")));
        try {
            seleniumLink.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", seleniumLink);
        }
        waitForPageLoad();
        takeScreenshot("03_selenium_documentation");

        String[] menuItems = {"Overview", "WebDriver", "Selenium Manager", "Grid", "IE Driver Server", "IDE", "Test Practices", "Legacy", "About"};
        for (String item : menuItems) {
            try {
                WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(item)));
                menuItem.click();
                waitForPageLoad();
                takeScreenshot("success_" + item.replace(" ", "_"));
            } catch (Exception e) {
                System.err.println("No se encontró el elemento del menú: " + item);
                takeScreenshot("error_" + item.replace(" ", "_"));
            }
        }
    }

    private void takeScreenshot(String filename) {
        if (driver != null) {
            try {
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.createDirectories(Paths.get("screenshots"));
                Files.copy(srcFile.toPath(), Paths.get("screenshots/" + filename + ".png"), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Captura guardada: " + filename);
            } catch (WebDriverException e) {
                System.err.println("❌ No se pudo tomar la captura: " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForPageLoad() {
        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        } catch (TimeoutException e) {
            System.err.println("⚠️ La página tardó demasiado en cargar.");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
