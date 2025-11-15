package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import com.vivek.bookms.exception.BookNotFoundException;
import com.vivek.bookms.mapper.BookMapper;
import com.vivek.bookms.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    
    @Autowired
    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }
    
    public List<BookDTO> getAllBooks() {
        logger.info("Fetching all books");
        List<Book> books = bookRepository.findAll();
        logger.debug("Found {} books", books.size());
        return books.stream()
                   .map(bookMapper::toDTO)
                   .collect(Collectors.toList());
    }
    
    public BookDTO getBookById(Long id) {
        logger.info("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Book not found with id: {}", id);
                return new BookNotFoundException("Book not found with id: " + id);
            });
        logger.debug("Found book: {}", book.getTitle());
        return bookMapper.toDTO(book);
    }
    
    public BookDTO createBook(BookDTO bookDTO) {
        logger.info("Creating new book: {}", bookDTO.getTitle());
        Book book = bookMapper.toEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        logger.info("Successfully created book with id: {}", savedBook.getId());
        return bookMapper.toDTO(savedBook);
    }
    
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        logger.info("Updating book with id: {}", id);
        Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Book not found with id: {} for update", id);
                return new BookNotFoundException("Book not found with id: " + id);
            });
        
        bookMapper.updateEntityFromDTO(bookDTO, existingBook);
        Book updatedBook = bookRepository.save(existingBook);
        logger.info("Successfully updated book with id: {}", updatedBook.getId());
        return bookMapper.toDTO(updatedBook);
    }
    
    public void deleteBook(Long id) {
        logger.info("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            logger.error("Book not found with id: {} for deletion", id);
            throw new BookNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        logger.info("Successfully deleted book with id: {}", id);
    }
    
    public List<BookDTO> searchBooks(String query) {
        logger.info("Searching books with query: {}", query);
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
        logger.debug("Found {} books matching query: {}", books.size(), query);
        return books.stream()
                   .map(bookMapper::toDTO)
                   .collect(Collectors.toList());
    }
}