package com.vivek.bookms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate-based implementation of HTTP client service
 */
@Service
public class HttpClientService implements IHttpClientService {
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public HttpClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType) {
        return restTemplate.getForEntity(url, responseType);
    }
}