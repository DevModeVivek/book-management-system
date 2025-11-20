package com.vivek.bookms.core;

/**
 * Core constants for maximum reusability across all modules
 */
public final class AppConstants {
    
    // API Constants
    public static final String API_VERSION = "/api/v1";
    public static final String BOOKS_PATH = API_VERSION + "/books";
    
    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "id";
    
    // Validation Constants
    public static final int MIN_TITLE_LENGTH = 1;
    public static final int MAX_TITLE_LENGTH = 500;
    public static final int MIN_AUTHOR_LENGTH = 1;
    public static final int MAX_AUTHOR_LENGTH = 300;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int MAX_GENRE_LENGTH = 100;
    public static final int MAX_PUBLISHER_LENGTH = 200;
    public static final int MAX_LANGUAGE_LENGTH = 50;
    public static final int MIN_PAGE_COUNT = 1;
    public static final int MAX_PAGE_COUNT = 10000;
    
    // Cache Constants
    public static final String BOOKS_CACHE = "books";
    public static final long DEFAULT_CACHE_TTL = 3600; // 1 hour
    
    // Security Constants
    public static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/books/external/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
    
    // System Constants
    public static final String SYSTEM_USER = "system";
    public static final String ANONYMOUS_USER = "anonymous";
    
    private AppConstants() {
        // Prevent instantiation
    }
}