package org.swaglabs.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.swaglabs.actiondriver.ActionDriver;
import org.swaglabs.base.BaseTest;
import org.swaglabs.utilities.LoggerManager;

public class CheckoutOverviewPage {
    private final ActionDriver actionDriver;

    // Locators
    private final By itemTotalLabel = By.xpath("//android.widget.TextView[contains(@text, 'Item total')]");
    private final By taxLabel       = By.xpath("//android.widget.TextView[contains(@text, 'Tax')]");
    private final By totalLabel     = By.xpath("//android.widget.TextView[contains(@text, 'Total')]");
    private final By finishButton   = By.xpath("//android.widget.TextView[@text='FINISH']");

    public CheckoutOverviewPage(AppiumDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    public double getItemTotal() {
        String text = actionDriver.getText(itemTotalLabel);
        return extractPrice(text);
    }

    public double getTax() {
        String text = actionDriver.getText(taxLabel);
        return extractPrice(text);
    }

    public double getTotal() {
        String text = actionDriver.getText(totalLabel);
        return extractPrice(text);
    }


    public boolean verifyTotalWithTax() {
        double itemTotal = getItemTotal();
        double tax = getTax();
        double expectedTotal = Math.round((itemTotal + tax) * 100.0) / 100.0;
        double actualTotal = getTotal();

        return expectedTotal == actualTotal;
    }


    public Totals verifyTotalsAndFinish() {
        // 1. Scroll until FINISH button is visible
        String uiScroll = "new UiScrollable(new UiSelector().scrollable(true))"
                + ".scrollIntoView(new UiSelector().text(\"FINISH\"));";
        BaseTest.getDriver().findElement(new AppiumBy.ByAndroidUIAutomator(uiScroll));

        // 2. Read amounts
        double itemTotal = extractPrice(actionDriver.getText(itemTotalLabel));
        double tax = extractPrice(actionDriver.getText(taxLabel));
        double total = extractPrice(actionDriver.getText(totalLabel));

        // 3. Validate totals (itemTotal + 8% tax = total)
        double expectedTotal = itemTotal + (itemTotal * 0.08); // 8% tax
        if (Math.abs(expectedTotal - total) > 0.01) { // allow rounding margin
            throw new AssertionError("Total mismatch! Expected: " + expectedTotal + " but got: " + total);
        }

        // 4. Click Finish
        actionDriver.click(finishButton);
        LoggerManager.logger(CheckoutOverviewPage.class).info("Verified totals: ItemTotal={}, Tax={}, Total={}", itemTotal, tax, total);

        return new Totals(itemTotal, tax, total, expectedTotal);
    }

    private double extractPrice(String text) {
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    // Helper class to hold totals
    public static class Totals {
        public final double itemTotal;
        public final double tax;
        public final double total;
        public final double expectedTotal;

        public Totals(double itemTotal, double tax, double total, double expectedTotal) {
            this.itemTotal = itemTotal;
            this.tax = tax;
            this.total = total;
            this.expectedTotal = expectedTotal;
        }
    }
}
