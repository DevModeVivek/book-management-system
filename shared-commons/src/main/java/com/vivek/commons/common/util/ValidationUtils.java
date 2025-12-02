package com.vivek.commons.common.util;

import com.vivek.commons.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Comprehensive validation utilities for the Book Management System
 * Updated to use centralized constants for consistency and maintainability
 */
@Slf4j
public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ============= BASIC VALIDATION METHODS =============

    /**
     * Validates that a value is not null
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonNull",
                    "NULL_VALUE", message);
            throw new IllegalArgumentException(message);
        }
        return obj;
    }

    /**
     * Validates that a value is not null with a custom exception
     */
    public static <T, X extends Throwable> T requireNonNull(T obj, Supplier<? extends X> exceptionSupplier) throws X {
        if (obj == null) {
            X exception = exceptionSupplier.get();
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonNull",
                    "NULL_VALUE", exception.getMessage());
            throw exception;
        }
        return obj;
    }

    /**
     * Validates that a string is not null or empty
     */
    public static String requireNonBlank(String str, String fieldName) {
        if (isBlank(str)) {
            String message = String.format(AppConstants.ErrorMessages.REQUIRED_FIELD_MISSING, fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonBlank",
                    "BLANK_VALUE", message);
            throw new IllegalArgumentException(message);
        }
        return str.trim();
    }

    /**
     * Validates that a collection is not null or empty
     */
    public static <T extends Collection<?>> T requireNonEmpty(T collection, String fieldName) {
        if (isEmpty(collection)) {
            String message = MessageFormat.format("Collection ''{0}'' cannot be null or empty", fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonEmpty",
                    "EMPTY_COLLECTION", message);
            throw new IllegalArgumentException(message);
        }
        return collection;
    }

    // ============= STRING VALIDATION METHODS =============

    /**
     * Checks if a string is null or blank
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks if a string is not null and not blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Validates string length within specified bounds using centralized constants
     */
    public static void validateStringLength(String str, String fieldName, int minLength, int maxLength) {
        if (str != null) {
            int length = str.length();
            if (length < minLength) {
                String message = String.format(AppConstants.ErrorMessages.FIELD_TOO_SHORT, fieldName, minLength);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateStringLength",
                        "LENGTH_TOO_SHORT", message);
                throw new IllegalArgumentException(message);
            }
            if (length > maxLength) {
                String message = String.format(AppConstants.ErrorMessages.FIELD_TOO_LONG, fieldName, maxLength);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateStringLength",
                        "LENGTH_TOO_LONG", message);
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Validates string matches a specific pattern
     */
    public static void validatePattern(String str, String pattern, String fieldName) {
        if (isNotBlank(str) && !str.matches(pattern)) {
            String message = MessageFormat.format("Field ''{0}'' does not match required pattern", fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePattern",
                    "INVALID_PATTERN", message + " - value: " + str + ", pattern: " + pattern);
            throw new IllegalArgumentException(message);
        }
    }

    // ============= COLLECTION VALIDATION METHODS =============

    /**
     * Checks if a collection is null or empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if a collection is not null and not empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Checks if a map is null or empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if a map is not null and not empty
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    // ============= NUMERIC VALIDATION METHODS =============

    /**
     * Validates that a number is within specified range
     */
    public static void validateRange(Number value, Number min, Number max, String fieldName) {
        if (value != null) {
            double val = value.doubleValue();
            double minVal = min.doubleValue();
            double maxVal = max.doubleValue();
            
            if (val < minVal || val > maxVal) {
                String message = MessageFormat.format(
                    "Field ''{0}'' must be between {1} and {2}, but was {3}",
                    fieldName, min, max, value
                );
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateRange",
                        "OUT_OF_RANGE", message);
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Validates that a number is positive
     */
    public static void validatePositive(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            String message = MessageFormat.format("Field ''{0}'' must be positive, but was {1}", fieldName, value);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePositive",
                    "NOT_POSITIVE", message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that a number is non-negative
     */
    public static void validateNonNegative(Number value, String fieldName) {
        if (value != null && value.doubleValue() < 0) {
            String message = MessageFormat.format("Field ''{0}'' must be non-negative, but was {1}", fieldName, value);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateNonNegative",
                    "NEGATIVE_VALUE", message);
            throw new IllegalArgumentException(message);
        }
    }

    // ============= BOOK-SPECIFIC VALIDATION METHODS USING CONSTANTS =============

    /**
     * Validates book title according to application rules using centralized constants
     */
    public static void validateBookTitle(String title) {
        requireNonBlank(title, "title");
        validateStringLength(title, "title", 
                AppConstants.Validation.TITLE_MIN_LENGTH, 
                AppConstants.Validation.TITLE_MAX_LENGTH);
        validatePattern(title, AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, "title");
    }

    /**
     * Validates book author according to application rules using centralized constants
     */
    public static void validateBookAuthor(String author) {
        requireNonBlank(author, "author");
        validateStringLength(author, "author", 
                AppConstants.Validation.AUTHOR_MIN_LENGTH, 
                AppConstants.Validation.AUTHOR_MAX_LENGTH);
        validatePattern(author, AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, "author");
    }

    /**
     * Validates ISBN format and checksum
     */
    public static void validateIsbn(String isbn) {
        requireNonBlank(isbn, "ISBN");
        
        // Remove hyphens and spaces for validation
        String cleanIsbn = isbn.replaceAll("[\\s\\-]", "");
        
        if (cleanIsbn.length() == 10) {
            validateIsbn10(cleanIsbn);
        } else if (cleanIsbn.length() == 13) {
            validateIsbn13(cleanIsbn);
        } else {
            String message = "ISBN must be either 10 or 13 digits long";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateIsbn",
                    "INVALID_ISBN_LENGTH", message + " - provided: " + cleanIsbn);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates ISBN-10 format and checksum
     */
    private static void validateIsbn10(String isbn) {
        if (!isbn.matches("\\d{9}[\\dX]")) {
            String message = "Invalid ISBN-10 format";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateIsbn10",
                    "INVALID_FORMAT", message + " - provided: " + isbn);
            throw new IllegalArgumentException(message);
        }
        
        // Validate ISBN-10 checksum
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (Character.getNumericValue(isbn.charAt(i)) * (10 - i));
        }
        
        char lastChar = isbn.charAt(9);
        int checkDigit = lastChar == 'X' ? 10 : Character.getNumericValue(lastChar);
        sum += checkDigit;
        
        if (sum % 11 != 0) {
            String message = "Invalid ISBN-10 checksum";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateIsbn10",
                    "INVALID_CHECKSUM", message + " - provided: " + isbn);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates ISBN-13 format and checksum
     */
    private static void validateIsbn13(String isbn) {
        if (!isbn.matches("\\d{13}")) {
            String message = "Invalid ISBN-13 format";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateIsbn13",
                    "INVALID_FORMAT", message + " - provided: " + isbn);
            throw new IllegalArgumentException(message);
        }
        
        // Validate ISBN-13 checksum
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = Character.getNumericValue(isbn.charAt(12));
        int calculatedCheck = (10 - (sum % 10)) % 10;
        
        if (checkDigit != calculatedCheck) {
            String message = "Invalid ISBN-13 checksum";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateIsbn13",
                    "INVALID_CHECKSUM", message + " - provided: " + isbn);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates search query according to application rules using centralized constants
     */
    public static void validateSearchQuery(String query) {
        if (isNotBlank(query)) {
            validateStringLength(query, "search query", 
                    AppConstants.Validation.SEARCH_QUERY_MIN_LENGTH, 
                    AppConstants.Validation.SEARCH_QUERY_MAX_LENGTH);
            validatePattern(query, AppConstants.RegexPatterns.SEARCH_QUERY, "search query");
        }
    }

    /**
     * Validates page count according to application rules using centralized constants
     */
    public static void validatePageCount(Integer pageCount) {
        if (pageCount != null) {
            validateRange(pageCount, 
                    AppConstants.Validation.PAGE_COUNT_MIN, 
                    AppConstants.Validation.PAGE_COUNT_MAX, 
                    "page count");
        }
    }

    // ============= BUSINESS LOGIC VALIDATION METHODS =============

    /**
     * Validates that an entity exists and is active
     */
    public static void validateEntityExists(Object entity, Long id, String entityType) {
        if (entity == null) {
            String message = MessageFormat.format("{0} not found with id: {1}", entityType, id);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validateEntityExists",
                    "ENTITY_NOT_FOUND", message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates pagination parameters using centralized constants
     */
    public static void validatePaginationParams(int page, int size) {
        if (page < 0) {
            String message = "Page number cannot be negative";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePaginationParams",
                    "NEGATIVE_PAGE", message);
            throw new IllegalArgumentException(message);
        }
        
        if (size <= 0) {
            String message = "Page size must be positive";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePaginationParams",
                    "INVALID_PAGE_SIZE", message);
            throw new IllegalArgumentException(message);
        }
        
        if (size > AppConstants.Validation.MAX_PAGE_SIZE) {
            String message = MessageFormat.format("Page size cannot exceed {0}", 
                    AppConstants.Validation.MAX_PAGE_SIZE);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePaginationParams",
                    "PAGE_SIZE_TOO_LARGE", message);
            throw new IllegalArgumentException(message);
        }
    }

    // ============= ARRAY AND VARARGS VALIDATION =============

    /**
     * Validates that an array is not null or empty
     */
    @SafeVarargs
    public static <T> void requireNonEmpty(String fieldName, T... array) {
        if (array == null || array.length == 0) {
            String message = MessageFormat.format("Array ''{0}'' cannot be null or empty", fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonEmpty",
                    "EMPTY_ARRAY", message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates that all array elements are non-null
     */
    @SafeVarargs
    public static <T> void requireAllNonNull(String fieldName, T... array) {
        requireNonEmpty(fieldName, array);
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                String message = MessageFormat.format("Element at index {0} in array ''{1}'' cannot be null", i, fieldName);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "requireAllNonNull",
                        "NULL_ARRAY_ELEMENT", message);
                throw new IllegalArgumentException(message);
            }
        }
    }

    // ============= CONDITIONAL VALIDATION =============

    /**
     * Validates a condition and throws exception with message if false
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "require",
                    "CONDITION_FAILED", message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates a condition and throws custom exception if false
     */
    public static <X extends Throwable> void require(boolean condition, Supplier<? extends X> exceptionSupplier) throws X {
        if (!condition) {
            X exception = exceptionSupplier.get();
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "require",
                    "CONDITION_FAILED", exception.getMessage());
            throw exception;
        }
    }
}