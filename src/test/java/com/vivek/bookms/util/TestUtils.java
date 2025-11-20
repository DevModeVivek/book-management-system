package com.vivek.bookms.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vivek.bookms.entity.Book;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Utility class for common test operations and data creation
 */
public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Create a sample book for testing
     */
    public static Book createSampleBook() {
        return Book.builder()
                .title("Sample Book")
                .author("Sample Author")
                .isbn("9780134685991")
                .publishedDate(LocalDate.of(2023, 1, 15))
                .price(new BigDecimal("29.99"))
                .description("A comprehensive guide to software development")
                .genre("Technology")
                .publisher("Tech Publications")
                .pageCount(350)
                .language("English")
                .build();
    }

    /**
     * Create a list of sample books for testing
     */
    public static List<Book> createSampleBooks() {
        Book book1 = createSampleBook();
        book1.setId(1L);
        
        Book book2 = Book.builder()
                .title("Advanced Programming Concepts")
                .author("Jane Developer")
                .isbn("9780135957059")
                .publishedDate(LocalDate.of(2022, 6, 10))
                .price(new BigDecimal("39.99"))
                .description("Deep dive into advanced programming paradigms")
                .genre("Technology")
                .publisher("Code Masters Press")
                .pageCount(480)
                .language("English")
                .build();
        book2.setId(2L);
        
        return Arrays.asList(book1, book2);
    }

    /**
     * Convert object to JSON string
     */
    public static String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Perform POST request with authentication
     */
    public static ResultActions performAuthenticatedPost(MockMvc mockMvc, String url, Object content) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(content))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")));
    }

    /**
     * Perform PUT request with authentication
     */
    public static ResultActions performAuthenticatedPut(MockMvc mockMvc, String url, Object content) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(content))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")));
    }

    /**
     * Perform DELETE request with authentication
     */
    public static ResultActions performAuthenticatedDelete(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")));
    }

    /**
     * Perform GET request with authentication
     */
    public static ResultActions performAuthenticatedGet(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")));
    }

    /**
     * Create a book with invalid data for negative testing
     */
    public static Book createInvalidBook() {
        return Book.builder()
                .title("") // Invalid: empty title
                .author("") // Invalid: empty author
                .isbn("123") // Invalid: too short ISBN
                .publishedDate(LocalDate.of(2030, 1, 1)) // Invalid: future date
                .price(new BigDecimal("-10.00")) // Invalid: negative price
                .description("A".repeat(2500)) // Invalid: too long description
                .genre("A".repeat(150)) // Invalid: too long genre
                .publisher("A".repeat(250)) // Invalid: too long publisher
                .pageCount(-1) // Invalid: negative page count
                .language("A".repeat(100)) // Invalid: too long language
                .build();
    }

    /**
     * Create a minimal valid book for testing
     */
    public static Book createMinimalValidBook() {
        return Book.builder()
                .title("A")
                .author("B")
                .isbn("9780134685991")
                .publishedDate(LocalDate.now())
                .price(new BigDecimal("0.01"))
                .build();
    }

    /**
     * Verify common book response fields
     */
    public static void verifyBookResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.id").exists())
              .andExpect(jsonPath("$.title").exists())
              .andExpect(jsonPath("$.author").exists())
              .andExpect(jsonPath("$.isbn").exists())
              .andExpect(jsonPath("$.publishedDate").exists())
              .andExpect(jsonPath("$.createdAt").exists())
              .andExpect(jsonPath("$.updatedAt").exists())
              .andExpect(jsonPath("$.isActive").value(true));
    }

    /**
     * Verify book list response
     */
    public static void verifyBookListResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.content").isArray())
              .andExpect(jsonPath("$.totalElements").exists())
              .andExpect(jsonPath("$.totalPages").exists())
              .andExpect(jsonPath("$.size").exists())
              .andExpect(jsonPath("$.number").exists());
    }
}