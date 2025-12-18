package com.vivek.commons.messaging.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.event.BaseDomainEvent;
import com.vivek.commons.messaging.IEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RabbitMQ Event Publisher Implementation
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitEventPublisher implements IEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public void publishEvent(BaseDomainEvent event) {
        publishEvent(event, event.getExchange(), event.getRoutingKey());
    }
    
    @Override
    public void publishEvent(BaseDomainEvent event, String exchange, String routingKey) {
        try {
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "RabbitEventPublisher.publishEvent",
                    String.format("Publishing event: %s to exchange: %s with routing key: %s",
                            event.getEventType(), exchange, routingKey));
            
            // Serialize event to JSON
            String eventJson = objectMapper.writeValueAsString(event);
            
            // Create message with properties
            Message message = MessageBuilder
                    .withBody(eventJson.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setHeader(AppConstants.Messaging.CORRELATION_ID_HEADER, event.getCorrelationId())
                    .setHeader(AppConstants.Messaging.EVENT_TYPE_HEADER, event.getEventType())
                    .setHeader(AppConstants.Messaging.SOURCE_SERVICE_HEADER, event.getSourceService())
                    .setHeader(AppConstants.Messaging.TIMESTAMP_HEADER, event.getTimestamp().toString())
                    .setMessageId(event.getEventId())
                    .setTimestamp(java.util.Date.from(event.getTimestamp()
                            .atZone(java.time.ZoneId.systemDefault()).toInstant()))
                    .build();
            
            // Publish message
            rabbitTemplate.send(exchange, routingKey, message);
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "RabbitEventPublisher.publishEvent",
                    String.format("Successfully published event: %s with ID: %s",
                            event.getEventType(), event.getEventId()));
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    "RabbitEventPublisher.publishEvent",
                    "EVENT_PUBLISH_FAILED", e.getMessage());
            
            // In a production system, you might want to implement a dead letter mechanism
            // or store failed events for later retry
            throw new RuntimeException("Failed to publish event: " + event.getEventType(), e);
        }
    }
    
    @Override
    public void publishEventWithRetry(BaseDomainEvent event, int maxRetries) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxRetries) {
            try {
                publishEvent(event);
                return; // Success, exit retry loop
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                log.warn(AppConstants.Logging.SERVICE_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_SERVICE,
                        "RabbitEventPublisher.publishEventWithRetry",
                        String.format("Failed to publish event (attempt %d/%d): %s", 
                                attempts, maxRetries, e.getMessage()));
                
                if (attempts < maxRetries) {
                    try {
                        // Wait before retry (exponential backoff)
                        Thread.sleep(1000L * attempts);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        // All retries failed
        log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                "RabbitEventPublisher.publishEventWithRetry",
                "EVENT_PUBLISH_RETRY_EXHAUSTED", 
                String.format("Failed to publish event after %d attempts", maxRetries));
        
        throw new RuntimeException("Failed to publish event after " + maxRetries + " attempts", lastException);
    }
}