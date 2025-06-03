package de.hitec.nhplus.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class to convert between <code>LocalDate</code> and <code>String</code>.
 */
public class DateConverter {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";

    /**
     * Converts a <code>String</code> to a <code>LocalDate</code>.
     *
     * @param date The date to convert.
     * @return The converted date.
     */
    public static LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Converts a <code>String</code> to a <code>LocalTime</code>.
     *
     * @param time The time to convert.
     * @return The converted time.
     */
    public static LocalTime convertStringToLocalTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    /**
     * Converts a <code>LocalDate</code> to a <code>String</code>.
     *
     * @param date The date to convert.
     * @return The converted date.
     */
    public static String convertLocalDateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    /**
     * Converts a <code>LocalTime</code> to a <code>String</code>.
     *
     * @param time The time to convert.
     * @return The converted time.
     */
    public static String convertLocalTimeToString(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }
}
