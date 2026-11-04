package com.linkedin.automation.tests;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            try {
                // Wait 3 seconds so LinkedIn finishes rendering images/text before capturing
                Thread.sleep(3000);

                Path folder = Paths.get("screenshots");
                Files.createDirectories(folder);
                String status = result.isSuccess() ? "PASS" : "FAIL";
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = folder.resolve(result.getName() + "_" + status + ".png");
                Files.copy(srcFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Screenshot saved: " + destination.toAbsolutePath());
            } catch (Exception e) {
                System.out.println("Failed to capture screenshot: " + e.getMessage());
            }
            driver.quit();
        }
    }
}