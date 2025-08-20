package org.swaglabs.actiondriver;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.swaglabs.base.BaseTest;
import org.swaglabs.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionDriver {

    private static final Logger logger = LoggerManager.logger(ActionDriver.class);

    private final AppiumDriver driver;
    private final WebDriverWait wait;

    public ActionDriver(AppiumDriver driver) {
        this.driver = driver;
        int explicitWait = Integer.parseInt(BaseTest.getProperties().getProperty("explicitWait", "10"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        logger.info("ActionDriver initialized");
    }

    // ===================== Wait Methods =====================

    public void waitForElementToBeVisible(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitForElementsToBeVisible(By by) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    public void waitForElementToBeClickable(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    // ===================== Element Actions =====================

    public void click(By by) {
        waitForElementToBeClickable(by);
        driver.findElement(by).click();
    }

    public void enterText(By by, String text) {
        waitForElementToBeVisible(by);
        WebElement element = driver.findElement(by);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By by) {
        waitForElementToBeVisible(by);
        return driver.findElement(by).getText();
    }

    public boolean isElementDisplayed(By by) {
        try {
            waitForElementToBeVisible(by);
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areElementsDisplayed(By by) {
        try {
            waitForElementsToBeVisible(by);
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== Mobile Gestures =====================


    public void scrollToElement(By by) {
        boolean isFound = false;
        int maxScrolls = 5; // safety net
        int scrollCount = 0;

        while (!isFound && scrollCount < maxScrolls) {
            try {
                if (driver.findElement(by).isDisplayed()) {
                    isFound = true;
                    break;
                }
            } catch (Exception e) {
                // not found yet, scroll
                String uiScrollable = "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()";
                driver.findElement(AppiumBy.androidUIAutomator(uiScrollable));
                scrollCount++;
            }
        }

        if (!isFound) {
            throw new RuntimeException("Element not found after scrolling: " + by.toString());
        }
    }

//
//    public void tap(By by) {
//        waitForElementToBeVisible(by);
//        new TouchAction(driver)
//                .tap(ElementOption.element(driver.findElement(by)))
//                .perform();
//    }
//
//    public void longPress(By by, int seconds) {
//        waitForElementToBeVisible(by);
//        new TouchAction(driver)
//                .longPress(ElementOption.element(driver.findElement(by)))
//                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(seconds)))
//                .release()
//                .perform();
//    }
//
//    public void swipe(By startBy, By endBy, int durationSeconds) {
//        WebElement start = driver.findElement(startBy);
//        WebElement end = driver.findElement(endBy);
//        new TouchAction(driver)
//                .press(ElementOption.element(start))
//                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(durationSeconds)))
//                .moveTo(ElementOption.element(end))
//                .release()
//                .perform();
//    }
//
//    public void scrollToElement(By by) {
//        WebElement element = driver.findElement(by);
//        driver.executeScript("mobile: scroll", new java.util.HashMap<String, Object>() {{
//            put("element", element.getId());
//            put("toVisible", true);
//        }});
//    }

    // ===================== Alert Handling =====================

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public void dismissAlert() {
        driver.switchTo().alert().dismiss();
    }

    public String getAlertText() {
        return driver.switchTo().alert().getText();
    }

    // ===================== Utilities =====================

    public void staticWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WebElement> getElements(By by) {
        waitForElementToBeVisible(by);
        return driver.findElements(by);
    }

    public List<String> getTexts(By by) {
        List<String> texts = new ArrayList<>();
        try {
            // Wait for at least one element to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            List<WebElement> elements = driver.findElements(by);
            for (WebElement el : elements) {
                String text = el.getText();
                if (!text.isEmpty()) {
                    texts.add(text);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to get texts for elements: " + by.toString(), e);
        }
        return texts;
    }

}
