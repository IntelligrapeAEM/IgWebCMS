package com.ig.igwebcms.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LoggerUtil for handling centralized
 * logging for errors and log messages.
 * method can be called as -
 * Example -
 * LoggerUtil.infoLog(Demo.class,"{} > {}","2","1"); (for runtime entry)
 * LoggerUtil.infoLog(Demo.class,"2>1"); (for static value entry)
 */
public final class LoggerUtil {

    /**
     * Instantiates a new logger util.
     */
    private LoggerUtil() {
    /* blank constructor */
    }

    /**
     * Write the info level log message to log file.
     *
     * @param className  Class name for which log log message to be written.
     * @param message    String message which is written to the log file
     *                   it may contain {} for entering runtime value.
     * @param parameters list of parameter values that will be
     *                   added to the message {} runtime values.
     */
    @SuppressWarnings("rawtypes")
    public static void infoLog(final Class className, final String message, final Object... parameters) {

        final Logger logger = LoggerUtil.getLogger(className);
        logger.info(message, parameters);
    }

    /**
     * Write the debug level log message to log file.
     *
     * @param className  Class name for which log log message to be written.
     * @param message    String message which is written to the log file
     *                   it may contain {} for entering runtime value.
     * @param parameters list of parameter values that will be
     *                   added to the message {} runtime values.
     */
    @SuppressWarnings("rawtypes")
    public static void debugLog(final Class className,
                                final String message, final Object... parameters) {
        final Logger logger = LoggerUtil.getLogger(className);
        logger.debug(message, parameters);
    }

    /**
     * Write the error level log message to log file.
     *
     * @param className  Class name for which log log message to be written.
     * @param message    String message which is written to the log file
     *                   it may contain {} for entering runtime value.
     * @param parameters list of parameter values that will be
     *                   added to the message {} runtime values.
     */
    @SuppressWarnings("rawtypes")
    public static void errorLog(final Class className,
                                final String message, final Object... parameters) {
        final Logger logger = LoggerUtil.getLogger(className);
        logger.error(message, parameters);
    }

    /**
     * Write the warn level log message to log file.
     *
     * @param className  Class name for which log log message to be written.
     * @param message    String message which is written to the log file
     *                   it may contain {} for entering runtime value.
     * @param parameters list of parameter values that will be
     *                   added to the message {} runtime values.
     */
    @SuppressWarnings("rawtypes")
    public static void warnLog(final Class className, final String message, final Object... parameters) {
        final Logger logger = LoggerUtil.getLogger(className);
        logger.warn(message, parameters);
    }

    /**
     * determines if the debug is enabled or not.
     *
     * @param className the cl.ass name.
     * @return true, if is debug. enabled.
     */
    @SuppressWarnings("rawtypes")
    public static boolean isDebugEnabled(final Class className) {

        final Logger logger = LoggerUtil.getLogger(className);
        return logger.isDebugEnabled();
    }

    /**
     * Gets the logger.
     *
     * @param className the cl for which logger object to be created.ass name.
     * @return logger object.
     */
    @SuppressWarnings("rawtypes")
    private static Logger getLogger(final Class className) {

        return LoggerFactory.getLogger(className);
    }
}
