package com.vivek.commons.messaging;

import com.vivek.commons.event.BaseDomainEvent;

/**
 * Event Publisher Interface for Domain Events
 */
public interface IEventPublisher {
    
    /**
     * Publish a domain event
     * @param event The domain event to publish
     */
    void publishEvent(BaseDomainEvent event);
    
    /**
     * Publish a domain event with custom exchange and routing key
     * @param event The domain event to publish
     * @param exchange The exchange to publish to
     * @param routingKey The routing key to use
     */
    void publishEvent(BaseDomainEvent event, String exchange, String routingKey);
    
    /**
     * Publish an event with retry mechanism
     * @param event The domain event to publish
     * @param maxRetries Maximum number of retry attempts
     */
    void publishEventWithRetry(BaseDomainEvent event, int maxRetries);
}