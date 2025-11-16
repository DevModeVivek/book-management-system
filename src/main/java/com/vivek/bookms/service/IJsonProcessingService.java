package com.vivek.bookms.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for JSON processing operations
 * Abstracts JSON operations from concrete ObjectMapper implementation
 */
public interface IJsonProcessingService {
    
    /**
     * Parse JSON string to JsonNode
     * @param jsonString JSON string to parse
     * @return JsonNode representation
     * @throws Exception if parsing fails
     */
    JsonNode readTree(String jsonString) throws Exception;
}