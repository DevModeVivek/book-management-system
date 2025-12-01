package com.vivek.bookms.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.bookms.constants.ErrorCodes;
import com.vivek.bookms.dto.BookDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test for the enhanced GlobalExceptionHandler
 * Tests all exception scenarios and validates proper error responses
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should handle validation errors for invalid request body")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleValidationErrors() throws Exception {
        // Create invalid BookDTO with missing required fields
        BookDTO invalidBook = new BookDTO();
        // Title is required but not set
        
        MvcResult result = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Validation.GENERAL_ERROR))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(header().exists("X-Error-Code"))
                .andExpect(header().exists("X-Trace-Id"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("validationErrors");
        assertThat(content).contains("title");
    }

    @Test
    @DisplayName("Should handle missing required path parameters")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMissingPathParameters() throws Exception {
        mockMvc.perform(get("/books/invalidId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Validation.INVALID_FORMAT))
                .andExpect(jsonPath("$.message").value("Invalid parameter type"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(header().exists("X-Error-Code"));
    }

    @Test
    @DisplayName("Should handle authentication errors")
    void shouldHandleAuthenticationErrors() throws Exception {
        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Security.AUTHENTICATION_REQUIRED))
                .andExpect(jsonPath("$.message").value("Authentication required"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(header().exists("WWW-Authenticate"));
    }

    @Test
    @DisplayName("Should handle access denied errors")
    @WithMockUser(roles = "USER") // USER role trying to access ADMIN endpoint
    void shouldHandleAccessDeniedErrors() throws Exception {
        BookDTO validBook = new BookDTO();
        validBook.setTitle("Test Book");
        validBook.setAuthor("Test Author");
        validBook.setIsbn("978-0-123456-78-9");

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Security.ACCESS_DENIED))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle method not allowed errors")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleMethodNotAllowed() throws Exception {
        mockMvc.perform(patch("/books") // PATCH not supported on /books
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value(ErrorCodes.System.METHOD_NOT_ALLOWED))
                .andExpect(jsonPath("$.message").value("HTTP method not supported"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(header().exists("Allow"));
    }

    @Test
    @DisplayName("Should handle invalid JSON format errors")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleInvalidJsonFormat() throws Exception {
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Validation.INVALID_FORMAT))
                .andExpect(jsonPath("$.message").value("Invalid request format"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle missing request parameters")
    @WithMockUser(roles = "USER")
    void shouldHandleMissingRequestParameters() throws Exception {
        // Search endpoint requires 'query' parameter
        mockMvc.perform(get("/books/search")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Validation.REQUIRED_FIELD))
                .andExpect(jsonPath("$.message").value("Required parameter missing"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle endpoint not found errors")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleEndpointNotFound() throws Exception {
        mockMvc.perform(get("/nonexistent-endpoint")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCodes.System.ENDPOINT_NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Endpoint not found"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle domain exceptions (BookNotFoundException)")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleDomainExceptions() throws Exception {
        // Try to get a book that doesn't exist
        mockMvc.perform(get("/books/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCodes.Book.NOT_FOUND))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(header().exists("X-Error-Code"))
                .andExpect(header().exists("X-Error-Category"));
    }

    @Test
    @DisplayName("Should include proper error headers in all responses")
    @WithMockUser(roles = "ADMIN")
    void shouldIncludeProperErrorHeaders() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/invalidId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error-Code"))
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(header().exists("X-Error-Category"))
                .andReturn();

        String errorCode = result.getResponse().getHeader("X-Error-Code");
        String traceId = result.getResponse().getHeader("X-Trace-Id");
        String errorCategory = result.getResponse().getHeader("X-Error-Category");

        assertThat(errorCode).isNotEmpty();
        assertThat(traceId).isNotEmpty();
        assertThat(errorCategory).isNotEmpty();
    }

    @Test
    @DisplayName("Should provide consistent error response structure")
    @WithMockUser(roles = "ADMIN")
    void shouldProvideConsistentErrorStructure() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        
        // Verify all required fields are present
        assertThat(content).contains("timestamp");
        assertThat(content).contains("status");
        assertThat(content).contains("error");
        assertThat(content).contains("message");
        assertThat(content).contains("path");
        assertThat(content).contains("traceId");
        assertThat(content).contains("success");
        assertThat(content).contains("\"success\":false");
    }
}