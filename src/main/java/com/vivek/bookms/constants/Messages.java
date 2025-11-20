package com.vivek.bookms.constants;

/**
 * Application-wide message constants for consistent messaging across all layers
 */
public final class Messages {
    
    // Success Messages
    public static final String BOOK_CREATED_SUCCESSFULLY = "Book created successfully";
    public static final String BOOK_UPDATED_SUCCESSFULLY = "Book updated successfully";
    public static final String BOOK_DELETED_SUCCESSFULLY = "Book deleted successfully";
    public static final String BOOK_RETRIEVED_SUCCESSFULLY = "Book retrieved successfully";
    public static final String BOOKS_RETRIEVED_SUCCESSFULLY = "Books retrieved successfully";
    public static final String BOOK_RESTORED_SUCCESSFULLY = "Book restored successfully";
    public static final String OPERATION_SUCCESSFUL = "Operation completed successfully";
    
    // Error Messages
    public static final String BOOK_NOT_FOUND = "Book not found with ID: {0}";
    public static final String BOOK_NOT_FOUND_BY_ISBN = "Book not found with ISBN: {0}";
    public static final String NO_BOOKS_FOUND = "No books found";
    public static final String BOOK_ALREADY_EXISTS = "Book already exists with ISBN: {0}";
    public static final String DUPLICATE_ISBN = "Book with ISBN {0} already exists";
    
    // Validation Messages
    public static final String TITLE_REQUIRED = "Title is required";
    public static final String TITLE_LENGTH_INVALID = "Title must be between {0} and {1} characters";
    public static final String AUTHOR_REQUIRED = "Author is required";
    public static final String AUTHOR_LENGTH_INVALID = "Author must be between {0} and {1} characters";
    public static final String ISBN_REQUIRED = "ISBN is required";
    public static final String ISBN_INVALID_FORMAT = "Invalid ISBN format";
    public static final String ISBN_ALREADY_EXISTS = "ISBN already exists: {0}";
    public static final String PUBLISHED_DATE_REQUIRED = "Published date is required";
    public static final String PUBLISHED_DATE_FUTURE = "Published date cannot be in the future";
    public static final String PRICE_POSITIVE = "Price must be positive";
    public static final String PAGE_COUNT_RANGE = "Page count must be between {0} and {1}";
    public static final String DESCRIPTION_LENGTH_INVALID = "Description cannot exceed {0} characters";
    public static final String GENRE_LENGTH_INVALID = "Genre cannot exceed {0} characters";
    public static final String PUBLISHER_LENGTH_INVALID = "Publisher cannot exceed {0} characters";
    public static final String LANGUAGE_LENGTH_INVALID = "Language cannot exceed {0} characters";
    public static final String VALIDATION_ERROR = "Validation failed";
    
    // General Messages
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String UNAUTHORIZED = "Unauthorized access";
    public static final String DATABASE_ERROR = "Database operation failed";
    
    // External API Messages
    public static final String EXTERNAL_API_ERROR = "External API error occurred";
    public static final String GOOGLE_BOOKS_API_ERROR = "Error fetching data from Google Books API";
    public static final String EXTERNAL_SEARCH_SUCCESS = "External search completed successfully";
    public static final String NO_EXTERNAL_BOOKS_FOUND = "No external books found for: {0}";
    
    // Log Messages
    public static final String REQUEST_RECEIVED = "Request received: {0}";
    public static final String REQUEST_PROCESSED = "Request processed: {0}";
    public static final String ERROR_OCCURRED = "Error in {0}: {1}";
    
    private Messages() {
        // Prevent instantiation
    }
}