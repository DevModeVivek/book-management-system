package com.vivek.bookms.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
    
    public static <T> ResponseEntity<Map<String, Object>> buildSuccessResponse(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
    
    public static <T> ResponseEntity<Map<String, Object>> buildSuccessResponse(T data) {
        return buildSuccessResponse(data, "Operation completed successfully");
    }
    
    public static ResponseEntity<Map<String, Object>> buildCreatedResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.CREATED.value());
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    public static ResponseEntity<Map<String, Object>> buildNoContentResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NO_CONTENT.value());
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
    
    public static ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("success", false);
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}