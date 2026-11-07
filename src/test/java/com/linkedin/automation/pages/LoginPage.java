package com.linkedin.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By usernameField = By.cssSelector(
        "input#username, input[name='session_key'], input[autocomplete='username'], " +
        "input[data-test-id='username'], input[type='email'], input[type='text']"
    );
    private By passwordField = By.cssSelector(
        "input#password, input[name='session_password'], input[autocomplete='current-password'], " +
        "input[data-test-id='password'], input[type='password']"
    );
    private By loginButton = By.xpath(
        "//button[@type='submit' or normalize-space()='Sign in']"
    );
    // Broad XPath catching inline error messages across classic and updated LinkedIn layouts
    private By errorMessage = By.xpath(
        "//*[@id='error-for-password' or @id='error-for-username' or @role='alert' " +
        "or contains(@class,'error') or contains(@class,'feedback') " +
        "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'wrong') " +
        "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'not the right') " +
        "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'try again') " +
        "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'incorrect')]"
    );

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    private WebElement waitForVisibleElement(By locator) {
        return wait.until(d -> {
            List<WebElement> elements = d.findElements(locator);
            for (WebElement el : elements) {
                try {
                    if (el.isDisplayed() && el.isEnabled()) {
                        return el;
                    }
                } catch (Exception ignored) {
                }
            }
            return null;
        });
    }

    public void login(String username, String password) {
        WebElement userBox = waitForVisibleElement(usernameField);
        userBox.clear();
        userBox.sendKeys(username);

        WebElement passBox = waitForVisibleElement(passwordField);
        passBox.clear();
        passBox.sendKeys(password);

        WebElement signInBtn = waitForVisibleElement(loginButton);
        signInBtn.click();
    }

    public String getErrorMessage() {
        return wait.until(d -> {
            // 1. Check for visible inline error text on the login page
            List<WebElement> elements = d.findElements(errorMessage);
            for (WebElement el : elements) {
                try {
                    String text = el.getText().trim();
                    if (el.isDisplayed() && !text.isEmpty()) {
                        return text;
                    }
                } catch (Exception ignored) {
                }
            }
            // 2. Fallback if LinkedIn redirects invalid login attempts to a security checkpoint or login-submit page
            String currentUrl = d.getCurrentUrl();
            if (currentUrl.contains("checkpoint") || currentUrl.contains("login-submit")) {
                return "LinkedIn blocked invalid login via security checkpoint/redirect: " + currentUrl;
            }
            return null;
        });
    }
}