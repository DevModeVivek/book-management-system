package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import java.util.List;

/**
 * Service interface for book management operations
 */
public interface IBookService {
    
    /**
     * Retrieve all books
     * @return List of all books
     */
    List<BookDTO> getAllBooks();
    
    /**
     * Get book by ID
     * @param id Book ID
     * @return Book DTO
     */
    BookDTO getBookById(Long id);
    
    /**
     * Create a new book
     * @param bookDTO Book data
     * @return Created book DTO
     */
    BookDTO createBook(BookDTO bookDTO);
    
    /**
     * Update an existing book
     * @param id Book ID
     * @param bookDTO Updated book data
     * @return Updated book DTO
     */
    BookDTO updateBook(Long id, BookDTO bookDTO);
    
    /**
     * Delete a book
     * @param id Book ID
     */
    void deleteBook(Long id);
    
    /**
     * Search books by title or author
     * @param query Search query
     * @return List of matching books
     */
    List<BookDTO> searchBooks(String query);
}