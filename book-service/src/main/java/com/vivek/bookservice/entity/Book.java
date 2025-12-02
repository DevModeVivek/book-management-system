package com.vivek.bookservice.entity;

import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.entity.base.BaseEntity;
import com.vivek.commons.common.validation.ValidISBN;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Enhanced Book entity extending BaseEntity with comprehensive validation using centralized constants
 * Follows proper inheritance pattern with Lombok SuperBuilder integration
 */
@Entity
@Table(name = AppConstants.Database.BOOKS_TABLE, 
       indexes = {
           @Index(name = "idx_book_isbn", columnList = "isbn", unique = true),
           @Index(name = "idx_book_title", columnList = "title"),
           @Index(name = "idx_book_author", columnList = "author"),
           @Index(name = "idx_book_active", columnList = AppConstants.Database.IS_ACTIVE_COLUMN)
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true, exclude = {"description"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Book extends BaseEntity {
    
    @Column(name = "title", nullable = false, length = AppConstants.Validation.TITLE_MAX_LENGTH)
    @NotBlank(message = "Title is required")
    @Size(min = AppConstants.Validation.TITLE_MIN_LENGTH, 
          max = AppConstants.Validation.TITLE_MAX_LENGTH,
          message = "Title must be between {min} and {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, 
             message = "Title contains invalid characters")
    private String title;
    
    @Column(name = "author", nullable = false, length = AppConstants.Validation.AUTHOR_MAX_LENGTH)
    @NotBlank(message = "Author is required")
    @Size(min = AppConstants.Validation.AUTHOR_MIN_LENGTH, 
          max = AppConstants.Validation.AUTHOR_MAX_LENGTH,
          message = "Author must be between {min} and {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, 
             message = "Author contains invalid characters")
    private String author;
    
    @Column(name = "isbn", unique = true, length = AppConstants.Validation.ISBN_MAX_LENGTH)
    @ValidISBN(message = "Invalid ISBN format. Must be a valid ISBN-10 or ISBN-13")
    @Size(min = AppConstants.Validation.ISBN_MIN_LENGTH, 
          max = AppConstants.Validation.ISBN_MAX_LENGTH,
          message = "ISBN must be between {min} and {max} characters")
    @EqualsAndHashCode.Include
    private String isbn;
    
    @Column(name = "published_date", nullable = false)
    @NotNull(message = "Published date is required")
    @PastOrPresent(message = "Published date cannot be in the future")
    private LocalDate publishedDate;
    
    @Column(name = "price", precision = 10, scale = 2)
    @DecimalMin(value = AppConstants.Validation.PRICE_MIN, 
                message = "Price must be at least {value}")
    @DecimalMax(value = AppConstants.Validation.PRICE_MAX, 
                message = "Price cannot exceed {value}")
    @Digits(integer = 8, fraction = 2, 
            message = "Price must have at most 8 integer digits and 2 fractional digits")
    private BigDecimal price;
    
    @Column(name = "description", length = AppConstants.Validation.DESCRIPTION_MAX_LENGTH)
    @Size(max = AppConstants.Validation.DESCRIPTION_MAX_LENGTH,
          message = "Description cannot exceed {max} characters")
    private String description;
    
    @Column(name = "genre", length = AppConstants.Validation.GENRE_MAX_LENGTH)
    @Size(max = AppConstants.Validation.GENRE_MAX_LENGTH,
          message = "Genre cannot exceed {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.ALPHANUMERIC_WITH_SPACES, 
             message = "Genre contains invalid characters")
    private String genre;
    
    @Column(name = "publisher", length = AppConstants.Validation.PUBLISHER_MAX_LENGTH)
    @Size(max = AppConstants.Validation.PUBLISHER_MAX_LENGTH,
          message = "Publisher cannot exceed {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, 
             message = "Publisher contains invalid characters")
    private String publisher;
    
    @Column(name = "page_count")
    @Min(value = AppConstants.Validation.PAGE_COUNT_MIN, 
         message = "Page count must be at least {value}")
    @Max(value = AppConstants.Validation.PAGE_COUNT_MAX, 
         message = "Page count cannot exceed {value}")
    private Integer pageCount;
    
    @Column(name = "language", length = AppConstants.Validation.LANGUAGE_MAX_LENGTH)
    @Size(max = AppConstants.Validation.LANGUAGE_MAX_LENGTH,
          message = "Language cannot exceed {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.ALPHANUMERIC_WITH_SPACES, 
             message = "Language contains invalid characters")
    private String language;
    
    // ============= BUSINESS METHODS =============
    
    /**
     * Check if the book has a price set
     */
    public boolean hasPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if the book has page count information
     */
    public boolean hasPageCount() {
        return pageCount != null && pageCount > 0;
    }
    
    /**
     * Get formatted price as string
     */
    public String getFormattedPrice() {
        return hasPrice() ? String.format("$%.2f", price) : "Price not available";
    }
    
    /**
     * Get book age in years from publication date
     */
    public int getBookAgeInYears() {
        if (publishedDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - publishedDate.getYear();
    }
    
    /**
     * Check if this book is newly published (within last 2 years)
     */
    public boolean isNewlyPublished() {
        return getBookAgeInYears() <= 2;
    }
    
    /**
     * Get a short summary for display
     */
    public String getShortSummary() {
        return String.format("%s by %s (%s)", 
                title != null ? title : "Unknown Title",
                author != null ? author : "Unknown Author",
                publishedDate != null ? publishedDate.getYear() : "Unknown Year");
    }
    
    /**
     * Get detailed information string for logging
     */
    @Override
    public String toLogString() {
        return String.format("Book{id=%s, title='%s', author='%s', isbn='%s', active=%s}", 
                getId(), title, author, isbn, isActive());
    }
    
    // ============= VALIDATION HELPER METHODS =============
    
    /**
     * Validate business rules for the book
     */
    public void validateBusinessRules() {
        if (publishedDate != null && publishedDate.isAfter(LocalDate.now())) {
            throw new IllegalStateException("Publication date cannot be in the future");
        }
        
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Price cannot be negative");
        }
        
        if (pageCount != null && pageCount <= 0) {
            throw new IllegalStateException("Page count must be positive");
        }
    }
    
    @PrePersist
    @PreUpdate
    protected void validateEntity() {
        validateBusinessRules();
        // Call parent validation
        super.prePersist();
    }
}
