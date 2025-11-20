package com.vivek.bookms.repository;

import com.vivek.bookms.constants.AppConstants;
import com.vivek.bookms.entity.Book;
import com.vivek.bookms.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced Book repository interface extending BaseRepository with comprehensive book operations
 * Uses centralized constants and follows proper inheritance patterns
 */
public interface IBookRepository extends BaseRepository<Book> {
    
    // ============= ISBN-BASED OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Optional<Book> findByIsbnAndIsActiveTrue(@Param("isbn") String isbn);
    
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Book b " +
           "WHERE b.isbn = :isbn AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    boolean existsByIsbnAndIsActiveTrue(@Param("isbn") String isbn);
    
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Book b " +
           "WHERE b.isbn = :isbn AND b.id != :excludeId AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    boolean existsByIsbnAndIdNotAndIsActiveTrue(@Param("isbn") String isbn, @Param("excludeId") Long excludeId);
    
    // ============= ADVANCED SEARCH OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY b.title ASC")
    List<Book> searchByTitleOrAuthorAndIsActiveTrue(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Page<Book> searchByTitleOrAuthorAndIsActiveTruePaged(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY b.title ASC")
    List<Book> searchByTitleContainingAndIsActiveTrue(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true " +
           "ORDER BY b.author ASC")
    List<Book> searchByAuthorContainingAndIsActiveTrue(@Param("searchTerm") String searchTerm);
    
    // ============= FIELD-SPECIFIC FINDER OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) = LOWER(:title) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    List<Book> findByTitleIgnoreCaseAndIsActiveTrue(@Param("title") String title);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) = LOWER(:author) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByAuthorIgnoreCaseAndIsActiveTrue(@Param("author") String author);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) = LOWER(:genre) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByGenreIgnoreCaseAndIsActiveTrue(@Param("genre") String genre);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.publisher) = LOWER(:publisher) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByPublisherIgnoreCaseAndIsActiveTrue(@Param("publisher") String publisher);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.language) = LOWER(:language) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByLanguageIgnoreCaseAndIsActiveTrue(@Param("language") String language);
    
    // ============= DATE-BASED OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE b.publishedDate > :date AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findByPublishedDateAfterAndIsActiveTrue(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Book b WHERE b.publishedDate < :date AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findByPublishedDateBeforeAndIsActiveTrue(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Book b WHERE b.publishedDate BETWEEN :startDate AND :endDate AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findByPublishedDateBetweenAndIsActiveTrue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT b FROM Book b WHERE YEAR(b.publishedDate) = :year AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findByPublishedYearAndIsActiveTrue(@Param("year") int year);
    
    // ============= PRICE-BASED OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE b.price IS NOT NULL AND b.price > :minPrice AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.price ASC")
    List<Book> findByPriceGreaterThanAndIsActiveTrue(@Param("minPrice") BigDecimal minPrice);
    
    @Query("SELECT b FROM Book b WHERE b.price IS NOT NULL AND b.price < :maxPrice AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.price ASC")
    List<Book> findByPriceLessThanAndIsActiveTrue(@Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT b FROM Book b WHERE b.price IS NOT NULL AND b.price BETWEEN :minPrice AND :maxPrice AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.price ASC")
    List<Book> findByPriceBetweenAndIsActiveTrue(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT b FROM Book b WHERE b.price IS NULL AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByPriceIsNullAndIsActiveTrue();
    
    // ============= PAGE COUNT OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE b.pageCount IS NOT NULL AND b.pageCount > :minPages AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.pageCount ASC")
    List<Book> findByPageCountGreaterThanAndIsActiveTrue(@Param("minPages") Integer minPages);
    
    @Query("SELECT b FROM Book b WHERE b.pageCount IS NOT NULL AND b.pageCount < :maxPages AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.pageCount ASC")
    List<Book> findByPageCountLessThanAndIsActiveTrue(@Param("maxPages") Integer maxPages);
    
    @Query("SELECT b FROM Book b WHERE b.pageCount IS NOT NULL AND b.pageCount BETWEEN :minPages AND :maxPages AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.pageCount ASC")
    List<Book> findByPageCountBetweenAndIsActiveTrue(@Param("minPages") Integer minPages, @Param("maxPages") Integer maxPages);
    
    // ============= COMBINED FILTERING OPERATIONS =============
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) = LOWER(:author) AND LOWER(b.genre) = LOWER(:genre) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByAuthorAndGenreIgnoreCaseAndIsActiveTrue(@Param("author") String author, @Param("genre") String genre);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) = LOWER(:genre) AND LOWER(b.publisher) = LOWER(:publisher) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.title ASC")
    List<Book> findByGenreAndPublisherIgnoreCaseAndIsActiveTrue(@Param("genre") String genre, @Param("publisher") String publisher);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) = LOWER(:genre) AND b.publishedDate BETWEEN :startDate AND :endDate AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findByGenreAndPublishedDateBetweenAndIsActiveTrue(@Param("genre") String genre, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // ============= STATISTICS OPERATIONS =============
    
    @Query("SELECT COUNT(b) FROM Book b WHERE LOWER(b.genre) = LOWER(:genre) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countByGenreIgnoreCaseAndIsActiveTrue(@Param("genre") String genre);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE LOWER(b.author) = LOWER(:author) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countByAuthorIgnoreCaseAndIsActiveTrue(@Param("author") String author);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE LOWER(b.publisher) = LOWER(:publisher) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countByPublisherIgnoreCaseAndIsActiveTrue(@Param("publisher") String publisher);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.publishedDate > :date AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countByPublishedDateAfterAndIsActiveTrue(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.price IS NOT NULL AND b.price BETWEEN :minPrice AND :maxPrice AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    long countByPriceBetweenAndIsActiveTrue(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // ============= ADVANCED ANALYTICS OPERATIONS =============
    
    @Query("SELECT b.genre, COUNT(b) FROM Book b WHERE b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true GROUP BY b.genre ORDER BY COUNT(b) DESC")
    List<Object[]> findGenreStatistics();
    
    @Query("SELECT b.author, COUNT(b) FROM Book b WHERE b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true GROUP BY b.author ORDER BY COUNT(b) DESC")
    List<Object[]> findAuthorStatistics();
    
    @Query("SELECT b.publisher, COUNT(b) FROM Book b WHERE b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true GROUP BY b.publisher ORDER BY COUNT(b) DESC")
    List<Object[]> findPublisherStatistics();
    
    @Query("SELECT YEAR(b.publishedDate) as year, COUNT(b) FROM Book b WHERE b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true GROUP BY YEAR(b.publishedDate) ORDER BY year DESC")
    List<Object[]> findPublicationYearStatistics();
    
    @Query("SELECT AVG(b.price) FROM Book b WHERE b.price IS NOT NULL AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    BigDecimal findAveragePrice();
    
    @Query("SELECT MIN(b.price) FROM Book b WHERE b.price IS NOT NULL AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    BigDecimal findMinPrice();
    
    @Query("SELECT MAX(b.price) FROM Book b WHERE b.price IS NOT NULL AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    BigDecimal findMaxPrice();
    
    @Query("SELECT AVG(b.pageCount) FROM Book b WHERE b.pageCount IS NOT NULL AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true")
    Double findAveragePageCount();
    
    // ============= TOP QUERIES FOR RECOMMENDATIONS =============
    
    @Query("SELECT b FROM Book b WHERE b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b." + AppConstants.Database.CREATED_AT_COLUMN + " DESC")
    List<Book> findRecentlyAddedBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.publishedDate > :date AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findRecentlyPublishedBooks(@Param("date") LocalDate date, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) = LOWER(:genre) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findBooksByGenreOrderByNewest(@Param("genre") String genre, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) = LOWER(:author) AND b." + AppConstants.Database.IS_ACTIVE_COLUMN + " = true ORDER BY b.publishedDate DESC")
    List<Book> findBooksByAuthorOrderByNewest(@Param("author") String author, Pageable pageable);
}