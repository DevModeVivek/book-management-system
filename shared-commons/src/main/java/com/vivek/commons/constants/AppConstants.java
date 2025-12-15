package com.vivek.commons.constants;

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
        // Book endpoints
        public static final String BOOKS_ENDPOINT = "/books";
        public static final String SEARCH_ENDPOINT = "/search";
        public static final String EXTERNAL_SEARCH_ENDPOINT = "/external-search";
        
        // User endpoints
        public static final String USERS_ENDPOINT = "/users";
        public static final String LOGIN_ENDPOINT = "/auth/login";
        public static final String REGISTER_ENDPOINT = "/auth/register";
        public static final String PROFILE_ENDPOINT = "/profile";
        
        // Notification endpoints
        public static final String NOTIFICATIONS_ENDPOINT = "/notifications";
        public static final String SEND_ENDPOINT = "/send";
        public static final String TEMPLATES_ENDPOINT = "/templates";
        
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
        public static final String USERS_TABLE = "users";
        public static final String NOTIFICATIONS_TABLE = "notifications";
        public static final String NOTIFICATION_TEMPLATES_TABLE = "notification_templates";
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
        public static final String USER_SEQUENCE = "user_seq";
        public static final String NOTIFICATION_SEQUENCE = "notification_seq";
    }
    
    // ============= CACHING CONSTANTS =============
    @UtilityClass
    public static class Cache {
        public static final String BOOKS_CACHE = "books";
        public static final String USERS_CACHE = "users";
        public static final String NOTIFICATIONS_CACHE = "notifications";
        public static final String SEARCH_CACHE = "book_search";
        public static final String COUNT_CACHE = "book_count";
        public static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour
        public static final int SEARCH_TTL_SECONDS = 1800; // 30 minutes
    }
    
    // ============= MESSAGING CONSTANTS =============
    @UtilityClass
    public static class Messaging {
        // Exchange names
        public static final String BOOK_EXCHANGE = "book.exchange";
        public static final String USER_EXCHANGE = "user.exchange";
        public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
        public static final String DLQ_EXCHANGE = "dlq.exchange";
        
        // Queue names
        public static final String BOOK_CREATED_QUEUE = "book.created.queue";
        public static final String BOOK_UPDATED_QUEUE = "book.updated.queue";
        public static final String BOOK_DELETED_QUEUE = "book.deleted.queue";
        public static final String NOTIFICATION_SEND_QUEUE = "notification.send.queue";
        
        // Dead Letter Queue names
        public static final String BOOK_CREATED_DLQ = "book.created.dlq";
        public static final String BOOK_UPDATED_DLQ = "book.updated.dlq";
        public static final String BOOK_DELETED_DLQ = "book.deleted.dlq";
        public static final String NOTIFICATION_SEND_DLQ = "notification.send.dlq";
        
        // Routing keys
        public static final String BOOK_CREATED_ROUTING_KEY = "book.created";
        public static final String BOOK_UPDATED_ROUTING_KEY = "book.updated";
        public static final String BOOK_DELETED_ROUTING_KEY = "book.deleted";
        public static final String NOTIFICATION_SEND_ROUTING_KEY = "notification.send";
        
        // Headers
        public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
        public static final String EVENT_TYPE_HEADER = "X-Event-Type";
        public static final String SOURCE_SERVICE_HEADER = "X-Source-Service";
        public static final String TIMESTAMP_HEADER = "X-Timestamp";
        
        // Configuration values
        public static final int MAX_RETRY_COUNT = 3;
        public static final String RETRY_DELAY = "1000";
        public static final String MESSAGE_TTL = "86400000"; // 24 hours
    }
    
    // ============= SERVICE NAMES =============
    @UtilityClass
    public static class Services {
        public static final String BOOK_SERVICE = "book-service";
        public static final String USER_SERVICE = "user-service";
        public static final String NOTIFICATION_SERVICE = "notification-service";
    }
    
    // ============= GENERAL CONTEXTS =============
    public static final String BOOK_VALIDATION_CONTEXT = "book_validation";
    public static final String GENERAL_ERROR_CONTEXT = "general_error";
    public static final String SEARCH_CONTEXT = "search_operation";
    public static final String CRUD_CONTEXT = "crud_operation";
}