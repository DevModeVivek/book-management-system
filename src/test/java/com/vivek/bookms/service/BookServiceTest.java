package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import com.vivek.bookms.exception.BookNotFoundException;
import com.vivek.bookms.mapper.IBookMapper;
import com.vivek.bookms.repository.IBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IBookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDTO testBookDTO;
    private IBookService bookServiceInterface;

    @BeforeEach
    void setUp() {
        // Initialize test book entity
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setPublishedDate(LocalDate.of(2023, 1, 1));
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());

        // Initialize test book DTO
        testBookDTO = new BookDTO();
        testBookDTO.setId(1L);
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setIsbn("1234567890");
        testBookDTO.setPublishedDate(LocalDate.of(2023, 1, 1));
        
        // Use interface reference
        bookServiceInterface = bookService;
    }

    @Test
    @DisplayName("Should return all books when getAll is called")
    void getAll_ShouldReturnAllBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        List<BookDTO> result = bookServiceInterface.getAll();

        // Then
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findAll();
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should return empty list when no books exist")
    void getAll_WhenNoBooksExist_ShouldReturnEmptyList() {
        // Given
        when(bookRepository.findAll()).thenReturn(List.of());

        // When
        List<BookDTO> result = bookServiceInterface.getAll();

        // Then
        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
        verify(bookMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should return book when valid id is provided")
    void getById_WithValidId_ShouldReturnBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        Optional<BookDTO> result = bookServiceInterface.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBookDTO.getTitle(), result.get().getTitle());
        verify(bookRepository).findById(1L);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should return empty optional when book not found")
    void getById_WithInvalidId_ShouldReturnEmptyOptional() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<BookDTO> result = bookServiceInterface.getById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository).findById(999L);
        verify(bookMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should create and return book when valid BookDTO is provided")
    void create_WithValidBookDTO_ShouldReturnCreatedBook() {
        // Given
        when(bookMapper.toEntity(testBookDTO)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        BookDTO result = bookServiceInterface.create(testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookMapper).toEntity(testBookDTO);
        verify(bookRepository).save(testBook);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should update and return book when valid id and BookDTO are provided")
    void update_WithValidIdAndBookDTO_ShouldReturnUpdatedBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        BookDTO result = bookServiceInterface.update(1L, testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookMapper).updateEntityFromDTO(testBookDTO, testBook);
        verify(bookRepository).save(testBook);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when updating non-existent book")
    void update_WithInvalidId_ShouldThrowException() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookServiceInterface.update(999L, testBookDTO));
        verify(bookRepository).findById(999L);
        verify(bookMapper, never()).updateEntityFromDTO(any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete book when valid id is provided")
    void delete_WithValidId_ShouldDeleteBook() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> bookServiceInterface.delete(1L));

        // Then
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when deleting non-existent book")
    void delete_WithInvalidId_ShouldThrowException() {
        // Given
        when(bookRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookServiceInterface.delete(999L));
        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should return search results when valid query is provided")
    void searchBooks_WithValidQuery_ShouldReturnSearchResults() {
        // Given
        String query = "Test";
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query))
                .thenReturn(books);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        List<BookDTO> result = bookServiceInterface.searchBooks(query);

        // Then
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should return all books when empty query is provided")
    void searchBooks_WithEmptyQuery_ShouldReturnAllBooks() {
        // Given
        String query = "";
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        List<BookDTO> result = bookServiceInterface.searchBooks(query);

        // Then
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findAll();
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should return true when book exists")
    void exists_WithValidId_ShouldReturnTrue() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = bookServiceInterface.exists(1L);

        // Then
        assertTrue(result);
        verify(bookRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should return false when book does not exist")
    void exists_WithInvalidId_ShouldReturnFalse() {
        // Given
        when(bookRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = bookServiceInterface.exists(999L);

        // Then
        assertFalse(result);
        verify(bookRepository).existsById(999L);
    }

    @Test
    @DisplayName("Should return correct count of books")
    void count_ShouldReturnCorrectCount() {
        // Given
        when(bookRepository.count()).thenReturn(5L);

        // When
        long result = bookServiceInterface.count();

        // Then
        assertEquals(5L, result);
        verify(bookRepository).count();
    }
}