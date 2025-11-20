package com.vivek.bookms.util;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for building test data objects
 */
public class TestDataBuilder {

    public static class BookBuilder {
        private Book book;
        
        public BookBuilder() {
            this.book = new Book();
            // Set default values
            this.book.setTitle("Default Test Book");
            this.book.setAuthor("Default Test Author");
            this.book.setIsbn("9781234567897");  // Valid ISBN-13 format (13 digits)
            this.book.setPublishedDate(LocalDate.of(2023, 1, 1));
            this.book.setGenre("Fiction");
            this.book.setPageCount(250);
            this.book.setLanguage("English");
            this.book.setPublisher("Test Publisher");
            this.book.setDescription("A test book description");
            this.book.setCreatedAt(LocalDateTime.now());
            this.book.setUpdatedAt(LocalDateTime.now());
        }
        
        public BookBuilder withId(Long id) {
            this.book.setId(id);
            return this;
        }
        
        public BookBuilder withTitle(String title) {
            this.book.setTitle(title);
            return this;
        }
        
        public BookBuilder withAuthor(String author) {
            this.book.setAuthor(author);
            return this;
        }
        
        public BookBuilder withIsbn(String isbn) {
            this.book.setIsbn(isbn);
            return this;
        }
        
        public BookBuilder withPublishedDate(LocalDate publishedDate) {
            this.book.setPublishedDate(publishedDate);
            return this;
        }
        
        public BookBuilder withGenre(String genre) {
            this.book.setGenre(genre);
            return this;
        }
        
        public BookBuilder withPageCount(Integer pageCount) {
            this.book.setPageCount(pageCount);
            return this;
        }
        
        public BookBuilder withLanguage(String language) {
            this.book.setLanguage(language);
            return this;
        }
        
        public BookBuilder withPublisher(String publisher) {
            this.book.setPublisher(publisher);
            return this;
        }
        
        public BookBuilder withDescription(String description) {
            this.book.setDescription(description);
            return this;
        }
        
        public Book build() {
            return this.book;
        }
    }

    public static class BookDTOBuilder {
        private BookDTO bookDTO;
        
        public BookDTOBuilder() {
            this.bookDTO = new BookDTO();
            // Set default values
            this.bookDTO.setTitle("Default Test Book");
            this.bookDTO.setAuthor("Default Test Author");
            this.bookDTO.setIsbn("9781234567897");  // Valid ISBN-13 format (13 digits)
            this.bookDTO.setPublishedDate(LocalDate.of(2023, 1, 1));
            this.bookDTO.setGenre("Fiction");
            this.bookDTO.setPageCount(250);
            this.bookDTO.setLanguage("English");
            this.bookDTO.setPublisher("Test Publisher");
            this.bookDTO.setDescription("A test book description");
        }
        
        public BookDTOBuilder withId(Long id) {
            this.bookDTO.setId(id);
            return this;
        }
        
        public BookDTOBuilder withTitle(String title) {
            this.bookDTO.setTitle(title);
            return this;
        }
        
        public BookDTOBuilder withAuthor(String author) {
            this.bookDTO.setAuthor(author);
            return this;
        }
        
        public BookDTOBuilder withIsbn(String isbn) {
            this.bookDTO.setIsbn(isbn);
            return this;
        }
        
        public BookDTOBuilder withPublishedDate(LocalDate publishedDate) {
            this.bookDTO.setPublishedDate(publishedDate);
            return this;
        }
        
        public BookDTOBuilder withGenre(String genre) {
            this.bookDTO.setGenre(genre);
            return this;
        }
        
        public BookDTOBuilder withPageCount(Integer pageCount) {
            this.bookDTO.setPageCount(pageCount);
            return this;
        }
        
        public BookDTOBuilder withLanguage(String language) {
            this.bookDTO.setLanguage(language);
            return this;
        }
        
        public BookDTOBuilder withPublisher(String publisher) {
            this.bookDTO.setPublisher(publisher);
            return this;
        }
        
        public BookDTOBuilder withDescription(String description) {
            this.bookDTO.setDescription(description);
            return this;
        }
        
        public BookDTO build() {
            return this.bookDTO;
        }
    }

    // Static factory methods for easy access
    public static BookBuilder aBook() {
        return new BookBuilder();
    }

    public static BookDTOBuilder aBookDTO() {
        return new BookDTOBuilder();
    }

    // Predefined test data sets
    public static List<Book> createSampleBooks() {
        return Arrays.asList(
            aBook().withId(1L).withTitle("Spring Boot in Action").withAuthor("Craig Walls").withGenre("Programming").build(),
            aBook().withId(2L).withTitle("Clean Code").withAuthor("Robert C. Martin").withGenre("Programming").build(),
            aBook().withId(3L).withTitle("The Great Gatsby").withAuthor("F. Scott Fitzgerald").withGenre("Classic").build()
        );
    }

    public static List<BookDTO> createSampleBookDTOs() {
        return Arrays.asList(
            aBookDTO().withId(1L).withTitle("Spring Boot in Action").withAuthor("Craig Walls").withGenre("Programming").build(),
            aBookDTO().withId(2L).withTitle("Clean Code").withAuthor("Robert C. Martin").withGenre("Programming").build(),
            aBookDTO().withId(3L).withTitle("The Great Gatsby").withAuthor("F. Scott Fitzgerald").withGenre("Classic").build()
        );
    }
}