package com.vivek.commons.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base domain event for all events in the system
 * Event-driven architecture foundation
 */
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseDomainEvent {
    
    @JsonProperty("eventId")
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();
    
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("aggregateId")
    private String aggregateId;
    
    @JsonProperty("aggregateType")
    private String aggregateType;
    
    @JsonProperty("sourceService")
    private String sourceService;
    
    @JsonProperty("correlationId")
    private String correlationId;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @JsonProperty("version")
    @Builder.Default
    private String version = "1.0";
    
    /**
     * Get the routing key for this event
     */
    public abstract String getRoutingKey();
    
    /**
     * Get the exchange for this event
     */
    public abstract String getExchange();
}