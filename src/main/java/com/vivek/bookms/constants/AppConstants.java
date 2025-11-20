package com.vivek.bookms.constants;

import lombok.experimental.UtilityClass;

/**
 * Application-wide constants for the Book Management System
 * Centralized location for all constant values to ensure consistency and maintainability
 */
@UtilityClass
public class AppConstants {

    // ============= APPLICATION METADATA =============
    public static final String APPLICATION_NAME = "Book Management System";
    public static final String APPLICATION_VERSION = "1.0.0";
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;
    
    // ============= VALIDATION CONSTANTS =============
    @UtilityClass
    public static class Validation {
        // Book-related validations
        public static final int TITLE_MIN_LENGTH = 1;
        public static final int TITLE_MAX_LENGTH = 200;
        public static final int AUTHOR_MIN_LENGTH = 1;
        public static final int AUTHOR_MAX_LENGTH = 100;
        public static final int DESCRIPTION_MAX_LENGTH = 2000;
        public static final int GENRE_MAX_LENGTH = 50;
        public static final int PUBLISHER_MAX_LENGTH = 100;
        public static final int LANGUAGE_MAX_LENGTH = 30;
        public static final int ISBN_MIN_LENGTH = 10;
        public static final int ISBN_MAX_LENGTH = 20;
        
        // Numeric validations
        public static final int PAGE_COUNT_MIN = 1;
        public static final int PAGE_COUNT_MAX = 10000;
        public static final String PRICE_MIN = "0.01";
        public static final String PRICE_MAX = "999999.99";
        
        // Search validations
        public static final int SEARCH_QUERY_MIN_LENGTH = 1;
        public static final int SEARCH_QUERY_MAX_LENGTH = 200;
        public static final int MIN_SEARCH_LENGTH = 1;
        public static final int MAX_SEARCH_LENGTH = 200;
        
        // Pagination validations
        public static final int MAX_PAGE_SIZE = 100;
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MIN_PAGE_SIZE = 1;
    }
    
    // ============= REGEX PATTERNS =============
    @UtilityClass
    public static class RegexPatterns {
        public static final String TEXT_WITH_PUNCTUATION = "^[\\p{L}0-9 .,'-]+$";
        public static final String ALPHANUMERIC_WITH_SPACES = "^[\\p{L}0-9 ]+$";
        public static final String ALPHANUMERIC = "^[\\p{L}0-9]+$";
        public static final String SEARCH_QUERY = "^[\\p{L}0-9 .,'-]+$";
        public static final String ISBN_10 = "^(?:ISBN(?:-10)?:?\\s*)?(?=[0-9X]{10}$|(?=(?:[0-9]+[-\\s])*[0-9X]$)[0-9]{1,5}[-\\s]?[0-9]+[-\\s]?[0-9]+[-\\s]?[0-9X]$).*$";
        public static final String ISBN_13 = "^(?:ISBN(?:-13)?:?\\s*)?(?=97[89][0-9]{10}$|(?=(?:[0-9]+[-\\s])*[0-9]$)(?:[0-9]+[-\\s]?){3}[0-9]$).*$";
    }
    
    // ============= ERROR MESSAGES =============
    @UtilityClass
    public static class ErrorMessages {
        // Book-related errors
        public static final String BOOK_NOT_FOUND = "Book not found";
        public static final String BOOK_NOT_FOUND_BY_ID = "Book not found with ID: %s";
        public static final String BOOK_SAVE_FAILED = "Failed to save book";
        public static final String BOOK_UPDATE_FAILED = "Failed to update book";
        public static final String BOOK_DELETE_FAILED = "Failed to delete book";
        public static final String BOOK_FETCH_FAILED = "Failed to fetch book";
        public static final String BOOK_SEARCH_FAILED = "Failed to search books";
        public static final String DUPLICATE_ISBN = "Book with ISBN %s already exists";
        
        // Validation errors
        public static final String VALIDATION_FAILED = "Validation failed";
        public static final String INVALID_INPUT = "Invalid input provided";
        public static final String INVALID_REQUEST = "Invalid request";
        public static final String REQUIRED_FIELD_MISSING = "Required field '%s' is missing";
        public static final String FIELD_TOO_LONG = "Field '%s' exceeds maximum length of %d characters";
        public static final String FIELD_TOO_SHORT = "Field '%s' is below minimum length of %d characters";
        
        // System errors
        public static final String INTERNAL_SERVER_ERROR = "Internal server error occurred";
        public static final String DATABASE_ERROR = "Database operation failed";
        public static final String EXTERNAL_API_UNAVAILABLE = "External service temporarily unavailable";
        public static final String ACCESS_DENIED = "Access denied";
        public static final String UNAUTHORIZED = "Unauthorized access";
        
        // Resource errors
        public static final String RESOURCE_NOT_FOUND = "Resource not found";
        public static final String RESOURCE_ALREADY_EXISTS = "Resource already exists";
        public static final String RESOURCE_CONFLICT = "Resource conflict detected";
    }
    
    // ============= SUCCESS MESSAGES =============
    @UtilityClass
    public static class SuccessMessages {
        public static final String BOOK_CREATED = "Book created successfully";
        public static final String BOOK_UPDATED = "Book updated successfully";
        public static final String BOOK_DELETED = "Book deleted successfully";
        public static final String BOOK_RESTORED = "Book restored successfully";
        public static final String OPERATION_SUCCESSFUL = "Operation completed successfully";
    }
    
    // ============= LOGGING CONSTANTS =============
    @UtilityClass
    public static class Logging {
        public static final String CATEGORY_CONTROLLER = "CONTROLLER";
        public static final String CATEGORY_SERVICE = "SERVICE";
        public static final String CATEGORY_REPOSITORY = "REPOSITORY";
        public static final String CATEGORY_MAPPER = "MAPPER";
        public static final String CATEGORY_VALIDATION = "VALIDATION";
        public static final String CATEGORY_SECURITY = "SECURITY";
        public static final String CATEGORY_EXTERNAL_API = "EXTERNAL_API";
        
        // Log patterns
        public static final String SERVICE_LOG_PATTERN = "[{}] {} - {}";
        public static final String ERROR_LOG_PATTERN = "[{}] {} - Error: {} - Code: {} - Message: {}";
        public static final String SECURITY_MARKER = "SECURITY_EVENT";
        
        // Log messages
        public static final String OPERATION_STARTED = "Operation started: {}";
        public static final String OPERATION_COMPLETED = "Operation completed: {}";
        public static final String OPERATION_FAILED = "Operation failed: {}";
    }
    
    // ============= SECURITY CONSTANTS =============
    @UtilityClass
    public static class Security {
        public static final String ROLE_ADMIN = "ADMIN";
        public static final String ROLE_USER = "USER";
        public static final String ROLE_MODERATOR = "MODERATOR";
        
        public static final String AUTHORITY_READ = "READ";
        public static final String AUTHORITY_WRITE = "WRITE";
        public static final String AUTHORITY_DELETE = "DELETE";
        public static final String AUTHORITY_ADMIN = "ADMIN";
    }
    
    // ============= API CONSTANTS =============
    @UtilityClass
    public static class Api {
        // Endpoints
        public static final String BOOKS_ENDPOINT = "/books";
        public static final String SEARCH_ENDPOINT = "/search";
        public static final String EXTERNAL_SEARCH_ENDPOINT = "/external-search";
        
        // Headers
        public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
        public static final String REQUEST_ID_HEADER = "X-Request-ID";
        public static final String API_VERSION_HEADER = "X-API-Version";
        
        // Parameters
        public static final String PAGE_PARAM = "page";
        public static final String SIZE_PARAM = "size";
        public static final String SORT_PARAM = "sort";
        public static final String QUERY_PARAM = "query";
    }
    
    // ============= DATABASE CONSTANTS =============
    @UtilityClass
    public static class Database {
        // Table names
        public static final String BOOKS_TABLE = "books";
        public static final String AUDIT_TABLE = "audit_log";
        
        // Common column names
        public static final String ID_COLUMN = "id";
        public static final String CREATED_AT_COLUMN = "created_at";
        public static final String UPDATED_AT_COLUMN = "updated_at";
        public static final String CREATED_BY_COLUMN = "created_by";
        public static final String UPDATED_BY_COLUMN = "updated_by";
        public static final String IS_ACTIVE_COLUMN = "is_active";
        public static final String VERSION_COLUMN = "version";
        
        // Sequence names
        public static final String BOOK_SEQUENCE = "book_seq";
    }
    
    // ============= CACHING CONSTANTS =============
    @UtilityClass
    public static class Cache {
        public static final String BOOKS_CACHE = "books";
        public static final String SEARCH_CACHE = "book_search";
        public static final String COUNT_CACHE = "book_count";
        public static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour
        public static final int SEARCH_TTL_SECONDS = 1800; // 30 minutes
    }
    
    // ============= GENERAL CONTEXTS =============
    public static final String BOOK_VALIDATION_CONTEXT = "book_validation";
    public static final String GENERAL_ERROR_CONTEXT = "general_error";
    public static final String SEARCH_CONTEXT = "search_operation";
    public static final String CRUD_CONTEXT = "crud_operation";
}