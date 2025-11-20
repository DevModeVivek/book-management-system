package com.vivek.bookms.exception;

import lombok.Getter;

/**
 * Exception for book-related errors with Lombok optimization
 */
@Getter
public class BookNotFoundException extends RuntimeException {
    
    private final String errorCode;
    
    public BookNotFoundException(String message) {
        super(message);
        this.errorCode = "BOOK_NOT_FOUND";
    }
    
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BOOK_NOT_FOUND";
    }
    
    public BookNotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}