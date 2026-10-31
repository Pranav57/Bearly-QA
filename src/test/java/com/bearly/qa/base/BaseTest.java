package com.bearly.qa.base;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ChromeOptions opts = new ChromeOptions();
        if (Config.headless()) opts.addArguments("--headless=new");
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080",
                "--use-fake-ui-for-media-stream", "--use-fake-device-for-media-stream");
        if (Config.chromeBin() != null) opts.setBinary(Config.chromeBin());
        driver = new ChromeDriver(opts);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /** Saves a screenshot for every failed test, for attaching to the bug report. */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver == null) return;
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                Path dir = Path.of("target", "screenshots");
                Files.createDirectories(dir);
                Path src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
                Files.copy(src, dir.resolve(result.getName() + ".png"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
        } finally {
            driver.quit();
        }
    }
}
