package org.swaglabs.tests;

import org.swaglabs.base.BaseTest;
import org.swaglabs.pages.LoginPage;
import org.swaglabs.pages.ProductPage;
import org.swaglabs.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseTest {

    private static final Logger logger = LoggerManager.logger(LoginPageTest.class);

    private LoginPage loginPage;
    private ProductPage productPage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        productPage = new ProductPage(getDriver());
    }

    @Test
    public void verifyValidLogin() {
        logger.info("Performing login");
        String username = "standard_user";
        String password = "secret_sauce";

        loginPage.performLogin(username, password);

        // Validate redirection to product catalog
        logger.info("Validating redirection to product catalog");
        boolean isCatalogDisplayed = productPage.isProductPageTitleDisplayed();
        Assert.assertTrue(isCatalogDisplayed, "Product catalog is not displayed after login");
    }

    @Test
    public void verifyInvalidLogin() {
        String username = "invalid_user";
        String password = "wrong_password";
        logger.info("Performing login with invalid credentials");

        loginPage.performLogin(username, password);

        boolean isErrorMessageDisplayed = loginPage.isErrorMessageDisplayed();
        Assert.assertTrue(isErrorMessageDisplayed, "Error message is not displayed");

        logger.info("Validating error message");
        String actualError = loginPage.getErrorMessage();
        Assert.assertEquals(actualError, "Username and password do not match any user in this service.", "Error message does not match expected");
    }
}
