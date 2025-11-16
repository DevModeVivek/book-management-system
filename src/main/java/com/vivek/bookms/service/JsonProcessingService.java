package com.vivek.bookms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ObjectMapper-based implementation of JSON processing service
 */
@Service
public class JsonProcessingService implements IJsonProcessingService {
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    public JsonProcessingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public JsonNode readTree(String jsonString) throws Exception {
        return objectMapper.readTree(jsonString);
    }
}