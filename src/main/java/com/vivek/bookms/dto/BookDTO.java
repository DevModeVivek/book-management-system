package com.vivek.bookms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Book DTO extending BaseDTO with book-specific properties and validation
 */
public class BookDTO extends BaseDTO {
    
    @NotBlank(message = "Title is required and cannot be empty")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required and cannot be empty")
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;
    
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$", 
             message = "ISBN must be a valid format")
    private String isbn;
    
    @NotNull(message = "Published date is required")
    @PastOrPresent(message = "Published date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;
    
    // Default constructor
    public BookDTO() {
        super();
    }
    
    // Constructor with ID
    public BookDTO(Long id) {
        super(id);
    }
    
    // Full constructor
    public BookDTO(Long id, String title, String author, String isbn, LocalDate publishedDate) {
        super(id);
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publishedDate=" + publishedDate +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}