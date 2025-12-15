package com.vivek.commons.event.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.event.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Book Created Domain Event
 * Published when a new book is created
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BookCreatedEvent extends BaseDomainEvent {
    
    @JsonProperty("bookId")
    private Long bookId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("author")
    private String author;
    
    @JsonProperty("isbn")
    private String isbn;
    
    @JsonProperty("publishedDate")
    private LocalDate publishedDate;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("genre")
    private String genre;
    
    @JsonProperty("publisher")
    private String publisher;
    
    @JsonProperty("createdBy")
    private String createdBy;
    
    public BookCreatedEvent(Long bookId, String title, String author, String isbn, 
                           LocalDate publishedDate, BigDecimal price, String genre, 
                           String publisher, String createdBy, String correlationId) {
        super();
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.price = price;
        this.genre = genre;
        this.publisher = publisher;
        this.createdBy = createdBy;
        
        // Set base event properties
        setEventType("BookCreated");
        setAggregateId(bookId.toString());
        setAggregateType("Book");
        setSourceService(AppConstants.Services.BOOK_SERVICE);
        setCorrelationId(correlationId);
    }
    
    @Override
    public String getRoutingKey() {
        return AppConstants.Messaging.BOOK_CREATED_ROUTING_KEY;
    }
    
    @Override
    public String getExchange() {
        return AppConstants.Messaging.BOOK_EXCHANGE;
    }
}