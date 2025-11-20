package com.vivek.bookms.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookManagementConstants {
    
    // Search constants
    public static final int MIN_SEARCH_LENGTH = 3;
    
    // Validation constants
    public static final class Validation {
        public static final int TITLE_MIN_LENGTH = 1;
        public static final int TITLE_MAX_LENGTH = 200;
        public static final int AUTHOR_MIN_LENGTH = 1;
        public static final int AUTHOR_MAX_LENGTH = 100;
        public static final int ISBN_MIN_LENGTH = 10;
        public static final int ISBN_MAX_LENGTH = 20;
        public static final int DESCRIPTION_MAX_LENGTH = 2000;
        public static final int GENRE_MAX_LENGTH = 50;
        public static final int PUBLISHER_MAX_LENGTH = 100;
        public static final int LANGUAGE_MAX_LENGTH = 30;
        public static final int PAGE_COUNT_MIN = 1;
        public static final int PAGE_COUNT_MAX = 10000;
        public static final int SEARCH_QUERY_MIN_LENGTH = 1;
        public static final int SEARCH_QUERY_MAX_LENGTH = 200;
        public static final int MAX_PAGE_SIZE = 100;
        public static final String PRICE_MIN = "0.01";
        public static final String PRICE_MAX = "999999.99";
        
        private Validation() {}
    }
    
    // Regex patterns
    public static final class RegexPatterns {
        public static final String TEXT_WITH_PUNCTUATION = "^[a-zA-Z0-9\\s\\p{Punct}]+$";
        public static final String ALPHANUMERIC = "^[a-zA-Z0-9\\s]+$";
        public static final String ISBN_10 = "^(?:ISBN(?:-10)?:?\\s*)?(?=[0-9X]{10}$|(?=(?:[0-9]+[-\\s])*[0-9X]$)[0-9]{1,5}[-\\s]?[0-9]+[-\\s]?[0-9]+[-\\s]?[0-9X]$).*$";
        public static final String ISBN_13 = "^(?:ISBN(?:-13)?:?\\s*)?(?=97[89][0-9]{10}$|(?=(?:[0-9]+[-\\s])*[0-9]$)(?:[0-9]+[-\\s]?){3}[0-9]$).*$";
        public static final String SEARCH_QUERY = "^[a-zA-Z0-9\\s\\p{Punct}]+$";
        
        private RegexPatterns() {}
    }
}