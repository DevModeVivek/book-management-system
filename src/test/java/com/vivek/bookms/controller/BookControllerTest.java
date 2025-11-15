package com.vivek.bookms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.service.BookService;
import com.vivek.bookms.service.GoogleBooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("BookController Tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private GoogleBooksService googleBooksService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBookDTO = new BookDTO();
        testBookDTO.setId(1L);
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setIsbn("1234567890");
        testBookDTO.setPublishedDate(LocalDate.of(2023, 1, 1));
    }

    @Test
    @DisplayName("Should return all books when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAllBooks_WithAdminRole_ShouldReturnBooks() throws Exception {
        // Given
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(bookService.getAllBooks()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"));
    }

    @Test
    @DisplayName("Should return book by ID when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getBookById_WithAdminRole_ShouldReturnBook() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenReturn(testBookDTO);

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @DisplayName("Should create book when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void createBook_WithAdminRole_ShouldCreateBook() throws Exception {
        // Given
        when(bookService.createBook(any(BookDTO.class))).thenReturn(testBookDTO);

        // When & Then
        mockMvc.perform(post("/api/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @DisplayName("Should update book when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        when(bookService.updateBook(eq(1L), any(BookDTO.class))).thenReturn(testBookDTO);

        // When & Then
        mockMvc.perform(put("/api/books/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @DisplayName("Should return forbidden when USER tries to access ADMIN endpoints")
    @WithMockUser(roles = "USER")
    void getAllBooks_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow USER to search external books")
    @WithMockUser(roles = "USER")
    void searchExternalBooks_WithUserRole_ShouldReturnBooks() throws Exception {
        // Given
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(googleBooksService.searchBooks("test")).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/external/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"));
    }
}