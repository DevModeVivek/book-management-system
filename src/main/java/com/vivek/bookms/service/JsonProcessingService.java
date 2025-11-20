package com.vivek.bookms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ObjectMapper-based implementation of JSON processing service
 */
@Service
@RequiredArgsConstructor
public class JsonProcessingService implements IJsonProcessingService {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public JsonNode readTree(String jsonString) throws Exception {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        return objectMapper.readTree(jsonString);
    }
}