package com.vivek.bookms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate-based implementation of HTTP client service
 */
@Service
@RequiredArgsConstructor
public class HttpClientService implements IHttpClientService {
    
    private final RestTemplate restTemplate;
    
    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType) {
        if (url == null || responseType == null) {
            throw new IllegalArgumentException("URL and response type cannot be null");
        }
        return restTemplate.getForEntity(url, responseType);
    }
}