package com.vivek.bookms.service;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;
import com.vivek.bookms.exception.BookNotFoundException;
import com.vivek.bookms.mapper.IBookMapper;
import com.vivek.bookms.repository.IBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Book service implementation with improved error handling and DI
 */
@Service
@Transactional
public class BookService implements IBookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    private final IBookRepository bookRepository;
    private final IBookMapper bookMapper;
    
    @Autowired
    public BookService(IBookRepository bookRepository, IBookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }
    
    @Override
    public BookDTO create(BookDTO bookDTO) {
        logger.info("Creating new book: {}", bookDTO.getTitle());
        try {
            Book book = bookMapper.toEntity(bookDTO);
            Book savedBook = bookRepository.save(book);
            logger.info("Successfully created book with id: {}", savedBook.getId());
            return bookMapper.toDTO(savedBook);
        } catch (Exception e) {
            logger.error("Error creating book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<BookDTO> getById(Long id) {
        logger.info("Fetching book with id: {}", id);
        try {
            return bookRepository.findById(id)
                    .map(book -> {
                        logger.debug("Found book: {}", book.getTitle());
                        return bookMapper.toDTO(book);
                    });
        } catch (Exception e) {
            logger.error("Error fetching book with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<BookDTO> getAll() {
        logger.info("Fetching all books");
        try {
            List<Book> books = bookRepository.findAll();
            logger.debug("Found {} books", books.size());
            
            if (books.isEmpty()) {
                logger.info("No books found in the database");
                return List.of(); // Return empty list instead of null
            }
            
            return books.stream()
                    .map(bookMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all books: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch books: " + e.getMessage(), e);
        }
    }
    
    @Override
    public BookDTO update(Long id, BookDTO bookDTO) {
        logger.info("Updating book with id: {}", id);
        try {
            Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Book not found with id: {} for update", id);
                    return new BookNotFoundException("Book not found with id: " + id);
                });
            
            bookMapper.updateEntityFromDTO(bookDTO, existingBook);
            Book updatedBook = bookRepository.save(existingBook);
            logger.info("Successfully updated book with id: {}", updatedBook.getId());
            return bookMapper.toDTO(updatedBook);
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating book with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void delete(Long id) {
        logger.info("Deleting book with id: {}", id);
        try {
            if (!bookRepository.existsById(id)) {
                logger.error("Book not found with id: {} for deletion", id);
                throw new BookNotFoundException("Book not found with id: " + id);
            }
            bookRepository.deleteById(id);
            logger.info("Successfully deleted book with id: {}", id);
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting book with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean exists(Long id) {
        try {
            return bookRepository.existsById(id);
        } catch (Exception e) {
            logger.error("Error checking if book exists with id {}: {}", id, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public long count() {
        try {
            return bookRepository.count();
        } catch (Exception e) {
            logger.error("Error counting books: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public List<BookDTO> searchBooks(String query) {
        logger.info("Searching books with query: {}", query);
        try {
            if (query == null || query.trim().isEmpty()) {
                logger.warn("Empty search query provided, returning all books");
                return getAll();
            }
            
            List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
            logger.debug("Found {} books matching query: {}", books.size(), query);
            
            return books.stream()
                    .map(bookMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching books with query '{}': {}", query, e.getMessage(), e);
            throw new RuntimeException("Failed to search books: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<BookDTO> findByTitle(String title) {
        logger.info("Searching books by title: {}", title);
        try {
            List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
            return books.stream()
                    .map(bookMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding books by title '{}': {}", title, e.getMessage(), e);
            throw new RuntimeException("Failed to find books by title: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<BookDTO> findByAuthor(String author) {
        logger.info("Searching books by author: {}", author);
        try {
            List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);
            return books.stream()
                    .map(bookMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding books by author '{}': {}", author, e.getMessage(), e);
            throw new RuntimeException("Failed to find books by author: " + e.getMessage(), e);
        }
    }
    
    @Override
    public BookDTO findByIsbn(String isbn) {
        logger.info("Searching book by ISBN: {}", isbn);
        try {
            return bookRepository.findByIsbn(isbn)
                    .map(bookMapper::toDTO)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error finding book by ISBN '{}': {}", isbn, e.getMessage(), e);
            throw new RuntimeException("Failed to find book by ISBN: " + e.getMessage(), e);
        }
    }
}