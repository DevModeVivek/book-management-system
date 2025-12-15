package com.vivek.commons.event.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.event.BaseDomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Book Deleted Domain Event
 * Published when a book is deleted
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BookDeletedEvent extends BaseDomainEvent {
    
    @JsonProperty("bookId")
    private Long bookId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("author")
    private String author;
    
    @JsonProperty("isbn")
    private String isbn;
    
    @JsonProperty("deletedBy")
    private String deletedBy;
    
    @JsonProperty("deletionType")
    private String deletionType; // SOFT or HARD
    
    public BookDeletedEvent(Long bookId, String title, String author, String isbn, 
                           String deletedBy, String deletionType, String correlationId) {
        super();
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.deletedBy = deletedBy;
        this.deletionType = deletionType;
        
        // Set base event properties
        setEventType("BookDeleted");
        setAggregateId(bookId.toString());
        setAggregateType("Book");
        setSourceService(AppConstants.Services.BOOK_SERVICE);
        setCorrelationId(correlationId);
    }
    
    @Override
    public String getRoutingKey() {
        return AppConstants.Messaging.BOOK_DELETED_ROUTING_KEY;
    }
    
    @Override
    public String getExchange() {
        return AppConstants.Messaging.BOOK_EXCHANGE;
    }
}