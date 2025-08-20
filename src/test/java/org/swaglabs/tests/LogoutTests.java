package org.swaglabs.tests;


import org.swaglabs.base.BaseTest;
import org.swaglabs.pages.LoginPage;
import org.swaglabs.pages.MenuPage;
import org.swaglabs.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LogoutTests extends BaseTest {

    private LoginPage loginPage;
    private ProductPage productPage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        productPage = new ProductPage(getDriver());
    }

    @Test
    public void testLogoutFlow() {
        logger.info("Starting logout test");

        // Step 1: Login first
        loginPage.performLogin("standard_user", "secret_sauce");
        Assert.assertTrue(productPage.isProductListLoaded(), "Product list should be visible after login");
        logger.info("Login successful, product list loaded");

        // Step 2: Open menu & logout
        MenuPage menuPage = new MenuPage(getDriver());
        menuPage.openMenu();
        menuPage.logout();
        logger.info("Clicked logout from menu");

        // Step 3: Verify back to login screen
        Assert.assertTrue(menuPage.isAtLoginScreen(), "App should return to login screen after logout");
        logger.info("Logout successful, returned to login screen ✅");
    }
}
