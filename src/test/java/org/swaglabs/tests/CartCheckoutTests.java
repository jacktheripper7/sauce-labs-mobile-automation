package org.swaglabs.tests;

import org.swaglabs.base.BaseTest;
import org.swaglabs.pages.*;
import org.apache.logging.log4j.Logger;
import org.swaglabs.utilities.LoggerManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CartCheckoutTests extends BaseTest {
    private LoginPage loginPage;
    private ProductPage productPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private CheckoutOverviewPage checkoutOverviewPage;

    private static final Logger logger = LoggerManager.logger(CartCheckoutTests.class);

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        productPage = new ProductPage(getDriver());
        cartPage = new CartPage(getDriver());
        checkoutPage = new CheckoutPage(getDriver());
        checkoutOverviewPage = new CheckoutOverviewPage(getDriver());

        // Login before each test
        logger.info("Logging into the app with valid credentials");
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");
        loginPage.tapLogin();
        logger.info("Login successful, navigating to product catalog");
    }

    @Test
    public void testCartCheckoutFlow() {
        logger.info("Starting positive cart & checkout test");

        // Step 1: Add products
        productPage.addProductToCart("Sauce Labs Backpack");
        productPage.addProductToCart("Sauce Labs Bike Light");
        logger.info("Added 2 products to the cart");

        int cartCount = productPage.getCartBadgeCount();
        Assert.assertEquals(cartCount, 2, "Cart badge count mismatch!");
        logger.info("Cart badge count validated: {}", cartCount);

        // Step 2: Open cart & validate items
        productPage.openCart();
        List<String> cartItems = cartPage.getCartItemTitles();
        logger.info("Cart contains: {}", cartItems);
        Assert.assertTrue(cartItems.contains("Sauce Labs Backpack"));
        Assert.assertTrue(cartItems.contains("Sauce Labs Bike Light"));

        // Step 3: Checkout
        cartPage.proceedToCheckout();
        checkoutPage.enterUserInfo("John", "Doe", "12345");
        checkoutPage.continueToOverview();

        CheckoutOverviewPage.Totals totals = checkoutOverviewPage.verifyTotalsAndFinish();

        logger.info("Asserting totals in test");
        Assert.assertEquals(totals.total, totals.expectedTotal, 0.01, "Total amount mismatch!");
        Assert.assertTrue(totals.tax > 0, "Tax should be greater than 0");


        // Step 4: Place order
        Assert.assertTrue(checkoutPage.isOrderSuccessful(), "Order success message not found!");
        logger.info("Order placed successfully");

        // Step 5: Click on back home
        checkoutPage.clickBackHome();
        logger.info("Navigated back to product catalog");

        // Step 5: Validate cart empty
        productPage.openCart();
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart is not empty after order!");
        logger.info("Cart validated empty after successful order");
    }

    // 🔹 NEGATIVE FLOWS

    @Test
    public void testCheckoutWithEmptyCart() {
        logger.info("Starting negative test: Checkout with empty cart");

        productPage.openCart();
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty for this test");
        logger.info("Cart is confirmed empty");

        // Try proceeding
        cartPage.proceedToCheckout();
        Assert.assertTrue(cartPage.isCartEmpty(), "Checkout should not proceed with empty cart");

        // Validate error
        boolean isErrorDisplayed = checkoutPage.isCheckoutErrorDisplayed();
        Assert.assertTrue(isErrorDisplayed, "Error message should be displayed for empty cart");
        logger.warn("Checkout attempted with empty cart - validation successful");
    }

    @Test
    public void testCheckoutWithMissingUserInfo() {
        logger.info("Starting negative test: Checkout with missing user info");

        // Add one product
        productPage.addProductToCart("Sauce Labs Backpack");
        logger.info("Added one product to cart");

        productPage.openCart();
        cartPage.proceedToCheckout();

        // Leave fields empty & try to continue
        checkoutPage.continueToOverview();

        // Validate error
        boolean isErrorDisplayed = checkoutPage.isCheckoutErrorDisplayed();
        Assert.assertTrue(isErrorDisplayed, "First Name is required");
        logger.warn("Checkout failed as expected due to missing user info");
    }
}
