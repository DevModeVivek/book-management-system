package com.vivek.bookservice.service;

import com.vivek.bookservice.dto.BookDTO;
import com.vivek.bookservice.entity.Book;
import com.vivek.bookservice.repository.IBookRepository;
import com.vivek.bookservice.mapper.IBookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IBookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDTO testBookDTO;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("9780134685991") // Valid ISBN-13 with correct checksum
                .price(BigDecimal.valueOf(29.99))
                .build();

        testBookDTO = BookDTO.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("9780134685991") // Valid ISBN-13 with correct checksum
                .price(BigDecimal.valueOf(29.99))
                .build();
    }

    @Test
    void getById_WhenBookExists_ReturnsBookDTO() {
        // Given
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        Optional<BookDTO> result = bookService.getById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Book");
        verify(bookRepository).findByIdAndIsActiveTrue(1L);
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    void getById_WhenBookNotExists_ReturnsEmpty() {
        // Given
        when(bookRepository.findByIdAndIsActiveTrue(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<BookDTO> result = bookService.getById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(bookRepository).findByIdAndIsActiveTrue(999L);
        verifyNoInteractions(bookMapper);
    }

    @Test
    void create_WhenValidBook_ReturnsCreatedBookDTO() {
        // Given - Create DTO without ID for creation
        BookDTO createDTO = BookDTO.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9780134685991")
                .price(BigDecimal.valueOf(29.99))
                .publishedDate(java.time.LocalDate.now()) // Add required publishedDate
                .build();
        
        when(bookMapper.toEntity(createDTO)).thenReturn(testBook);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);
        when(bookRepository.existsByIsbnAndIsActiveTrue(anyString())).thenReturn(false);

        // When
        BookDTO result = bookService.create(createDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void findByIsbn_WhenBookExists_ReturnsBookDTO() {
        // Given - Use the same valid ISBN from setUp
        when(bookRepository.findByIsbnAndIsActiveTrue("9780134685991")).thenReturn(Optional.of(testBook));
        when(bookMapper.toDTO(testBook)).thenReturn(testBookDTO);

        // When
        Optional<BookDTO> result = bookService.findByIsbn("9780134685991");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getIsbn()).isEqualTo("9780134685991");
        verify(bookRepository).findByIsbnAndIsActiveTrue("9780134685991");
        verify(bookMapper).toDTO(testBook);
    }

    @Test
    void searchBooks_WithValidQuery_ReturnsMatchingBooks() {
        // Given
        List<Book> books = List.of(testBook);
        List<BookDTO> bookDTOs = List.of(testBookDTO);
        when(bookRepository.searchByTitleOrAuthorAndIsActiveTrue("test")).thenReturn(books);
        when(bookMapper.toDTOList(books)).thenReturn(bookDTOs);

        // When
        List<BookDTO> result = bookService.searchBooks("test");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Book");
        verify(bookRepository).searchByTitleOrAuthorAndIsActiveTrue("test");
        verify(bookMapper).toDTOList(books);
    }
}