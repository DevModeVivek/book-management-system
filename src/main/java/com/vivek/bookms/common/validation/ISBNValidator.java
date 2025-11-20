package com.vivek.bookms.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Validator implementation for ISBN validation
 * Supports both ISBN-10 and ISBN-13 formats with proper validation logic
 */
public class ISBNValidator implements ConstraintValidator<ValidISBN, String> {

    private boolean allowNull;
    private ValidISBN.ISBNFormat[] formats;

    @Override
    public void initialize(ValidISBN constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.formats = constraintAnnotation.formats();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }

        // Remove hyphens and spaces for validation
        String cleanISBN = value.replaceAll("[\\s-]", "");

        // Check if the ISBN matches any of the specified formats
        return Arrays.stream(formats)
                .anyMatch(format -> isValidFormat(cleanISBN, format));
    }

    private boolean isValidFormat(String isbn, ValidISBN.ISBNFormat format) {
        if (!Pattern.matches(format.getPattern(), isbn)) {
            return false;
        }

        // Additional checksum validation
        switch (format) {
            case ISBN_10:
                return validateISBN10Checksum(isbn);
            case ISBN_13:
                return validateISBN13Checksum(isbn);
            default:
                return false;
        }
    }

    /**
     * Validates ISBN-10 checksum using the standard algorithm
     */
    private boolean validateISBN10Checksum(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(isbn.charAt(i))) {
                return false;
            }
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }

        char checkChar = isbn.charAt(9);
        int checkDigit = checkChar == 'X' ? 10 : checkChar - '0';
        
        return (sum + checkDigit) % 11 == 0;
    }

    /**
     * Validates ISBN-13 checksum using the standard algorithm
     */
    private boolean validateISBN13Checksum(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            if (!Character.isDigit(isbn.charAt(i))) {
                return false;
            }
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        if (!Character.isDigit(isbn.charAt(12))) {
            return false;
        }
        int checkDigit = isbn.charAt(12) - '0';
        
        return (sum + checkDigit) % 10 == 0;
    }
}