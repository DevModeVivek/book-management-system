package com.vivek.bookservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.bookservice.dto.BookDTO;
import com.vivek.commons.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleBooksService implements IGoogleBooksService {
    
    @Value("${external.google-books.base-url:https://www.googleapis.com/books/v1}")
    private String googleBooksApiUrl;
    
    @Value("${external.google-books.api-key:}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    @Cacheable(value = "external-books", key = "'title:' + #title")
    public List<BookDTO> searchBooksByTitle(String title) {
        log.info("Searching Google Books API for title: {}", title);
        return searchBooks("intitle:" + title);
    }
    
    @Override
    @Cacheable(value = "external-books", key = "'author:' + #author")
    public List<BookDTO> searchBooksByAuthor(String author) {
        log.info("Searching Google Books API for author: {}", author);
        return searchBooks("inauthor:" + author);
    }
    
    @Override
    @Cacheable(value = "external-books", key = "'search:' + #query")
    public List<BookDTO> searchBooks(String query) {
        try {
            log.info("Making request to Google Books API with query: {}", query);
            
            String fullApiUrl = googleBooksApiUrl + "/volumes";
            
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(fullApiUrl)
                .queryParam("q", query)
                .queryParam("maxResults", 10);
            
            if (apiKey != null && !apiKey.isEmpty()) {
                uriBuilder.queryParam("key", apiKey);
            }
            
            String url = uriBuilder.toUriString();
            log.debug("Request URL: {}", url);
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return parseGoogleBooksResponse(response.getBody());
            } else {
                throw ExternalApiException.googleBooksError("Failed to fetch data from Google Books API");
            }
            
        } catch (Exception e) {
            log.error("Error calling Google Books API: {}", e.getMessage(), e);
            throw ExternalApiException.googleBooksError("Error calling Google Books API: " + e.getMessage(), e);
        }
    }
    
    private List<BookDTO> parseGoogleBooksResponse(String responseBody) {
        List<BookDTO> books = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode itemsNode = rootNode.get("items");
            
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    JsonNode volumeInfo = item.get("volumeInfo");
                    if (volumeInfo != null) {
                        BookDTO book = parseVolumeInfo(volumeInfo);
                        if (book != null) {
                            books.add(book);
                        }
                    }
                }
            }
            
            log.info("Parsed {} books from Google Books API response", books.size());
            
        } catch (Exception e) {
            log.error("Error parsing Google Books API response: {}", e.getMessage(), e);
            throw ExternalApiException.googleBooksError("Error parsing Google Books API response");
        }
        
        return books;
    }
    
    private BookDTO parseVolumeInfo(JsonNode volumeInfo) {
        try {
            BookDTO book = new BookDTO();
            
            // Title
            JsonNode titleNode = volumeInfo.get("title");
            if (titleNode != null) {
                book.setTitle(titleNode.asText());
            }
            
            // Authors
            JsonNode authorsNode = volumeInfo.get("authors");
            if (authorsNode != null && authorsNode.isArray() && authorsNode.size() > 0) {
                book.setAuthor(authorsNode.get(0).asText());
            }
            
            // ISBN
            JsonNode industryIdentifiers = volumeInfo.get("industryIdentifiers");
            if (industryIdentifiers != null && industryIdentifiers.isArray()) {
                for (JsonNode identifier : industryIdentifiers) {
                    String type = identifier.get("type").asText();
                    if ("ISBN_13".equals(type) || "ISBN_10".equals(type)) {
                        book.setIsbn(identifier.get("identifier").asText());
                        break;
                    }
                }
            }
            
            // Published Date
            JsonNode publishedDateNode = volumeInfo.get("publishedDate");
            if (publishedDateNode != null) {
                String publishedDateStr = publishedDateNode.asText();
                LocalDate publishedDate = parsePublishedDate(publishedDateStr);
                book.setPublishedDate(publishedDate);
            }
            
            // Only return book if it has at least title and author
            if (book.getTitle() != null && book.getAuthor() != null) {
                return book;
            }
            
        } catch (Exception e) {
            log.warn("Error parsing individual book from Google Books API: {}", e.getMessage());
        }
        
        return null;
    }
    
    private LocalDate parsePublishedDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDate.now(); // Default to current date
        }
        
        try {
            // Try different date formats
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (dateStr.matches("\\d{4}-\\d{2}")) {
                return LocalDate.parse(dateStr + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (dateStr.matches("\\d{4}")) {
                return LocalDate.parse(dateStr + "-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            log.warn("Could not parse date: {}", dateStr);
        }
        
        return LocalDate.now(); // Default to current date if parsing fails
    }
}