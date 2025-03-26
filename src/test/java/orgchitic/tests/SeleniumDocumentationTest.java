package orgchitic.tests;

// Importación de bibliotecas necesarias
import io.github.bonigarcia.wdm.WebDriverManager;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class SeleniumDocumentationTest {
    // Declaración de variables necesarias
    private WebDriver driver;          // Controlador de Selenium
    private WebDriverWait wait;        // Espera explícita para elementos
    private ScreenRecorder screenRecorder;  // Grabador de pantalla

    @BeforeClass
    public void setup() {
        // Configuración antes de la ejecución de los tests
        try {
            // Configuración de la grabación de la pantalla
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            File movieDir = new File("videos");

            // Definir el formato del archivo de video (AVI en este caso)
            Format fileFormat = new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI);
            Format screenFormat = new Format(MediaTypeKey, MediaType.VIDEO,
                    EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    DepthKey, 24,
                    FrameRateKey, new Rational(15, 1),
                    QualityKey, 1.0f,
                    KeyFrameIntervalKey, 15 * 60);
            Format mouseFormat = new Format(MediaTypeKey, MediaType.VIDEO,
                    EncodingKey, "black",
                    FrameRateKey, new Rational(30, 1));
            Format audioFormat = null;  // Sin audio en este caso

            // Inicialización del grabador de pantalla
            screenRecorder = new MyScreenRecorder(gc,
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()),
                    fileFormat, screenFormat, mouseFormat, audioFormat, movieDir);
            screenRecorder.start();  // Comienza la grabación
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuración de Selenium con WebDriverManager para gestionar el driver del navegador
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();  // Inicialización del controlador para Chrome
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // Configuración de la espera
        driver.manage().window().maximize();  // Maximizar la ventana del navegador
    }

    @Test
    public void testSeleniumDocumentation() {
        // Test para buscar la documentación de Selenium en Google
        driver.get("https://www.google.com");  // Navegar a Google
        waitForPageLoad();  // Esperar a que la página se cargue completamente
        takeScreenshot("01_google_home");  // Tomar captura de pantalla

        // Buscar "Documentación de Selenium" en Google
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        searchBox.sendKeys("Documentación de Selenium" + Keys.ENTER);
        waitForPageLoad();  // Esperar a que los resultados de búsqueda carguen
        takeScreenshot("02_google_results");  // Tomar captura de pantalla de los resultados

        // Hacer clic en el enlace de Selenium
        WebElement seleniumLink = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Selenium")));
        try {
            seleniumLink.click();
        } catch (ElementClickInterceptedException e) {
            // Si el clic es interceptado, usar JavaScript para realizar el clic
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", seleniumLink);
        }
        waitForPageLoad();  // Esperar a que se cargue la página de documentación de Selenium
        takeScreenshot("03_selenium_documentation");  // Tomar captura de pantalla de la documentación

        // Navegar a través de los elementos del menú lateral
        String[] menuItems = {"Overview", "WebDriver", "Selenium Manager", "Grid", "IE Driver Server", "IDE", "Test Practices", "Legacy", "About"};
        for (String item : menuItems) {
            try {
                WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(item)));
                menuItem.click();  // Hacer clic en cada elemento del menú
                waitForPageLoad();  // Esperar a que la página se cargue
                takeScreenshot("success_" + item.replace(" ", "_"));  // Captura si se hizo clic con éxito
            } catch (Exception e) {
                // Si no se encuentra el elemento, imprimir un error y tomar captura de pantalla
                System.err.println("No se encontró el elemento del menú: " + item);
                takeScreenshot("error_" + item.replace(" ", "_"));
            }
        }
    }

    private void takeScreenshot(String filename) {
        // Método para tomar capturas de pantalla y guardarlas
        if (driver != null) {
            try {
                // Tomar captura de pantalla y guardar en un archivo
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.createDirectories(Paths.get("screenshots"));  // Crear directorio si no existe
                Files.copy(srcFile.toPath(), Paths.get("screenshots/" + filename + ".png"), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Captura guardada: " + filename);  // Imprimir mensaje de éxito
            } catch (WebDriverException e) {
                System.err.println("❌ No se pudo tomar la captura: " + filename);  // Si no se pudo tomar la captura
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForPageLoad() {
        // Método para esperar a que la página termine de cargar
        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                    .equals("complete"));
        } catch (TimeoutException e) {
            System.err.println("⚠️ La página tardó demasiado en cargar.");
        }
    }

    @AfterClass
    public void tearDown() {
        // Configuración después de la ejecución de los tests
        if (driver != null) {
            driver.quit();  // Cerrar el navegador
        }
        // Finalizar la grabación de la pantalla
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();  // Detener la grabación
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
