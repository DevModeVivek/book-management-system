package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import com.vivek.bookms.exception.BookNotFoundException;
import com.vivek.bookms.mapper.BookMapper;
import com.vivek.bookms.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService; // Test the concrete implementation

    private Book testBook;
    private BookDTO testBookDTO;
    private IBookService bookServiceInterface; // Reference to interface

    @BeforeEach
    void setUp() {
        // Initialize test data
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setPublishedDate(LocalDate.of(2023, 1, 1));

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
    @DisplayName("Should return all books when getAllBooks is called")
    void getAllBooks_ShouldReturnAllBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        List<BookDTO> result = bookServiceInterface.getAllBooks();

        // Then
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository).findAll();
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should return book when valid id is provided")
    void getBookById_WithValidId_ShouldReturnBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        BookDTO result = bookServiceInterface.getBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when book id does not exist")
    void getBookById_WithInvalidId_ShouldThrowException() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookServiceInterface.getBookById(999L));
        verify(bookRepository).findById(999L);
        verify(bookMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should create and return book when valid BookDTO is provided")
    void createBook_WithValidBookDTO_ShouldReturnCreatedBook() {
        // Given
        when(bookMapper.toEntity(testBookDTO)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        BookDTO result = bookServiceInterface.createBook(testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookMapper).toEntity(testBookDTO);
        verify(bookRepository).save(testBook);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should update and return book when valid id and BookDTO are provided")
    void updateBook_WithValidIdAndBookDTO_ShouldReturnUpdatedBook() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        BookDTO result = bookServiceInterface.updateBook(1L, testBookDTO);

        // Then
        assertNotNull(result);
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository).findById(1L);
        verify(bookMapper).updateEntityFromDTO(testBookDTO, testBook);
        verify(bookRepository).save(testBook);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    @DisplayName("Should delete book when valid id is provided")
    void deleteBook_WithValidId_ShouldDeleteBook() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> bookServiceInterface.deleteBook(1L));

        // Then
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
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
}