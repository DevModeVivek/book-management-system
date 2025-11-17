package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import java.util.List;

/**
 * Book service interface extending ICrudService with book-specific operations
 */
public interface IBookService extends ICrudService<BookDTO, Long> {
    
    /**
     * Search books by title or author
     * @param query Search query
     * @return List of matching books
     */
    List<BookDTO> searchBooks(String query);
    
    /**
     * Find books by title (case insensitive)
     * @param title Book title
     * @return List of matching books
     */
    List<BookDTO> findByTitle(String title);
    
    /**
     * Find books by author (case insensitive)
     * @param author Book author
     * @return List of matching books
     */
    List<BookDTO> findByAuthor(String author);
    
    /**
     * Find book by ISBN
     * @param isbn Book ISBN
     * @return BookDTO if found, null otherwise
     */
    BookDTO findByIsbn(String isbn);
}