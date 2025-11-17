package com.vivek.bookms.repository;

import com.vivek.bookms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Book repository extending both JpaRepository and IBookRepository
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, IBookRepository {
    
    // Custom query methods with proper error handling
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(@Param("query") String title, @Param("query") String author);
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<Book> findByAuthorContainingIgnoreCase(@Param("author") String author);
    
    Optional<Book> findByIsbn(String isbn);
}
