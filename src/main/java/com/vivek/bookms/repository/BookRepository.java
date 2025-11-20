package com.vivek.bookms.repository;

import com.vivek.bookms.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Streamlined Book repository with essential book-specific operations
 */
@Repository
public interface BookRepository extends BaseRepository<Book> {
    
    // ISBN Operations
    Optional<Book> findByIsbnAndIsActiveTrue(String isbn);
    boolean existsByIsbnAndIsActiveTrue(String isbn);
    
    @Query("SELECT COUNT(b) > 0 FROM Book b WHERE b.isbn = :isbn AND b.id != :excludeId AND b.isActive = true")
    boolean existsByIsbnAndIdNotAndIsActiveTrue(@Param("isbn") String isbn, @Param("excludeId") Long excludeId);
    
    // Search Operations
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND b.isActive = true")
    List<Book> searchByTitleOrAuthorAndIsActiveTrue(@Param("query") String query);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND b.isActive = true")
    Page<Book> searchByTitleOrAuthorAndIsActiveTrue(@Param("query") String query, Pageable pageable);
    
    // Filtering Operations
    List<Book> findByGenreIgnoreCaseAndIsActiveTrue(String genre);
    List<Book> findByPublisherIgnoreCaseAndIsActiveTrue(String publisher);
    List<Book> findByPublishedDateAfterAndIsActiveTrue(LocalDate date);
    List<Book> findByPublishedDateBetweenAndIsActiveTrue(LocalDate startDate, LocalDate endDate);
}
