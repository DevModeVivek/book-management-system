package com.vivek.bookms.repository;

import com.vivek.bookms.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Custom query to search books by title
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Custom query to search books by title or author
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
}
