package org.swaglabs.base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;
import org.apache.logging.log4j.Logger;
import org.swaglabs.actiondriver.ActionDriver;
import org.swaglabs.utilities.LoggerManager;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {

    protected static Properties properties;
    private static ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<ActionDriver> actionDriverThreadLocal = new ThreadLocal<>();
    public static final Logger logger = LoggerManager.logger(BaseTest.class);

    @BeforeSuite
    public void loadConfig() throws IOException {
        properties = new Properties();
        FileInputStream fileInputStream =
                new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/config.properties");
        properties.load(fileInputStream);
        logger.info("Config file loaded successfully");
    }

    @BeforeMethod
    public synchronized void setUp(Method method) throws Exception {
        logger.info("Setting up AppiumDriver for: {}", this.getClass().getSimpleName());

        boolean runOnBrowserStack = Boolean.parseBoolean(properties.getProperty("runOnBrowserStack", "false"));
        boolean runOnEmulator = Boolean.parseBoolean(properties.getProperty("runOnEmulator", "true"));

        AppiumDriver driver;

        if (runOnBrowserStack) {
            logger.info("Running tests on BrowserStack Cloud Device");

            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName(properties.getProperty("browserstack.device"));
            options.setPlatformVersion(properties.getProperty("browserstack.os_version"));
            options.setApp(properties.getProperty("browserstack.app"));
            options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);

            String username = System.getenv("BROWSERSTACK_USERNAME");
            String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");

            String browserstackURL = "https://" + username + ":" + accessKey + "@hub.browserstack.com/wd/hub";
            driver = new AppiumDriver(new URL(browserstackURL), options);

        } else {
            logger.info("Running tests on Local Emulator/Real Device");

            String deviceName = runOnEmulator
                    ? properties.getProperty("emulator.deviceName")
                    : properties.getProperty("real.deviceName");
            String platformVersion = runOnEmulator
                    ? properties.getProperty("emulator.platformVersion")
                    : properties.getProperty("real.platformVersion");
            String udid = runOnEmulator
                    ? properties.getProperty("emulator.udid")
                    : properties.getProperty("real.udid");

            String appPath = properties.getProperty("app.path");
            String appiumServerURL = properties.getProperty("appium.serverURL");

            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName(deviceName)
                    .setPlatformVersion(platformVersion)
                    .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                    .setApp(System.getProperty("user.dir") + "/" + appPath);

            options.setAppPackage("com.swaglabsmobileapp");
            options.setAppActivity("com.swaglabsmobileapp.SplashActivity");
            options.setAutoGrantPermissions(true);
            options.setNewCommandTimeout(Duration.ofSeconds(3600));

            if (udid != null && !udid.isEmpty()) {
                options.setUdid(udid);
            }

            driver = new AppiumDriver(new URL(appiumServerURL), options);
        }

        driverThreadLocal.set(driver);

        // Initialize ActionDriver for thread
        actionDriverThreadLocal.set(new ActionDriver(driver));
        logger.info("ActionDriver initialized for thread: " + Thread.currentThread().getId());
    }

    @AfterMethod
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            logger.info("AppiumDriver quit successfully");
        }
        driverThreadLocal.remove();
        actionDriverThreadLocal.remove();
    }

    // Thread-safe getters
    public static AppiumDriver getDriver() {
        if (driverThreadLocal.get() == null)
            throw new IllegalStateException("Driver not initialized");
        return driverThreadLocal.get();
    }

    public static ActionDriver getActionDriver() {
        if (actionDriverThreadLocal.get() == null)
            throw new IllegalStateException("ActionDriver not initialized");
        return actionDriverThreadLocal.get();
    }

    public static Properties getProperties() {
        return properties;
    }
}
