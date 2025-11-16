package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import java.util.List;

/**
 * Service interface for external Google Books API operations
 */
public interface IGoogleBooksService {
    
    /**
     * Search books by title
     * @param title Book title to search
     * @return List of matching books
     */
    List<BookDTO> searchBooksByTitle(String title);
    
    /**
     * Search books by author
     * @param author Author name to search
     * @return List of matching books
     */
    List<BookDTO> searchBooksByAuthor(String author);
    
    /**
     * General search in Google Books API
     * @param query Search query
     * @return List of matching books
     */
    List<BookDTO> searchBooks(String query);
}