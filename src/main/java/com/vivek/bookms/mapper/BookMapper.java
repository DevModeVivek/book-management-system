package com.vivek.bookms.mapper;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    
    public BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }
        
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            book.getPublishedDate()
        );
    }
    
    public Book toEntity(BookDTO bookDTO) {
        if (bookDTO == null) {
            return null;
        }
        
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublishedDate(bookDTO.getPublishedDate());
        
        return book;
    }
    
    public void updateEntityFromDTO(BookDTO bookDTO, Book book) {
        if (bookDTO == null || book == null) {
            return;
        }
        
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublishedDate(bookDTO.getPublishedDate());
    }
}