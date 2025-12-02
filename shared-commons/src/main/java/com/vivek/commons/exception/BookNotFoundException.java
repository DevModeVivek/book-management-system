package com.vivek.commons.exception;

import com.vivek.commons.constants.ErrorCodes;

/**
 * Book-specific domain exception with enhanced error handling
 * Extends DomainException for consistent error structure and eliminates code repetition
 */
public class BookNotFoundException extends DomainException {
    
    private BookNotFoundException(Builder builder) {
        super(builder);
    }
    
    /**
     * Create a standard book not found exception
     */
    public static BookNotFoundException forId(Long id) {
        return builder()
            .withErrorCode(ErrorCodes.Book.NOT_FOUND)
            .withCategory(ErrorCodes.Categories.DOMAIN)
            .withUserMessage(String.format("Book not found"))
            .withTechnicalMessage(String.format("Book with ID %s was not found", id))
            .withContext("bookId", id)
            .build();
    }
    
    /**
     * Create book not found exception for ISBN
     */
    public static BookNotFoundException forIsbn(String isbn) {
        return builder()
            .withErrorCode(ErrorCodes.Book.NOT_FOUND)
            .withCategory(ErrorCodes.Categories.DOMAIN)
            .withUserMessage("Book with the specified ISBN was not found")
            .withTechnicalMessage(String.format("Book with ISBN %s was not found", isbn))
            .withContext("isbn", isbn)
            .build();
    }
    
    /**
     * Create book not found exception with custom message
     */
    public static BookNotFoundException withMessage(String message) {
        return builder()
            .withErrorCode(ErrorCodes.Book.NOT_FOUND)
            .withCategory(ErrorCodes.Categories.DOMAIN)
            .withUserMessage(message)
            .withTechnicalMessage(message)
            .build();
    }
    
    /**
     * Create book already exists exception
     */
    public static BookNotFoundException alreadyExists(String isbn) {
        return builder()
            .withErrorCode(ErrorCodes.Book.ALREADY_EXISTS)
            .withCategory(ErrorCodes.Categories.DOMAIN)
            .withUserMessage("A book with this ISBN already exists")
            .withTechnicalMessage(String.format("Book with ISBN %s already exists", isbn))
            .withContext("isbn", isbn)
            .build();
    }
    
    /**
     * Get builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for BookNotFoundException
     */
    public static class Builder extends DomainException.Builder<Builder> {
        
        public Builder() {
            withErrorCode(ErrorCodes.Book.NOT_FOUND);
            withCategory(ErrorCodes.Categories.DOMAIN);
        }
        
        @Override
        protected Builder self() {
            return this;
        }
        
        @Override
        public BookNotFoundException build() {
            return new BookNotFoundException(this);
        }
    }
}