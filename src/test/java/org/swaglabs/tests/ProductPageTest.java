package org.swaglabs.tests;

import org.swaglabs.base.BaseTest;
import org.swaglabs.pages.LoginPage;
import org.swaglabs.pages.ProductPage;
import org.swaglabs.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductPageTest extends BaseTest {
    private static final Logger logger = LoggerManager.logger(ProductPageTest.class);
    private ProductPage productPage;
    private LoginPage loginPage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(getDriver());
        productPage = new ProductPage(getDriver());
    }

    @Test
    public void validateProductListIsLoaded() {
        loginPage.performLogin("standard_user", "secret_sauce");
        Assert.assertTrue(productPage.isProductPageTitleDisplayed(), "Product page title is not displayed");
        Assert.assertTrue(productPage.isProductCatalogDisplayed(), "Product catalog is not displayed");
        Assert.assertTrue(productPage.isProductListLoaded(), "Product list with actual products in it is not loaded");
        List<String> titles = productPage.getAllVisibleProductTitles();
        logger.info("Products loaded: {}", titles);
    }

    @Test
    public void selectSpecificProduct() {
        loginPage.performLogin("standard_user", "secret_sauce");
        Assert.assertTrue(productPage.isProductPageTitleDisplayed(), "Product page title is not displayed");
        String productToSelect = "Sauce Labs Backpack";
        productPage.selectProduct(productToSelect);
        String productDescription = productPage.getSelectedProductDescription();
        Assert.assertEquals(productDescription, "carry.allTheThings() with the sleek, " +
                        "streamlined Sly Pack that melds uncompromising style with unequaled laptop and tablet protection.",
                "Product description does not match expected");
    }

    @Test
    public void sortProductsByAscendingPrice() {
        loginPage.performLogin("standard_user", "secret_sauce");
        Assert.assertTrue(productPage.isProductPageTitleDisplayed(), "Product page title is not displayed");
        Assert.assertTrue(productPage.isProductCatalogDisplayed(), "Product catalog is not displayed");
        productPage.sortProductsByAscendingPrice();
        List<String> titles = productPage.getAllVisibleProductTitles();
        logger.info("Sorted visible products: {}", titles);

        String firstProductPrice = productPage.getFirstProductPrice();
        Assert.assertTrue(firstProductPrice.contains("$"), "First product price is not displayed correctly");
        Assert.assertTrue(firstProductPrice.contains("7.99"), "First product price is not the lowest");
        logger.info("First product price: {}", firstProductPrice);
    }
}
