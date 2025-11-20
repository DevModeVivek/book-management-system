package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Enhanced Book service interface extending ICrudService with comprehensive book-specific operations
 */
public interface IBookService extends ICrudService<BookDTO, Long> {
    
    // ============= SEARCH OPERATIONS =============
    
    /**
     * Search books by title or author
     * @param query Search query
     * @return List of matching books
     */
    List<BookDTO> searchBooks(String query);
    
    /**
     * Search books by title or author with pagination
     * @param query Search query
     * @param pageable Pagination information
     * @return Page of matching books
     */
    Page<BookDTO> searchBooks(String query, Pageable pageable);
    
    // ============= FIND OPERATIONS =============
    
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
     * @return Optional BookDTO
     */
    Optional<BookDTO> findByIsbn(String isbn);
    
    /**
     * Find books by genre (case insensitive)
     * @param genre Book genre
     * @return List of matching books
     */
    List<BookDTO> findByGenre(String genre);
    
    /**
     * Find books by publisher (case insensitive)
     * @param publisher Book publisher
     * @return List of matching books
     */
    List<BookDTO> findByPublisher(String publisher);
    
    // ============= SOFT DELETE OPERATIONS =============
    
    /**
     * Soft delete a book by ID
     * @param id Book ID
     */
    void softDelete(Long id);
    
    /**
     * Restore a soft-deleted book by ID
     * @param id Book ID
     */
    void restore(Long id);
    
    // ============= EXISTENCE CHECK OPERATIONS =============
    
    /**
     * Check if a book exists with the given ISBN
     * @param isbn Book ISBN
     * @return true if exists, false otherwise
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * Check if a book exists with the given ISBN excluding a specific ID
     * @param isbn Book ISBN
     * @param excludeId ID to exclude from check
     * @return true if exists, false otherwise
     */
    boolean existsByIsbnAndIdNot(String isbn, Long excludeId);
}