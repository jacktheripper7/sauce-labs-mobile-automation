package org.swaglabs.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.swaglabs.actiondriver.ActionDriver;
import org.swaglabs.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ProductPage {

    private static final Logger logger = LoggerManager.logger(ProductPage.class);

    private final ActionDriver actionDriver;

    // Locator for product catalog unique element (adjust based on app)
    private final By productCatalogTitle = By.xpath("//android.widget.TextView[@text='PRODUCTS']");
    private final By productListItems = By.xpath("//android.view.ViewGroup[@content-desc='test-Item']");
    private final By productTitle = By.xpath("//android.widget.TextView[@content-desc='test-Item title']");
    private final By productDescription = By.xpath("//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[2]");
    private final By filterButton = By.xpath("//android.view.ViewGroup[@content-desc='test-Modal Selector Button']");
    private final By sortOptionByAscPrice = By.xpath("//android.widget.TextView[@text='Price (low to high)']");
    private final By firstProductPrice = By.xpath("(//android.view.ViewGroup[@content-desc='test-Item'])[1]//android.widget.TextView[contains(@text,'$')]");
    private final By cartIcon = AppiumBy.accessibilityId("test-Cart");

    public ProductPage(AppiumDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    // Check if catalog is displayed
    public boolean isProductPageTitleDisplayed() {
        boolean displayed = actionDriver.isElementDisplayed(productCatalogTitle);
        logger.info("Product page title displayed: {}", displayed);
        return displayed;
    }

    // Check if product catalog is displayed
    public boolean isProductCatalogDisplayed() {
        boolean displayed = actionDriver.areElementsDisplayed(productListItems);
        logger.info("Product catalog displayed: {}", displayed);
        return displayed;
    }

    // Get all product titles
    public List<String> getAllVisibleProductTitles() {
        List<String> titles = actionDriver.getTexts(productTitle);
        logger.info("Visible product titles: {}", titles);
        return titles;
    }

    public boolean isProductListLoaded() {
        List<String> titles = getAllVisibleProductTitles();
        return !titles.isEmpty();
    }

    // Select a product by title
    public void selectProduct(String title) {
        List<String> titles = getAllVisibleProductTitles();
        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).equalsIgnoreCase(title)) {
                logger.info("Selecting product: {}", title);
                actionDriver.click(By.xpath("//android.view.ViewGroup[@content-desc='test-Item'][" + (i + 1) + "]"));
                return;
            }
        }
        logger.error("Product with title '{}' not found", title);
        throw new RuntimeException("Product with title '" + title + "' not found");
    }

    // Get product description
    public String getSelectedProductDescription() {
        return actionDriver.getText(productDescription);
    }

    // Filter/Sort products
    public void sortProductsByAscendingPrice() {
        actionDriver.click(filterButton);
        actionDriver.click(sortOptionByAscPrice);
    }

    public String getFirstProductPrice() {
        return actionDriver.getText(firstProductPrice);
    }

    public void addProductToCart(String productName) {
        // Dynamic locator for product's "ADD TO CART" button
        By addToCartBtn = By.xpath(
                "//android.widget.TextView[@text='" + productName + "']/../..//android.view.ViewGroup[@content-desc='test-ADD TO CART']"
        );

        logger.info("Adding product to cart: {}", productName);
        actionDriver.click(addToCartBtn);
    }


    public void openCart() {
        logger.info("Opening cart page");
        actionDriver.click(cartIcon);
    }

    /**
     * Get the cart badge count.
     */
    public int getCartBadgeCount() {
        By cartBadge = By.xpath("//android.view.ViewGroup[@content-desc='test-Cart']//android.widget.TextView[@text]");
        String countText = actionDriver.getText(cartBadge);
        int count = Integer.parseInt(countText.trim());
        logger.info("Cart badge count fetched: {}", count);
        return count;
    }
}
