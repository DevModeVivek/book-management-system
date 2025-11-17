package com.vivek.bookms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksService implements IGoogleBooksService {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleBooksService.class);
    
    @Value("${external.google-books.base-url}")
    private String googleBooksApiUrl;
    
    @Value("${external.google-books.api-key:}")
    private String apiKey;
    
    // Using interfaces for external service dependencies
    private final IHttpClientService httpClientService;
    private final IJsonProcessingService jsonProcessingService;
    
    public GoogleBooksService(IHttpClientService httpClientService, IJsonProcessingService jsonProcessingService) {
        this.httpClientService = httpClientService;
        this.jsonProcessingService = jsonProcessingService;
    }
    
    @Override
    public List<BookDTO> searchBooksByTitle(String title) {
        logger.info("Searching Google Books API for title: {}", title);
        return searchBooks("intitle:" + title);
    }
    
    @Override
    public List<BookDTO> searchBooksByAuthor(String author) {
        logger.info("Searching Google Books API for author: {}", author);
        return searchBooks("inauthor:" + author);
    }
    
    @Override
    public List<BookDTO> searchBooks(String query) {
        try {
            logger.info("Making request to Google Books API with query: {}", query);
            
            String fullApiUrl = googleBooksApiUrl + "/volumes";
            
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(fullApiUrl)
                .queryParam("q", query)
                .queryParam("maxResults", 10);
            
            if (apiKey != null && !apiKey.isEmpty()) {
                uriBuilder.queryParam("key", apiKey);
            }
            
            String url = uriBuilder.toUriString();
            logger.debug("Request URL: {}", url);
            
            // Using interface-based HTTP client service
            ResponseEntity<String> response = httpClientService.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return parseGoogleBooksResponse(response.getBody());
            } else {
                throw new ExternalApiException("Failed to fetch data from Google Books API");
            }
            
        } catch (Exception e) {
            logger.error("Error calling Google Books API: {}", e.getMessage(), e);
            throw new ExternalApiException("Error calling Google Books API: " + e.getMessage());
        }
    }
    
    private List<BookDTO> parseGoogleBooksResponse(String responseBody) {
        List<BookDTO> books = new ArrayList<>();
        
        try {
            // Using interface-based JSON processing service
            JsonNode rootNode = jsonProcessingService.readTree(responseBody);
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
            
            logger.info("Parsed {} books from Google Books API response", books.size());
            
        } catch (Exception e) {
            logger.error("Error parsing Google Books API response: {}", e.getMessage(), e);
            throw new ExternalApiException("Error parsing Google Books API response");
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
            logger.warn("Error parsing individual book from Google Books API: {}", e.getMessage());
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
            logger.warn("Could not parse date: {}", dateStr);
        }
        
        return LocalDate.now(); // Default to current date if parsing fails
    }
}