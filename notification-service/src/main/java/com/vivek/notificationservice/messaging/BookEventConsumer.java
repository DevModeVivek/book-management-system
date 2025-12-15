package com.vivek.notificationservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.event.book.BookCreatedEvent;
import com.vivek.commons.event.book.BookUpdatedEvent;
import com.vivek.commons.event.book.BookDeletedEvent;
import com.vivek.notificationservice.entity.Notification;
import com.vivek.notificationservice.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Book Event Consumer for Phase 3: Asynchronous Messaging
 * Listens to book domain events and creates appropriate notifications
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BookEventConsumer {
    
    private final INotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    @RabbitListener(queues = AppConstants.Messaging.BOOK_CREATED_QUEUE)
    public void handleBookCreatedEvent(
            @Payload String eventPayload,
            @Header(AppConstants.Messaging.CORRELATION_ID_HEADER) String correlationId,
            @Header(AppConstants.Messaging.EVENT_TYPE_HEADER) String eventType) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "BookEventConsumer.handleBookCreatedEvent",
                String.format("Received BookCreatedEvent with correlation ID: %s", correlationId));
        
        try {
            BookCreatedEvent event = objectMapper.readValue(eventPayload, BookCreatedEvent.class);
            
            // Create notification for book creation
            Notification notification = Notification.builder()
                    .recipientEmail("admin@bookmanagement.com") // In real app, get from admin settings
                    .recipientName("Admin")
                    .subject(String.format("New Book Added: %s", event.getTitle()))
                    .content(createBookCreatedNotificationContent(event))
                    .notificationType(Notification.NotificationType.BOOK_CREATED)
                    .referenceId(event.getBookId().toString())
                    .referenceType("BOOK")
                    .build();
            
            // Send the notification
            notificationService.sendNotification(notification);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "BookEventConsumer.handleBookCreatedEvent",
                    String.format("Successfully processed BookCreatedEvent for book ID: %d", event.getBookId()));
                    
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "BookEventConsumer.handleBookCreatedEvent",
                    "EVENT_PROCESSING_FAILED",
                    String.format("Failed to process BookCreatedEvent: %s", e.getMessage()));
            throw new RuntimeException("Failed to process BookCreatedEvent", e);
        }
    }
    
    @RabbitListener(queues = AppConstants.Messaging.BOOK_UPDATED_QUEUE)
    public void handleBookUpdatedEvent(
            @Payload String eventPayload,
            @Header(AppConstants.Messaging.CORRELATION_ID_HEADER) String correlationId,
            @Header(AppConstants.Messaging.EVENT_TYPE_HEADER) String eventType) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "BookEventConsumer.handleBookUpdatedEvent",
                String.format("Received BookUpdatedEvent with correlation ID: %s", correlationId));
        
        try {
            BookUpdatedEvent event = objectMapper.readValue(eventPayload, BookUpdatedEvent.class);
            
            // Create notification for book update
            Notification notification = Notification.builder()
                    .recipientEmail("admin@bookmanagement.com")
                    .recipientName("Admin")
                    .subject(String.format("Book Updated: %s", event.getTitle()))
                    .content(createBookUpdatedNotificationContent(event))
                    .notificationType(Notification.NotificationType.BOOK_CREATED) // Reusing enum
                    .referenceId(event.getBookId().toString())
                    .referenceType("BOOK")
                    .build();
            
            // Send the notification
            notificationService.sendNotification(notification);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "BookEventConsumer.handleBookUpdatedEvent",
                    String.format("Successfully processed BookUpdatedEvent for book ID: %d", event.getBookId()));
                    
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "BookEventConsumer.handleBookUpdatedEvent",
                    "EVENT_PROCESSING_FAILED",
                    String.format("Failed to process BookUpdatedEvent: %s", e.getMessage()));
            throw new RuntimeException("Failed to process BookUpdatedEvent", e);
        }
    }
    
    @RabbitListener(queues = AppConstants.Messaging.BOOK_DELETED_QUEUE)
    public void handleBookDeletedEvent(
            @Payload String eventPayload,
            @Header(AppConstants.Messaging.CORRELATION_ID_HEADER) String correlationId,
            @Header(AppConstants.Messaging.EVENT_TYPE_HEADER) String eventType) {
        
        log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "BookEventConsumer.handleBookDeletedEvent",
                String.format("Received BookDeletedEvent with correlation ID: %s", correlationId));
        
        try {
            BookDeletedEvent event = objectMapper.readValue(eventPayload, BookDeletedEvent.class);
            
            // Create notification for book deletion
            Notification notification = Notification.builder()
                    .recipientEmail("admin@bookmanagement.com")
                    .recipientName("Admin")
                    .subject(String.format("Book Deleted: %s", event.getTitle()))
                    .content(createBookDeletedNotificationContent(event))
                    .notificationType(Notification.NotificationType.BOOK_CREATED) // Reusing enum
                    .referenceId(event.getBookId().toString())
                    .referenceType("BOOK")
                    .build();
            
            // Send the notification
            notificationService.sendNotification(notification);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "BookEventConsumer.handleBookDeletedEvent",
                    String.format("Successfully processed BookDeletedEvent for book ID: %d", event.getBookId()));
                    
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "BookEventConsumer.handleBookDeletedEvent",
                    "EVENT_PROCESSING_FAILED",
                    String.format("Failed to process BookDeletedEvent: %s", e.getMessage()));
            throw new RuntimeException("Failed to process BookDeletedEvent", e);
        }
    }
    
    // ============= PRIVATE HELPER METHODS =============
    
    private String createBookCreatedNotificationContent(BookCreatedEvent event) {
        return String.format("""
                A new book has been added to the system:
                
                Title: %s
                Author: %s
                ISBN: %s
                Genre: %s
                Publisher: %s
                Published Date: %s
                Price: %s
                Created By: %s
                Created At: %s
                
                Please review the new addition to ensure it meets our quality standards.
                
                Best regards,
                Book Management System
                """,
                event.getTitle(),
                event.getAuthor(),
                event.getIsbn() != null ? event.getIsbn() : "N/A",
                event.getGenre() != null ? event.getGenre() : "N/A",
                event.getPublisher() != null ? event.getPublisher() : "N/A",
                event.getPublishedDate() != null ? event.getPublishedDate().toString() : "N/A",
                event.getPrice() != null ? String.format("$%.2f", event.getPrice()) : "N/A",
                event.getCreatedBy() != null ? event.getCreatedBy() : "System",
                event.getTimestamp().toString()
        );
    }
    
    private String createBookUpdatedNotificationContent(BookUpdatedEvent event) {
        return String.format("""
                A book has been updated in the system:
                
                Title: %s
                Author: %s
                ISBN: %s
                Genre: %s
                Publisher: %s
                Published Date: %s
                Price: %s
                Updated By: %s
                Updated At: %s
                
                Previous Values: %s
                
                Please review the changes to ensure accuracy.
                
                Best regards,
                Book Management System
                """,
                event.getTitle(),
                event.getAuthor(),
                event.getIsbn() != null ? event.getIsbn() : "N/A",
                event.getGenre() != null ? event.getGenre() : "N/A",
                event.getPublisher() != null ? event.getPublisher() : "N/A",
                event.getPublishedDate() != null ? event.getPublishedDate().toString() : "N/A",
                event.getPrice() != null ? String.format("$%.2f", event.getPrice()) : "N/A",
                event.getUpdatedBy() != null ? event.getUpdatedBy() : "System",
                event.getTimestamp().toString(),
                event.getPreviousValues() != null ? event.getPreviousValues() : "N/A"
        );
    }
    
    private String createBookDeletedNotificationContent(BookDeletedEvent event) {
        return String.format("""
                A book has been deleted from the system:
                
                Title: %s
                Author: %s
                ISBN: %s
                Deletion Type: %s
                Deleted By: %s
                Deleted At: %s
                
                This action has been logged for audit purposes.
                
                Best regards,
                Book Management System
                """,
                event.getTitle(),
                event.getAuthor(),
                event.getIsbn() != null ? event.getIsbn() : "N/A",
                event.getDeletionType(),
                event.getDeletedBy() != null ? event.getDeletedBy() : "System",
                event.getTimestamp().toString()
        );
    }
}