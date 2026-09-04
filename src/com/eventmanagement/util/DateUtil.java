package com.eventmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static String formatDate(LocalDate date) {

        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    public static LocalDate parseDate(String dateText) throws DateTimeParseException {
        if (dateText == null) {
            throw new DateTimeParseException("Date cannot be null", "", 0);
        }
        return LocalDate.parse(dateText.trim(), DATE_FORMATTER);
    }

}
