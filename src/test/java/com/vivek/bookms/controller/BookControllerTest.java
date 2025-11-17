package com.vivek.bookms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.service.IBookService;
import com.vivek.bookms.service.IGoogleBooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("BookController Tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookService bookService;

    @MockBean
    private IGoogleBooksService googleBooksService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBookDTO = new BookDTO();
        testBookDTO.setId(1L);
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setIsbn("9781234567890");
        testBookDTO.setPublishedDate(LocalDate.of(2023, 1, 1));
    }

    @Test
    @DisplayName("Should return all books when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAllBooks_WithAdminRole_ShouldReturnBooks() throws Exception {
        // Given
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(bookService.getAll()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"));
    }

    @Test
    @DisplayName("Should return book by ID when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getBookById_WithAdminRole_ShouldReturnBook() throws Exception {
        // Given
        when(bookService.getById(1L)).thenReturn(Optional.of(testBookDTO));

        // When & Then
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @DisplayName("Should create book when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void createBook_WithAdminRole_ShouldCreateBook() throws Exception {
        // Given
        when(bookService.create(any(BookDTO.class))).thenReturn(testBookDTO);

        // When & Then
        mockMvc.perform(post("/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @DisplayName("Should update book when authenticated as ADMIN")
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithAdminRole_ShouldUpdateBook() throws Exception {
        // Given
        when(bookService.update(eq(1L), any(BookDTO.class))).thenReturn(testBookDTO);

        // When & Then
        mockMvc.perform(put("/books/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Book"));
    }

    @Test
    @DisplayName("Should return forbidden when USER tries to access ADMIN endpoints")
    @WithMockUser(roles = "USER")
    void getAllBooks_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/books"))
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
        mockMvc.perform(get("/books/external/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"));
    }
}