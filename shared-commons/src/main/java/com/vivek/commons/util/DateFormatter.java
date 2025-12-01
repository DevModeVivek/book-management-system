package com.vivek.commons.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    public static String formatForDisplay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DISPLAY_FORMATTER);
    }
    
    public static String formatForApi(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DEFAULT_FORMATTER);
    }
    
    public static LocalDate parseFromString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd", e);
        }
    }
    
    public static boolean isValidDateFormat(String dateStr) {
        try {
            parseFromString(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}