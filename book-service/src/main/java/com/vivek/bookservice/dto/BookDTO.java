package com.vivek.bookservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.common.validation.ValidISBN;
import com.vivek.commons.dto.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Book Data Transfer Object with comprehensive validation and Swagger documentation
 * Extends BaseDTO for common fields like ID, timestamps, and audit information
 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true, exclude = {"description"})
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Book data transfer object containing all book information")
public class BookDTO extends BaseDTO {
    
    @NotBlank(message = "Title is required and cannot be empty")
    @Size(min = AppConstants.Validation.TITLE_MIN_LENGTH, 
          max = AppConstants.Validation.TITLE_MAX_LENGTH, 
          message = "Title must be between {min} and {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, 
             message = "Title contains invalid characters")
    @EqualsAndHashCode.Include
    @Schema(description = "Book title", example = "Clean Code: A Handbook of Agile Software Craftsmanship", required = true)
    private String title;
    
    @NotBlank(message = "Author is required and cannot be empty")
    @Size(min = AppConstants.Validation.AUTHOR_MIN_LENGTH, 
          max = AppConstants.Validation.AUTHOR_MAX_LENGTH, 
          message = "Author must be between {min} and {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, 
             message = "Author name contains invalid characters")
    @Schema(description = "Book author name", example = "Robert C. Martin", required = true)
    private String author;
    
    @ValidISBN(message = "Invalid ISBN format. Must be a valid ISBN-10 or ISBN-13")
    @Size(min = AppConstants.Validation.ISBN_MIN_LENGTH, 
          max = AppConstants.Validation.ISBN_MAX_LENGTH, 
          message = "ISBN length must be between {min} and {max} characters")
    @EqualsAndHashCode.Include
    @Schema(description = "Book ISBN (10 or 13 digits)", example = "978-0132350884")
    private String isbn;
    
    @NotNull(message = "Published date is required")
    @PastOrPresent(message = "Published date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("publishedDate")
    @Schema(description = "Book publication date", example = "2008-08-01", required = true)
    private LocalDate publishedDate;
    
    @DecimalMin(value = AppConstants.Validation.PRICE_MIN, 
                message = "Price must be at least {value}")
    @DecimalMax(value = AppConstants.Validation.PRICE_MAX, 
                message = "Price cannot exceed {value}")
    @Digits(integer = 8, fraction = 2, 
            message = "Price must have at most 8 integer digits and 2 fractional digits")
    @Schema(description = "Book price in USD", example = "45.99", minimum = "0.01", maximum = "9999.99")
    private BigDecimal price;
    
    @Size(max = AppConstants.Validation.DESCRIPTION_MAX_LENGTH, 
          message = "Description cannot exceed {max} characters")
    @Schema(description = "Book description", example = "A comprehensive guide to writing clean, maintainable code")
    private String description;
    
    @Size(max = AppConstants.Validation.GENRE_MAX_LENGTH, 
          message = "Genre cannot exceed {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.ALPHANUMERIC_WITH_SPACES, 
             message = "Genre contains invalid characters")
    @Schema(description = "Book genre/category", example = "Programming")
    private String genre;
    
    @Size(max = AppConstants.Validation.PUBLISHER_MAX_LENGTH, 
          message = "Publisher cannot exceed {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.TEXT_WITH_PUNCTUATION, 
             message = "Publisher name contains invalid characters")
    @Schema(description = "Book publisher", example = "Pearson")
    private String publisher;
    
    @Min(value = AppConstants.Validation.PAGE_COUNT_MIN, 
         message = "Page count must be at least {value}")
    @Max(value = AppConstants.Validation.PAGE_COUNT_MAX, 
         message = "Page count cannot exceed {value}")
    @Schema(description = "Number of pages in the book", example = "464", minimum = "1", maximum = "10000")
    private Integer pageCount;
    
    @Size(max = AppConstants.Validation.LANGUAGE_MAX_LENGTH, 
          message = "Language cannot exceed {max} characters")
    @Pattern(regexp = AppConstants.RegexPatterns.ALPHANUMERIC_WITH_SPACES, 
             message = "Language contains invalid characters")
    @Schema(description = "Book language", example = "English", defaultValue = "English")
    private String language;
    
    // ============= ALTERNATIVE JSON PROPERTIES =============
    
    /**
     * Alternative field name for publication date to match service expectations
     */
    @JsonProperty("publicationDate")
    public LocalDate getPublicationDate() {
        return this.publishedDate;
    }
    
    public void setPublicationDate(LocalDate publicationDate) {
        this.publishedDate = publicationDate;
    }
    
    // ============= BUSINESS METHODS =============
    
    /**
     * Check if the book has a price set
     */
    @JsonIgnore
    @Schema(description = "Formatted price with currency symbol", example = "$45.99", accessMode = Schema.AccessMode.READ_ONLY)
    public boolean hasPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if the book has page count information
     */
    @JsonIgnore
    public boolean hasPageCount() {
        return pageCount != null && pageCount > 0;
    }
    
    /**
     * Get formatted price as string
     */
    @JsonIgnore
    public String getFormattedPrice() {
        return hasPrice() ? String.format("$%.2f", price) : "Price not available";
    }
    
    /**
     * Get book age in years from publication date
     */
    @JsonIgnore
    public int getBookAgeInYears() {
        if (publishedDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - publishedDate.getYear();
    }
    
    /**
     * Check if this book is newly published (within last 2 years)
     */
    @JsonIgnore
    @Schema(description = "Whether the book was published within the last 2 years", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    public boolean isNewlyPublished() {
        return getBookAgeInYears() <= 2;
    }
    
    /**
     * Get a short summary for display
     */
    @JsonIgnore
    public String getShortSummary() {
        return String.format("%s by %s (%s)", 
                title != null ? title : "Unknown Title",
                author != null ? author : "Unknown Author",
                publishedDate != null ? publishedDate.getYear() : "Unknown Year");
    }
    
    /**
     * Check if book is a lengthy read (more than 500 pages)
     */
    @Schema(description = "Whether the book is considered lengthy (>500 pages)", example = "false", accessMode = Schema.AccessMode.READ_ONLY)
    public boolean isLengthyRead() {
        return pageCount != null && pageCount > 500;
    }
    
    /**
     * Get book category based on page count
     */
    @Schema(description = "Book length category based on page count", example = "Medium", accessMode = Schema.AccessMode.READ_ONLY)
    public String getLengthCategory() {
        if (pageCount == null) {
            return "Unknown";
        }
        if (pageCount <= 200) {
            return "Short";
        } else if (pageCount <= 500) {
            return "Medium";
        } else {
            return "Long";
        }
    }
    
    // ============= ENHANCED VALIDATION METHODS =============
    
    /**
     * Validate all book fields according to business rules for creation
     */
    @Override
    public void validateForCreation() {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                "BookDTO.validateForCreation",
                this.toLogString());
        
        // Call parent validation
        super.validateForCreation();
        
        validateRequiredFields();
        validateBusinessRules();
        normalize();
        
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                "BookDTO.validateForCreation",
                "Validation completed successfully");
    }
    
    /**
     * Validate all book fields for update operations
     */
    @Override
    public void validateForUpdate() {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                "BookDTO.validateForUpdate",
                this.toLogString());
        
        // Call parent validation
        super.validateForUpdate();
        
        validateRequiredFields();
        validateBusinessRules();
        normalize();
        
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                "BookDTO.validateForUpdate",
                "Validation completed successfully");
    }
    
    /**
     * Validate required fields using centralized validation
     */
    private void validateRequiredFields() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                String.format(AppConstants.ErrorMessages.REQUIRED_FIELD_MISSING, "title"));
        }
        
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException(
                String.format(AppConstants.ErrorMessages.REQUIRED_FIELD_MISSING, "author"));
        }
        
        if (publishedDate == null) {
            throw new IllegalArgumentException(
                String.format(AppConstants.ErrorMessages.REQUIRED_FIELD_MISSING, "publishedDate"));
        }
    }
    
    /**
     * Validate business rules using centralized validation
     */
    private void validateBusinessRules() {
        // Validate published date is not in future
        if (publishedDate != null && publishedDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Published date cannot be in the future");
        }
        
        // Validate price is positive if provided
        if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        
        // Validate page count is positive if provided
        if (pageCount != null && pageCount <= 0) {
            throw new IllegalArgumentException("Page count must be positive");
        }
        
        // Validate string lengths using centralized constants
        validateStringField(title, "title", 
                AppConstants.Validation.TITLE_MIN_LENGTH, 
                AppConstants.Validation.TITLE_MAX_LENGTH);
        
        validateStringField(author, "author",
                AppConstants.Validation.AUTHOR_MIN_LENGTH,
                AppConstants.Validation.AUTHOR_MAX_LENGTH);
    }
    
    /**
     * Helper method to validate string field lengths
     */
    private void validateStringField(String value, String fieldName, int minLength, int maxLength) {
        if (value != null) {
            if (value.length() < minLength) {
                throw new IllegalArgumentException(
                    String.format(AppConstants.ErrorMessages.FIELD_TOO_SHORT, fieldName, minLength));
            }
            if (value.length() > maxLength) {
                throw new IllegalArgumentException(
                    String.format(AppConstants.ErrorMessages.FIELD_TOO_LONG, fieldName, maxLength));
            }
        }
    }
    
    /**
     * Clean and normalize fields
     */
    public void normalize() {
        title = normalizeString(title);
        author = normalizeString(author);
        description = normalizeString(description);
        genre = normalizeString(genre);
        publisher = normalizeString(publisher);
        language = normalizeString(language);
        
        // Normalize ISBN by removing spaces and hyphens
        if (isbn != null) {
            isbn = isbn.replaceAll("[\\s-]", "");
        }
        
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                "BookDTO.normalize",
                "Fields normalized successfully");
    }
    
    /**
     * Helper method to normalize string fields
     */
    private String normalizeString(String value) {
        return value != null ? value.trim() : null;
    }
    
    // ============= UTILITY METHODS =============
    
    /**
     * Get a detailed string representation for logging
     */
    @Override
    public String toLogString() {
        return String.format("BookDTO{id=%s, title='%s', author='%s', isbn='%s', active=%s}", 
                           getId(), title, author, isbn, isActive());
    }
    
    /**
     * Get display-friendly summary
     */
    @Override
    public String toDisplayString() {
        return String.format("Book #%s: %s %s",
                getId() != null ? getId() : "NEW",
                getShortSummary(),
                isActive() ? "(Active)" : "(Inactive)");
    }
    
    /**
     * Create a copy of this DTO using the builder pattern
     */
    public BookDTO copy() {
        return this.toBuilder().build();
    }
    
    /**
     * Create a new DTO for creation (without system fields)
     */
    public BookDTO copyForCreation() {
        BookDTO copy = this.toBuilder().build();
        copy.prepareForCreation();
        return copy;
    }
}