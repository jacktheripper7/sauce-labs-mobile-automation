package org.swaglabs.pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.swaglabs.actiondriver.ActionDriver;

public class LoginPage {
    private final ActionDriver actionDriver;

    // Locators
    private final By usernameField = By.xpath("//android.widget.EditText[@content-desc='test-Username']");
    private final By passwordField = By.xpath("//android.widget.EditText[@content-desc='test-Password']");
    private final By loginButton   = By.xpath("//android.view.ViewGroup[@content-desc='test-LOGIN']");
    private final By errorMessage  = By.xpath("//android.view.ViewGroup[@content-desc='test-Error message']/android.widget.TextView");

    public LoginPage(AppiumDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    public void enterUsername(String username) {
        actionDriver.enterText(usernameField, username);
    }

    public void enterPassword(String password) {
        actionDriver.enterText(passwordField, password);
    }

    public void tapLogin() {
        actionDriver.click(loginButton);
    }

    public String getErrorMessage() {
        // Wait for error message to be visible before getting text
        return actionDriver.getText(errorMessage);
    }

    public boolean isErrorMessageDisplayed() {
        return actionDriver.isElementDisplayed(errorMessage);
    }

    public void performLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        tapLogin();
    }
}
