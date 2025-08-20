package org.swaglabs.utilities;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerManager {

    /**
     * Returns a class-specific logger for better traceability in logs.
     * @param clazz The class requesting the logger
     * @return Logger instance
     */
    public static Logger logger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
}
