package com.ig.igwebcms.core.util;


import com.ig.igwebcms.core.logging.LoggerUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This Class contains method related to date object formatting.
 */
public final class DateUtil {
    /**
     * Initializing DateUtil class.
     */
    private DateUtil() {
        /* NO CONSTRUCTOR */
    }

    /**
     * Convert Date String into Date Object in required format.
     *
     * @param date   it is a date String
     * @param format represent the format of the date string.
     * @return Date object for the given string.
     */
    public static Date getDate(final String date, final String format) {

        Date dateObject = null;
        final SimpleDateFormat formatter = checkDateFormat(format);
        try {
            dateObject = formatter.parse(date);
        } catch (ParseException parseException) {
            LoggerUtil.errorLog(DateUtil.class, "Invalid String to convert into date object");
        }
        return dateObject;
    }

    /**
     * get current date according to the local in a specified format.
     *
     * @param locale represent the locale in which date will be returned.
     * @param format represent the format of the date string.
     * @return formatted date in String format.
     */

    public static String getCurrentDate(final String locale, final String format) {

        final Calendar calendar = Calendar.getInstance();
        if (!"".equals(locale)) {
            calendar.setTimeZone(TimeZone.getTimeZone(locale));
        }
        final SimpleDateFormat formatter = checkDateFormat(format);
        return formatter.format(calendar.getTime());
    }

    /**
     * private method for getting the string formatter for a given format.
     *
     * @param format represent the format of the date string.
     * @return SimpleDateFormat object.
     */
    private static SimpleDateFormat checkDateFormat(final String format) {

        return ("".equals(format)) ? new SimpleDateFormat("dd-MM-yyyy", Locale.US) : new SimpleDateFormat(format);
    }

    /**
     * get current date in a specific format.
     *
     * @param format represent the format of the date string.
     * @return formatted date in String format.
     */
    public static String getCurrentDate(final String format) {

        final SimpleDateFormat dateFormat = checkDateFormat(format);
        final Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }
}
