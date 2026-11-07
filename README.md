# LinkedIn UI Automation Test Suite (IT3153 - Assignment 04)

**Student:** Ulindu Chakranga Prabhashwara  
**Student ID:** D-BIS-24-0004

## Project Overview
This project is an automated UI test suite built with Java, Selenium WebDriver, and TestNG to automate core user flows on LinkedIn (Login, Search, and Result Navigation). It uses the Page Object Model (POM) design pattern and Explicit Waits to handle dynamic content, fulfilling the requirements for the IT3153 Software Quality Assurance module.

## Prerequisites
To run this project on your local machine, ensure you have the following installed:
* **Java Development Kit (JDK):** Version 25 (latest LTS) or higher
* **Maven:** For dependency management and test execution
* **Google Chrome:** The automated tests are configured to run on the Chrome browser

## Project Structure
The test suite utilizes the Page Object Model (POM) to separate element locators and actions from the test logic:
* `src/test/java/com/linkedin/automation/pages/`: Contains page classes (`LoginPage.java`, `HomePage.java`).
* `src/test/java/com/linkedin/automation/tests/`: Contains the test execution logic and WebDriver management (`BaseTest.java`, `LinkedInAutomatedTests.java`).

## How to Configure Credentials
Before running the tests, you must configure a valid LinkedIn test account to comply with LinkedIn's security policies. **Do not use a primary personal account.**

1. Configure environment variables in the same terminal used to run Maven:
   ```powershell
   $env:LINKEDIN_TEST_USER = "your-dedicated-test-email"
   $env:LINKEDIN_TEST_PASSWORD = "your-dedicated-test-password"
   ```
2. The tests read these values from `src/test/java/com/linkedin/automation/tests/LinkedInAutomatedTests.java`:
   ```java
   private final String testUser = requireCredential("LINKEDIN_TEST_USER");
   private final String testPass = requireCredential("LINKEDIN_TEST_PASSWORD");
   ```
3. Use your own dedicated LinkedIn test account. Never commit real credentials to the repository.

## How to Run the Tests
To execute the test suite from the terminal and generate reports, run the following Maven command from the root directory (where the `pom.xml` is located):

```bash
mvn clean test
```

## Important Note on Bot Detection (Ethics & Rules)
LinkedIn actively restricts automated access and frequently deploys anti-bot measures. In compliance with the assignment's ethics rules, this script does not attempt to bypass CAPTCHAs programmatically. 

Instead, the tests utilize a **60-second Explicit Wait** during the login phase. If LinkedIn presents an Email PIN verification or CAPTCHA, the browser execution will pause, allowing the user to manually solve the security check on the screen. Once solved and the feed loads, the automated test will seamlessly resume.

## Test Artifacts
* **Screenshots:** The `BaseTest.java` class is configured to automatically capture a screenshot at the end of every test (pass or fail). These are saved in the automatically generated `/screenshots/` directory.
* **TestNG Reports:** After execution, detailed HTML reports can be found in the `/target/surefire-reports/` directory.