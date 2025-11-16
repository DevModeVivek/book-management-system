package com.vivek.bookms.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for HTTP client operations
 * Abstracts HTTP calls from concrete RestTemplate implementation
 */
public interface IHttpClientService {
    
    /**
     * Perform GET request and return response entity
     * @param url The URL to call
     * @param responseType Response type class
     * @param <T> Response type
     * @return ResponseEntity with response
     */
    <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType);
}