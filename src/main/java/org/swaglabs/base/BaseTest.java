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
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/config.properties");
        properties.load(fileInputStream);
        logger.info("Config file loaded");
    }

    @BeforeMethod
    public synchronized void setUp(Method method) throws Exception {
        logger.info("Setting up AppiumDriver for: {}", this.getClass().getSimpleName());

        boolean runOnEmulator = Boolean.parseBoolean(properties.getProperty("runOnEmulator", "true"));
        boolean runOnBrowserStack = Boolean.parseBoolean(properties.getProperty("runOnBrowserStack", "false"));

        UiAutomator2Options options = new UiAutomator2Options();
        AppiumDriver driver;

        if (runOnBrowserStack) {
            // BrowserStack setup
            options.setPlatformName("Android");
            options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
            options.setCapability("device", properties.getProperty("browserstack.device"));
            options.setCapability("os_version", properties.getProperty("browserstack.os_version"));
            options.setCapability("app", properties.getProperty("browserstack.app"));
            options.setCapability("project", "SwagLabs Mobile Automation");
            options.setCapability("build", "GitHub Actions CI");
            options.setCapability("name", method.getName());
            options.setCapability("autoGrantPermissions", true);

            driver = new AppiumDriver(new URL("https://"
                    + properties.getProperty("browserstack.username") + ":"
                    + properties.getProperty("browserstack.accessKey")
                    + "@hub.browserstack.com/wd/hub"), options);

        } else {
            // Local Emulator or Real Device setup
            String deviceName = runOnEmulator ? properties.getProperty("emulator.deviceName") : properties.getProperty("real.deviceName");
            String platformVersion = runOnEmulator ? properties.getProperty("emulator.platformVersion") : properties.getProperty("real.platformVersion");
            String udid = runOnEmulator ? properties.getProperty("emulator.udid") : properties.getProperty("real.udid");
            String appPath = properties.getProperty("app.path");
            String appiumServerURL = properties.getProperty("appium.serverURL");

            options.setDeviceName(deviceName);
            options.setPlatformVersion(platformVersion);
            options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
            options.setApp(System.getProperty("user.dir") + "/" + appPath);
            options.setAppPackage("com.swaglabsmobileapp");
            options.setAppActivity("com.swaglabsmobileapp.SplashActivity");
            options.setAutoGrantPermissions(true);
            options.setNewCommandTimeout(Duration.ofSeconds(3600));
            if (!udid.isEmpty()) options.setUdid(udid);

            driver = new AppiumDriver(new URL(appiumServerURL), options);
        }

        driverThreadLocal.set(driver);
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
        if (driverThreadLocal.get() == null) throw new IllegalStateException("Driver not initialized");
        return driverThreadLocal.get();
    }

    public static ActionDriver getActionDriver() {
        if (actionDriverThreadLocal.get() == null) throw new IllegalStateException("ActionDriver not initialized");
        return actionDriverThreadLocal.get();
    }

    public static Properties getProperties() {
        return properties;
    }
}
