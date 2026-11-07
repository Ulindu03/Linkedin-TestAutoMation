
package com.linkedin.automation.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.linkedin.automation.pages.HomePage;
import com.linkedin.automation.pages.LoginPage;

public class LinkedInAutomatedTests extends BaseTest {

    private String testUser() {
        return requireCredential("LINKEDIN_TEST_USER", "linkedin.test.user");
    }

    private String testPass() {
        return requireCredential("LINKEDIN_TEST_PASSWORD", "linkedin.test.password");
    }

    private static String requireCredential(String environmentName, String propertyName) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = System.getProperty(propertyName);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Missing LinkedIn credential. Configure " + environmentName +
                " or pass -D" + propertyName + " before running the tests."
            );
        }
        return value;
    }

    @Test(priority = 1, description = "Task 2: Positive Login Test")
    public void testSuccessfulLogin() {
        driver.get("https://www.linkedin.com/login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(testUser(), testPass());

        // Wait up to 60s for the feed URL and the top navigation bar to visibly load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        wait.until(ExpectedConditions.urlContains("feed"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("header, nav, #global-nav")));

        Assert.assertTrue(driver.getCurrentUrl().contains("feed"), "Login failed or did not load feed.");
    }

    @Test(priority = 2, description = "Task 2: Negative Login Test")
    public void testInvalidLogin() {
        driver.get("https://www.linkedin.com/login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(testUser(), "WrongPassword123!");

        String errorMsg = loginPage.getErrorMessage();
        System.out.println("Captured Error Message: " + errorMsg);
        Assert.assertFalse(errorMsg.isEmpty(), "Expected error message was not displayed.");
    }

    @Test(priority = 3, description = "Task 3: Automate Search")
    public void testAutomateSearch() {
        driver.get("https://www.linkedin.com/login");
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        // 1. Log in and wait for feed
        loginPage.login(testUser(), testPass());
        WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(60));
        loginWait.until(ExpectedConditions.urlContains("feed"));

        // 2. Search for "software testing"
        homePage.searchFor("software testing");

        // 3. Verify search results URL and at least one visible result element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("search/results"));
        Assert.assertTrue(driver.getCurrentUrl().contains("search/results"), "Search results URL did not load.");
        Assert.assertTrue(homePage.isResultDisplayed(), "No visible search result element found.");
    }

    @Test(priority = 4, description = "Task 4: Click a Search Result")
    public void testClickSearchResult() {
        driver.get("https://www.linkedin.com/login");
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        // 1. Log in and perform search
        loginPage.login(testUser(), testPass());
        WebDriverWait loginWait = new WebDriverWait(driver, Duration.ofSeconds(60));
        loginWait.until(ExpectedConditions.urlContains("feed"));

        homePage.searchFor("software testing");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("search/results"));
        String searchPageUrl = driver.getCurrentUrl();

        // 2. Click the first result
        homePage.clickFirstResult();

        // 3. Verify that the new profile/company/result page opened and rendered its main heading
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(searchPageUrl)));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1, h2, .pv-top-card")));
        Assert.assertNotEquals(driver.getCurrentUrl(), searchPageUrl, "Failed to navigate to clicked search result.");
    }
}