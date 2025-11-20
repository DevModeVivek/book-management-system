package com.vivek.bookms.exception;

import lombok.Getter;
import java.util.Map;
import java.util.HashMap;

/**
 * Exception for validation-related errors with Lombok optimization
 */
@Getter
public class ValidationException extends RuntimeException {
    private final Map<String, String> fieldErrors;
    private final String errorContext;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorContext = "GENERAL_ERROR";
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldErrors = new HashMap<>();
        this.errorContext = "GENERAL_ERROR";
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
        this.errorContext = "GENERAL_ERROR";
    }

    public ValidationException(String message, String errorContext) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.errorContext = errorContext != null ? errorContext : "GENERAL_ERROR";
    }

    public ValidationException(String message, Map<String, String> fieldErrors, String errorContext) {
        super(message);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
        this.errorContext = errorContext != null ? errorContext : "GENERAL_ERROR";
    }

    public ValidationException withContext(String context) {
        return new ValidationException(this.getMessage(), this.fieldErrors, context);
    }

    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> fieldErrors = new HashMap<>();
        private String message = "Validation failed";
        private String context = "GENERAL_ERROR";

        public Builder addFieldError(String field, String error) {
            fieldErrors.put(field, error);
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withContext(String context) {
            this.context = context;
            return this;
        }

        public boolean hasErrors() {
            return !fieldErrors.isEmpty();
        }

        public ValidationException build() {
            return new ValidationException(message, fieldErrors, context);
        }
    }
}