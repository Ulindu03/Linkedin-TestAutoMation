package com.linkedin.automation.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Matches the search input across LinkedIn's classic and updated navigation bars
    private By searchBox = By.cssSelector(
        "input.search-global-typeahead__input, input[placeholder*='Search'], " +
        "input[aria-label*='Search'], input[role='combobox'], input[data-test-id='search-input']"
    );
    // If the browser window collapses the search bar into a magnifying glass button
    private By searchIconButton = By.cssSelector(
        "button.search-global-typeahead__collapsed-search-button, button[aria-label*='Search']"
    );
    // Matches clickable search results (people, companies, jobs, groups, posts) inside the main results container
    private By searchResults = By.xpath(
        "//main//a[contains(@href,'/in/') or contains(@href,'/company/') or contains(@href,'/jobs/view/') or contains(@href,'/school/') or contains(@href,'/groups/') or contains(@class,'app-aware-link')]"
    );

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void searchFor(String query) {
        WebElement searchInput = wait.until(d -> {
            // Try finding the visible search input directly
            List<WebElement> inputs = d.findElements(searchBox);
            for (WebElement input : inputs) {
                try {
                    if (input.isDisplayed() && input.isEnabled()) {
                        return input;
                    }
                } catch (Exception ignored) {
                }
            }
            // If collapsed, click the magnifying glass button first
            List<WebElement> buttons = d.findElements(searchIconButton);
            for (WebElement btn : buttons) {
                try {
                    if (btn.isDisplayed()) {
                        btn.click();
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            return null;
        });

        searchInput.click();
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.sendKeys(Keys.ENTER);
    }

    // Verifies at least one visible result element is displayed (Task 3 requirement)
    public boolean isResultDisplayed() {
        WebElement firstResult = getFirstVisibleResult();
        return firstResult != null && firstResult.isDisplayed();
    }

    private WebElement getFirstVisibleResult() {
        return wait.until(d -> {
            List<WebElement> results = d.findElements(searchResults);
            for (WebElement res : results) {
                try {
                    String text = res.getText().trim();
                    String href = res.getAttribute("href");
                    if (res.isDisplayed() && !text.isEmpty() && href != null && !href.contains("/search/results/")) {
                        return res;
                    }
                } catch (Exception ignored) {
                }
            }
            return null;
        });
    }

    // Clicks the first visible search result (Task 4 requirement)
    public void clickFirstResult() {
        WebElement firstResult = getFirstVisibleResult();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstResult);
        try {
            firstResult.click();
        } catch (Exception e) {
            // Fallback if LinkedIn's sticky header overlaps the standard click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstResult);
        }
    }
}