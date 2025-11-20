package com.vivek.bookms.core;

/**
 * Centralized messages for maximum reusability across all application modules
 */
public final class Messages {
    
    // General Success Messages
    public static final String SUCCESS = "Operation completed successfully";
    public static final String CREATED = "Resource created successfully";
    public static final String UPDATED = "Resource updated successfully";
    public static final String DELETED = "Resource deleted successfully";
    public static final String RETRIEVED = "Resource retrieved successfully";
    
    // Book-Specific Success Messages
    public static final String BOOK_CREATED_SUCCESS = "Book created successfully";
    public static final String BOOK_UPDATED_SUCCESS = "Book updated successfully";
    public static final String BOOK_DELETED_SUCCESS = "Book deleted successfully";
    public static final String BOOK_RETRIEVED_SUCCESS = "Book retrieved successfully";
    public static final String BOOKS_RETRIEVED_SUCCESS = "Books retrieved successfully";
    public static final String SEARCH_COMPLETED_SUCCESS = "Search completed successfully";
    public static final String EXTERNAL_SEARCH_SUCCESS = "External search completed successfully";
    
    // General Error Messages
    public static final String NOT_FOUND = "Resource not found with id: %s";
    public static final String ALREADY_EXISTS = "Resource already exists: %s";
    public static final String INVALID_INPUT = "Invalid input provided";
    public static final String VALIDATION_ERROR = "Validation failed";
    public static final String DATABASE_ERROR = "Database operation failed";
    public static final String EXTERNAL_API_ERROR = "External API call failed";
    
    // Book-Specific Error Messages
    public static final String BOOK_NOT_FOUND = "Book not found with id: %s";
    public static final String BOOKS_NOT_FOUND = "No books found";
    public static final String NO_BOOKS_FOUND_FOR_QUERY = "No books found for query: %s";
    public static final String NO_EXTERNAL_BOOKS_FOUND = "No external books found for: %s";
    public static final String BOOK_ALREADY_EXISTS = "Book already exists with ISBN: %s";
    public static final String INVALID_ISBN = "Invalid ISBN format: %s";
    
    // Validation Messages
    public static final String REQUIRED = "%s is required";
    public static final String INVALID_LENGTH = "%s must be between %d and %d characters";
    public static final String INVALID_RANGE = "%s must be between %d and %d";
    public static final String INVALID_FORMAT = "%s has invalid format";
    public static final String FUTURE_DATE = "%s cannot be in the future";
    public static final String TITLE_REQUIRED = "Book title is required";
    public static final String AUTHOR_REQUIRED = "Book author is required";
    public static final String ISBN_REQUIRED = "Book ISBN is required";
    public static final String PUBLISHED_DATE_REQUIRED = "Published date is required";
    
    // API Messages
    public static final String API_CALL_SUCCESS = "API call completed successfully";
    public static final String API_CALL_FAILED = "API call failed: %s";
    public static final String TIMEOUT_ERROR = "Request timeout occurred";
    public static final String CONNECTION_ERROR = "Connection error occurred";
    
    // Security Messages
    public static final String FORBIDDEN_ACCESS = "Access denied - insufficient permissions";
    public static final String UNAUTHORIZED = "Authentication required";
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred";
    
    // Log Messages
    public static final String REQUEST_RECEIVED = "Request received: %s";
    public static final String REQUEST_PROCESSED = "Request processed: %s";
    public static final String ERROR_OCCURRED = "Error in %s: %s";
    public static final String OPERATION_START = "Starting operation: %s";
    public static final String OPERATION_END = "Completed operation: %s";
    
    // Pagination Messages
    public static final String PAGE_RETRIEVED = "Page %d of %d retrieved successfully";
    public static final String INVALID_PAGE_SIZE = "Page size must be between 1 and %d";
    public static final String INVALID_PAGE_NUMBER = "Page number must be non-negative";
    
    private Messages() {
        // Prevent instantiation
    }
}