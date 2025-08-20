package org.swaglabs.pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.swaglabs.actiondriver.ActionDriver;

public class CheckoutPage {
    private final ActionDriver actionDriver;

    // Locators
    private final By firstNameField = By.xpath("//android.widget.EditText[@content-desc='test-First Name']");
    private final By lastNameField = By.xpath("//android.widget.EditText[@content-desc='test-Last Name']");
    private final By zipField = By.xpath("//android.widget.EditText[@content-desc='test-Zip/Postal Code']");
    private final By continueButton = By.xpath("//android.view.ViewGroup[@content-desc='test-CONTINUE']");
    private final By successMessage = By.xpath("//android.widget.TextView[@text='THANK YOU FOR YOU ORDER']");
    private final By errorMessage = By.xpath("//android.view.ViewGroup[@content-desc='test-Error message']");
    private final By backHomeButton = By.xpath("//android.widget.TextView[@text='BACK HOME']");


    public CheckoutPage(AppiumDriver driver) {
        this.actionDriver = new ActionDriver(driver);

    }

    public void enterUserInfo(String firstName, String lastName, String zip) {
        actionDriver.enterText(firstNameField, firstName);
        actionDriver.enterText(lastNameField, lastName);
        actionDriver.enterText(zipField, zip);
    }

    public void continueToOverview() {
        actionDriver.click(continueButton);
    }



    public boolean isOrderSuccessful() {
        return actionDriver.isElementDisplayed(successMessage);
    }


    public boolean isCheckoutErrorDisplayed() {
        return actionDriver.isElementDisplayed(errorMessage);
    }

    public void clickBackHome() {
        actionDriver.click(backHomeButton);
    }
}
