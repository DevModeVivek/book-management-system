package com.vivek.bookms.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Book entity extending BaseEntity with book-specific properties
 */
@Entity
@Table(name = "books")
public class Book extends BaseEntity {
    
    @Column(nullable = false)
    @NotBlank(message = "Title cannot be blank")
    private String title;
    
    @Column(nullable = false)
    @NotBlank(message = "Author cannot be blank")
    private String author;
    
    @Column(unique = true)
    private String isbn;
    
    @Column(name = "published_date", nullable = false)
    @NotNull(message = "Published date is required")
    private LocalDate publishedDate;
    
    // Default constructor
    public Book() {
        super();
    }
    
    // Constructor with properties
    public Book(String title, String author, String isbn, LocalDate publishedDate) {
        super();
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
        return "Book{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publishedDate=" + publishedDate +
                '}';
    }
}
