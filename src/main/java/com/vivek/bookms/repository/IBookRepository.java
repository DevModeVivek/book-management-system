package com.vivek.bookms.repository;

import com.vivek.bookms.entity.Book;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity operations
 * Defines data access contract without Spring Data JPA dependency
 */
public interface IBookRepository {
    
    // Basic CRUD operations
    Book save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();
    
    // Custom search operations
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    Optional<Book> findByIsbn(String isbn);
}