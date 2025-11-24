package com.vivek.bookms.common.validation;

import com.vivek.bookms.constants.AppConstants;
import com.vivek.bookms.constants.ErrorCodes;
import com.vivek.bookms.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Enhanced validation utilities with comprehensive validation methods
 * Uses centralized constants for consistency and maintainability
 */
@Slf4j
public final class ValidationUtils {

    // Pre-compiled patterns for better performance
    private static final Pattern TEXT_WITH_PUNCTUATION = Pattern.compile(AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION);
    private static final Pattern SEARCH_QUERY_PATTERN = Pattern.compile(AppConstants.RegexPatterns.SEARCH_QUERY);
    private static final Pattern ISBN_10_PATTERN = Pattern.compile(AppConstants.RegexPatterns.ISBN_10);
    private static final Pattern ISBN_13_PATTERN = Pattern.compile(AppConstants.RegexPatterns.ISBN_13);

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ============= BASIC VALIDATION METHODS =============

    /**
     * Validates that a value is not null
     */
    public static <T> T requireNonNull(T obj, String fieldName) {
        if (obj == null) {
            String message = String.format(AppConstants.ErrorMessages.REQUIRED_FIELD_MISSING, fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN, 
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonNull", 
                    "NULL_VALUE", message);
            throw ValidationException.requiredField(fieldName);
        }
        return obj;
    }

    /**
     * Validates that a value is not null with custom exception
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

    // ============= STRING VALIDATION METHODS =============

    /**
     * Validates that a string is not null or blank
     */
    public static String requireNonBlank(String str, String fieldName) {
        if (isBlank(str)) {
            String message = String.format(AppConstants.ErrorMessages.REQUIRED_FIELD_MISSING, fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonBlank",
                    "BLANK_VALUE", message);
            throw ValidationException.requiredField(fieldName);
        }
        return str.trim();
    }

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
     * Validates string length within specified bounds
     */
    public static void validateStringLength(String str, String fieldName, int minLength, int maxLength) {
        if (str != null) {
            int length = str.length();
            if (length < minLength) {
                String message = String.format(AppConstants.ErrorMessages.FIELD_TOO_SHORT, fieldName, minLength);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateStringLength",
                        "LENGTH_TOO_SHORT", message);
                throw ValidationException.builder()
                    .withErrorCode(ErrorCodes.Validation.TOO_SHORT)
                    .withUserMessage(message)
                    .addFieldError(fieldName, String.format("Minimum length is %d characters", minLength))
                    .build();
            }
            if (length > maxLength) {
                String message = String.format(AppConstants.ErrorMessages.FIELD_TOO_LONG, fieldName, maxLength);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateStringLength",
                        "LENGTH_TOO_LONG", message);
                throw ValidationException.builder()
                    .withErrorCode(ErrorCodes.Validation.TOO_LONG)
                    .withUserMessage(message)
                    .addFieldError(fieldName, String.format("Maximum length is %d characters", maxLength))
                    .build();
            }
        }
    }

    /**
     * Validates string matches a specific pattern
     */
    public static void validatePattern(String str, Pattern pattern, String fieldName) {
        if (isNotBlank(str) && !pattern.matcher(str).matches()) {
            String message = String.format("Field '%s' contains invalid characters", fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePattern",
                    "INVALID_PATTERN", message);
            throw ValidationException.invalidFormat(fieldName, "Valid characters only");
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
     * Validates that a collection is not null or empty
     */
    public static <T extends Collection<?>> T requireNonEmpty(T collection, String fieldName) {
        if (isEmpty(collection)) {
            String message = String.format("Collection '%s' cannot be null or empty", fieldName);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "requireNonEmpty",
                    "EMPTY_COLLECTION", message);
            throw ValidationException.requiredField(fieldName);
        }
        return collection;
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
                String message = String.format("Field '%s' must be between %s and %s, but was %s", 
                        fieldName, min, max, value);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateRange",
                        "OUT_OF_RANGE", message);
                throw ValidationException.builder()
                    .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                    .withUserMessage(message)
                    .addFieldError(fieldName, String.format("Value must be between %s and %s", min, max))
                    .build();
            }
        }
    }

    /**
     * Validates that a number is positive
     */
    public static void validatePositive(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            String message = String.format("Field '%s' must be positive, but was %s", fieldName, value);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePositive",
                    "NOT_POSITIVE", message);
            throw ValidationException.builder()
                .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                .withUserMessage(message)
                .addFieldError(fieldName, "Value must be positive")
                .build();
        }
    }

    // ============= BOOK-SPECIFIC VALIDATION METHODS =============

    /**
     * Validates book title according to application rules
     */
    public static void validateBookTitle(String title) {
        requireNonBlank(title, "title");
        validateStringLength(title, "title", 
                AppConstants.Validation.TITLE_MIN_LENGTH, 
                AppConstants.Validation.TITLE_MAX_LENGTH);
        validatePattern(title, TEXT_WITH_PUNCTUATION, "title");
    }

    /**
     * Validates book author according to application rules
     */
    public static void validateBookAuthor(String author) {
        requireNonBlank(author, "author");
        validateStringLength(author, "author", 
                AppConstants.Validation.AUTHOR_MIN_LENGTH, 
                AppConstants.Validation.AUTHOR_MAX_LENGTH);
        validatePattern(author, TEXT_WITH_PUNCTUATION, "author");
    }

    /**
     * Validates search query according to application rules
     */
    public static void validateSearchQuery(String query) {
        if (isNotBlank(query)) {
            validateStringLength(query, "search query", 
                    AppConstants.Validation.SEARCH_QUERY_MIN_LENGTH, 
                    AppConstants.Validation.SEARCH_QUERY_MAX_LENGTH);
            validatePattern(query, SEARCH_QUERY_PATTERN, "search query");
        }
    }

    /**
     * Validates ISBN format
     */
    public static void validateIsbn(String isbn) {
        if (isNotBlank(isbn)) {
            String cleanIsbn = isbn.replaceAll("[\\s-]", "");
            if (!ISBN_10_PATTERN.matcher(cleanIsbn).matches() && 
                !ISBN_13_PATTERN.matcher(cleanIsbn).matches()) {
                String message = "Invalid ISBN format. Must be a valid ISBN-10 or ISBN-13";
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validateIsbn",
                        "INVALID_ISBN", message);
                throw ValidationException.invalidFormat("isbn", "ISBN-10 or ISBN-13");
            }
        }
    }

    /**
     * Validates page count according to application rules
     */
    public static void validatePageCount(Integer pageCount) {
        if (pageCount != null) {
            validateRange(pageCount, 
                    AppConstants.Validation.PAGE_COUNT_MIN, 
                    AppConstants.Validation.PAGE_COUNT_MAX, 
                    "page count");
        }
    }

    /**
     * Validates price according to application rules
     */
    public static void validatePrice(BigDecimal price) {
        if (price != null) {
            BigDecimal minPrice = new BigDecimal(AppConstants.Validation.PRICE_MIN);
            BigDecimal maxPrice = new BigDecimal(AppConstants.Validation.PRICE_MAX);
            
            if (price.compareTo(minPrice) < 0 || price.compareTo(maxPrice) > 0) {
                String message = String.format("Price must be between %s and %s, but was %s", 
                        AppConstants.Validation.PRICE_MIN, AppConstants.Validation.PRICE_MAX, price);
                log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_VALIDATION, "validatePrice",
                        "INVALID_PRICE", message);
                throw ValidationException.builder()
                    .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                    .withUserMessage(message)
                    .addFieldError("price", String.format("Price must be between %s and %s", 
                        AppConstants.Validation.PRICE_MIN, AppConstants.Validation.PRICE_MAX))
                    .build();
            }
        }
    }

    /**
     * Validates publication date
     */
    public static void validatePublicationDate(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now())) {
            String message = "Publication date cannot be in the future";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePublicationDate",
                    "FUTURE_DATE", message);
            throw ValidationException.builder()
                .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                .withUserMessage(message)
                .addFieldError("publishedDate", "Date cannot be in the future")
                .build();
        }
    }

    // ============= PAGINATION VALIDATION =============

    /**
     * Validates pagination parameters
     */
    public static void validatePaginationParams(int page, int size) {
        if (page < 0) {
            String message = "Page number cannot be negative";
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePaginationParams",
                    "NEGATIVE_PAGE", message);
            throw ValidationException.builder()
                .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                .withUserMessage(message)
                .addFieldError("page", "Page number must be non-negative")
                .build();
        }
        
        if (size < AppConstants.Validation.MIN_PAGE_SIZE) {
            String message = String.format("Page size must be at least %d", AppConstants.Validation.MIN_PAGE_SIZE);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePaginationParams",
                    "INVALID_PAGE_SIZE", message);
            throw ValidationException.builder()
                .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                .withUserMessage(message)
                .addFieldError("size", String.format("Minimum size is %d", AppConstants.Validation.MIN_PAGE_SIZE))
                .build();
        }
        
        if (size > AppConstants.Validation.MAX_PAGE_SIZE) {
            String message = String.format("Page size cannot exceed %d", AppConstants.Validation.MAX_PAGE_SIZE);
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_VALIDATION, "validatePaginationParams",
                    "PAGE_SIZE_TOO_LARGE", message);
            throw ValidationException.builder()
                .withErrorCode(ErrorCodes.Validation.OUT_OF_RANGE)
                .withUserMessage(message)
                .addFieldError("size", String.format("Maximum size is %d", AppConstants.Validation.MAX_PAGE_SIZE))
                .build();
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
            throw ValidationException.constraintViolation(message);
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