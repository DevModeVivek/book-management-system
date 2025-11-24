package com.vivek.bookms.constants;

/**
 * Centralized error codes and categories to avoid hardcoding and support internationalization
 */
public final class ErrorCodes {
    
    private ErrorCodes() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // ============= ERROR CATEGORIES =============
    public static final class Categories {
        public static final String DOMAIN = "DOMAIN";
        public static final String VALIDATION = "VALIDATION";
        public static final String EXTERNAL_API = "EXTERNAL_API";
        public static final String SECURITY = "SECURITY";
        public static final String DATABASE = "DATABASE";
        public static final String SYSTEM = "SYSTEM";
        
        private Categories() {}
    }
    
    // ============= BOOK DOMAIN ERRORS =============
    public static final class Book {
        public static final String NOT_FOUND = "BOOK_NOT_FOUND";
        public static final String ALREADY_EXISTS = "BOOK_ALREADY_EXISTS";
        public static final String INVALID_ISBN = "BOOK_INVALID_ISBN";
        public static final String DUPLICATE_ISBN = "BOOK_DUPLICATE_ISBN";
        public static final String INVALID_STATE = "BOOK_INVALID_STATE";
        public static final String CREATION_FAILED = "BOOK_CREATION_FAILED";
        public static final String UPDATE_FAILED = "BOOK_UPDATE_FAILED";
        public static final String DELETE_FAILED = "BOOK_DELETE_FAILED";
        
        private Book() {}
    }
    
    // ============= VALIDATION ERRORS =============
    public static final class Validation {
        public static final String GENERAL_ERROR = "VALIDATION_ERROR";
        public static final String REQUIRED_FIELD = "VALIDATION_REQUIRED_FIELD";
        public static final String INVALID_FORMAT = "VALIDATION_INVALID_FORMAT";
        public static final String OUT_OF_RANGE = "VALIDATION_OUT_OF_RANGE";
        public static final String TOO_LONG = "VALIDATION_TOO_LONG";
        public static final String TOO_SHORT = "VALIDATION_TOO_SHORT";
        public static final String INVALID_PATTERN = "VALIDATION_INVALID_PATTERN";
        public static final String CONSTRAINT_VIOLATION = "VALIDATION_CONSTRAINT_VIOLATION";
        
        private Validation() {}
    }
    
    // ============= EXTERNAL API ERRORS =============
    public static final class ExternalApi {
        public static final String SERVICE_UNAVAILABLE = "EXTERNAL_API_UNAVAILABLE";
        public static final String TIMEOUT = "EXTERNAL_API_TIMEOUT";
        public static final String INVALID_RESPONSE = "EXTERNAL_API_INVALID_RESPONSE";
        public static final String AUTHENTICATION_FAILED = "EXTERNAL_API_AUTH_FAILED";
        public static final String RATE_LIMITED = "EXTERNAL_API_RATE_LIMITED";
        public static final String GOOGLE_BOOKS_ERROR = "GOOGLE_BOOKS_API_ERROR";
        
        private ExternalApi() {}
    }
    
    // ============= SECURITY ERRORS =============
    public static final class Security {
        public static final String ACCESS_DENIED = "SECURITY_ACCESS_DENIED";
        public static final String INSUFFICIENT_PRIVILEGES = "SECURITY_INSUFFICIENT_PRIVILEGES";
        public static final String AUTHENTICATION_REQUIRED = "SECURITY_AUTH_REQUIRED";
        public static final String INVALID_CREDENTIALS = "SECURITY_INVALID_CREDENTIALS";
        public static final String SESSION_EXPIRED = "SECURITY_SESSION_EXPIRED";
        
        private Security() {}
    }
    
    // ============= DATABASE ERRORS =============
    public static final class Database {
        public static final String CONNECTION_FAILED = "DATABASE_CONNECTION_FAILED";
        public static final String CONSTRAINT_VIOLATION = "DATABASE_CONSTRAINT_VIOLATION";
        public static final String DUPLICATE_KEY = "DATABASE_DUPLICATE_KEY";
        public static final String TRANSACTION_FAILED = "DATABASE_TRANSACTION_FAILED";
        public static final String QUERY_FAILED = "DATABASE_QUERY_FAILED";
        
        private Database() {}
    }
    
    // ============= SYSTEM ERRORS =============
    public static final class System {
        public static final String INTERNAL_ERROR = "SYSTEM_INTERNAL_ERROR";
        public static final String CONFIGURATION_ERROR = "SYSTEM_CONFIG_ERROR";
        public static final String RESOURCE_NOT_AVAILABLE = "SYSTEM_RESOURCE_UNAVAILABLE";
        public static final String OPERATION_NOT_SUPPORTED = "SYSTEM_OPERATION_NOT_SUPPORTED";
        
        private System() {}
    }
}