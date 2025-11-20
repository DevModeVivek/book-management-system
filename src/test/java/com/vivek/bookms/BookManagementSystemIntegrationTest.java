package com.vivek.bookms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import com.vivek.bookms.repository.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Book Management System Integration Tests")
class BookManagementSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        
        // Create DTO without ID for creation
        testBookDTO = BookDTO.builder()
                .title("Integration Test Book")
                .author("Test Author")
                .isbn("9781234567897")  // Valid ISBN-13 format (13 digits)
                .publishedDate(LocalDate.of(2023, 1, 1))
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should perform complete CRUD operations flow")
    @WithMockUser(roles = "ADMIN")
    void completeBookCrudFlow() throws Exception {
        // Create a book
        String createResponse = mockMvc.perform(post("/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Integration Test Book"))
                .andReturn().getResponse().getContentAsString();

        // Get all books
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Integration Test Book"));

        // Search books
        mockMvc.perform(get("/books/search")
                .param("query", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Integration Test Book"));
    }

    @Test
    @DisplayName("Should handle authentication and authorization correctly")
    void testSecurityConfiguration() throws Exception {
        // Unauthenticated request should be denied
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());

        // USER role SHOULD be able to access GET operations (based on @PreAuthorize("hasAnyRole('USER', 'ADMIN')"))
        mockMvc.perform(get("/books")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("user", "user123")))
                .andExpect(status().isOk());

        // USER role should NOT be able to access ADMIN-only operations (like POST)
        mockMvc.perform(post("/books")
                .with(csrf())
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("user", "user123"))
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());

        // ADMIN role should be able to access all operations
        mockMvc.perform(get("/books")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }
}