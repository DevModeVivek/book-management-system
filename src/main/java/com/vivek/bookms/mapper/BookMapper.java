package com.vivek.bookms.mapper;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import org.springframework.stereotype.Component;

/**
 * Book mapper implementation with dependency injection support
 */
@Component
public class BookMapper implements IBookMapper {
    
    @Override
    public BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }
        
        BookDTO dto = new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            book.getPublishedDate()
        );
        
        // Set timestamps from base entity
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        
        return dto;
    }
    
    @Override
    public Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublishedDate(dto.getPublishedDate());
        
        // Set timestamps if available
        if (dto.getCreatedAt() != null) {
            book.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getUpdatedAt() != null) {
            book.setUpdatedAt(dto.getUpdatedAt());
        }
        
        return book;
    }
    
    @Override
    public void updateEntityFromDTO(BookDTO dto, Book book) {
        if (dto == null || book == null) {
            return;
        }
        
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setPublishedDate(dto.getPublishedDate());
        
        // Update timestamp will be handled by JPA @PreUpdate
    }
}