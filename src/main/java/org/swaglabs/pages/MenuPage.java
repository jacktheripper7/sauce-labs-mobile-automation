package org.swaglabs.pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.swaglabs.actiondriver.ActionDriver;

public class MenuPage {

    private final ActionDriver actionDriver;

    // Locators
    private final By menuButton   = By.xpath("//android.view.ViewGroup[@content-desc='test-Menu']");
    private final By logoutButton = By.xpath("//android.view.ViewGroup[@content-desc='test-LOGOUT']");
    private final By loginButton  = By.xpath("//android.widget.TextView[@text='LOGIN']"); // appears on login screen

    public MenuPage(AppiumDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    // Actions
    public void openMenu() {
        actionDriver.click(menuButton);
    }

    public void logout() {
        actionDriver.click(logoutButton);
    }

    public boolean isAtLoginScreen() {
        return actionDriver.isElementDisplayed(loginButton);
    }
}
