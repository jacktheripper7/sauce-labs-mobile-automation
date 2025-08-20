package org.swaglabs.pages;

import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.swaglabs.actiondriver.ActionDriver;
import org.swaglabs.utilities.LoggerManager;

import java.util.List;

public class CartPage {
    private final ActionDriver actionDriver;

    private static final Logger logger = LoggerManager.logger(CartPage.class);

    // Locators
    private final By cartItemsTitle = By.xpath("//android.view.ViewGroup[@content-desc='test-Item']/android.view.ViewGroup[2]/android.widget.TextView[1]");
    private final By checkoutButton = By.xpath("//android.widget.TextView[@text='CHECKOUT']");

    public CartPage(AppiumDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    public List<String> getCartItemTitles() {
        return actionDriver.getTexts(cartItemsTitle);
    }

    public int getCartItemCount() {
        try {
            List<WebElement> items = actionDriver.getElements(cartItemsTitle);
            int count = (items != null) ? items.size() : 0;
            logger.info("Cart contains {} items", count);
            return count;
        } catch (Exception e) {
            logger.warn("No cart items found, returning 0");
            return 0;
        }
    }


    public void proceedToCheckout() {
        if (getCartItemCount() >= 2) {
            logger.info("More than 1 product in cart, scrolling to checkout button...");
            actionDriver.scrollToElement(checkoutButton);
            actionDriver.click(checkoutButton);
            logger.info("Clicked on Checkout button");
        } else {
            logger.warn("Less than 2 products in cart. Checkout button may already be visible.");
            actionDriver.click(checkoutButton);
        }
    }

    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }
}
