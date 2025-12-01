package com.vivek.bookservice.service;

import com.vivek.bookservice.dto.BookDTO;
import com.vivek.bookservice.entity.Book;
import com.vivek.bookservice.repository.IBookRepository;
import com.vivek.bookservice.mapper.IBookMapper;
import com.vivek.commons.service.base.BaseService;
import com.vivek.commons.constants.AppConstants;
import com.vivek.commons.constants.ErrorCodes;
import com.vivek.commons.exception.ValidationException;
import com.vivek.commons.common.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced Book service extending BaseService with centralized constants and comprehensive validation
 * Implements proper inheritance pattern with business-specific logic
 */
@Service
@Slf4j
public class BookService extends BaseService<Book, BookDTO, Long> implements IBookService {
    
    private final IBookRepository bookRepository;
    private final IBookMapper bookMapper;
    
    public BookService(IBookRepository bookRepository, IBookMapper bookMapper) {
        super(bookRepository, bookMapper);
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }
    
    // ============= ENHANCED SEARCH OPERATIONS =============
    
    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooks(String query) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".searchBooks",
                String.format("Searching books with query: %s", query));
        
        try {
            if (ValidationUtils.isBlank(query)) {
                log.warn("Empty search query provided, returning all active books");
                return getAll();
            }
            
            String sanitizedQuery = query.trim();
            ValidationUtils.validateSearchQuery(sanitizedQuery);
            
            List<Book> books = bookRepository.searchByTitleOrAuthorAndIsActiveTrue(sanitizedQuery);
            List<BookDTO> result = bookMapper.toDTOList(books);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".searchBooks",
                    String.format("Found %d books matching query: %s", result.size(), sanitizedQuery));
            
            return result;
            
        } catch (ValidationException e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".searchBooks",
                    "VALIDATION_ERROR", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".searchBooks",
                    "SEARCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_SEARCH_FAILED, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(String query, @NotNull Pageable pageable) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".searchBooks(pageable)",
                String.format("Searching books with pagination - Query: %s, Page: %d", query, pageable.getPageNumber()));
        
        try {
            ValidationUtils.validatePaginationParams(pageable.getPageNumber(), pageable.getPageSize());
            
            if (ValidationUtils.isBlank(query)) {
                log.warn("Empty search query provided, returning all active books with pagination");
                return getAll(pageable);
            }
            
            String sanitizedQuery = query.trim();
            ValidationUtils.validateSearchQuery(sanitizedQuery);
            
            Page<Book> books = bookRepository.searchByTitleOrAuthorAndIsActiveTruePaged(sanitizedQuery, pageable);
            Page<BookDTO> result = books.map(bookMapper::toDTO);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".searchBooks(pageable)",
                    String.format("Found %d books on page %d of %d for query: %s",
                            result.getNumberOfElements(), result.getNumber() + 1,
                            result.getTotalPages(), sanitizedQuery));
            
            return result;
            
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".searchBooks(pageable)",
                    "SEARCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_SEARCH_FAILED, e);
        }
    }
    
    // ============= SPECIFIC FINDER METHODS =============
    
    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> findByTitle(String title) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".findByTitle",
                String.format("Finding books by title: %s", title));
        
        try {
            ValidationUtils.requireNonBlank(title, "title");
            
            List<Book> books = bookRepository.findByTitleIgnoreCaseAndIsActiveTrue(title.trim());
            List<BookDTO> result = bookMapper.toDTOList(books);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByTitle",
                    String.format("Found %d books with title: %s", result.size(), title));
            
            return result;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByTitle",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_FETCH_FAILED, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> findByAuthor(String author) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".findByAuthor",
                String.format("Finding books by author: %s", author));
        
        try {
            ValidationUtils.requireNonBlank(author, "author");
            
            List<Book> books = bookRepository.findByAuthorIgnoreCaseAndIsActiveTrue(author.trim());
            List<BookDTO> result = bookMapper.toDTOList(books);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByAuthor",
                    String.format("Found %d books by author: %s", result.size(), author));
            
            return result;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByAuthor",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_FETCH_FAILED, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BookDTO> findByIsbn(String isbn) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".findByIsbn",
                String.format("Finding book by ISBN: %s", isbn));
        
        try {
            ValidationUtils.validateIsbn(isbn);
            
            Optional<Book> book = bookRepository.findByIsbnAndIsActiveTrue(isbn.trim());
            Optional<BookDTO> result = book.map(bookMapper::toDTO);
            
            if (result.isPresent()) {
                log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_SERVICE,
                        getServiceName() + ".findByIsbn",
                        String.format("Found book with ISBN: %s", isbn));
            } else {
                log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                        AppConstants.Logging.CATEGORY_SERVICE,
                        getServiceName() + ".findByIsbn",
                        String.format("No book found with ISBN: %s", isbn));
            }
            
            return result;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByIsbn",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_FETCH_FAILED, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> findByGenre(String genre) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".findByGenre",
                String.format("Finding books by genre: %s", genre));
        
        try {
            ValidationUtils.requireNonBlank(genre, "genre");
            
            List<Book> books = bookRepository.findByGenreIgnoreCaseAndIsActiveTrue(genre.trim());
            List<BookDTO> result = bookMapper.toDTOList(books);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByGenre",
                    String.format("Found %d books in genre: %s", result.size(), genre));
            
            return result;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByGenre",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_FETCH_FAILED, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> findByPublisher(String publisher) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".findByPublisher",
                String.format("Finding books by publisher: %s", publisher));
        
        try {
            ValidationUtils.requireNonBlank(publisher, "publisher");
            
            List<Book> books = bookRepository.findByPublisherIgnoreCaseAndIsActiveTrue(publisher.trim());
            List<BookDTO> result = bookMapper.toDTOList(books);
            
            log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByPublisher",
                    String.format("Found %d books by publisher: %s", result.size(), publisher));
            
            return result;
            
        } catch (Exception e) {
            log.error(AppConstants.Logging.ERROR_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".findByPublisher",
                    "FETCH_ERROR", e.getMessage());
            throw new RuntimeException(AppConstants.ErrorMessages.BOOK_FETCH_FAILED, e);
        }
    }
    
    // ============= BUSINESS VALIDATION METHODS =============
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".existsByIsbn",
                String.format("Checking if book exists with ISBN: %s", isbn));
        
        try {
            ValidationUtils.validateIsbn(isbn);
            return bookRepository.existsByIsbnAndIsActiveTrue(isbn.trim());
        } catch (Exception e) {
            log.warn(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".existsByIsbn",
                    String.format("Error checking existence by ISBN '%s': %s", isbn, e.getMessage()));
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbnAndIdNot(String isbn, Long excludeId) {
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_SERVICE,
                getServiceName() + ".existsByIsbnAndIdNot",
                String.format("Checking if book exists with ISBN: %s excluding ID: %s", isbn, excludeId));
        
        try {
            ValidationUtils.validateIsbn(isbn);
            ValidationUtils.requireNonNull(excludeId, "excludeId");
            return bookRepository.existsByIsbnAndIdNotAndIsActiveTrue(isbn.trim(), excludeId);
        } catch (Exception e) {
            log.warn(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".existsByIsbnAndIdNot",
                    String.format("Error checking existence by ISBN '%s' excluding ID %s: %s", isbn, excludeId, e.getMessage()));
            return false;
        }
    }
    
    // ============= OVERRIDE BASE SERVICE VALIDATION HOOKS =============
    
    @Override
    protected void validateForCreate(BookDTO dto) {
        super.validateForCreate(dto);
        
        // Business-specific validation for creation
        if (ValidationUtils.isNotBlank(dto.getIsbn()) && existsByIsbn(dto.getIsbn())) {
            throw ValidationException.builder()
                    .withErrorCode(ErrorCodes.Book.DUPLICATE_ISBN)
                    .withUserMessage(String.format(AppConstants.ErrorMessages.DUPLICATE_ISBN, dto.getIsbn()))
                    .addFieldError("isbn", "ISBN already exists")
                    .withContext("isbn", dto.getIsbn())
                    .build();
        }
        
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                getServiceName() + ".validateForCreate",
                "Book creation validation completed");
    }
    
    @Override
    protected void validateForUpdate(Long id, BookDTO dto) {
        super.validateForUpdate(id, dto);
        
        // Business-specific validation for update
        if (ValidationUtils.isNotBlank(dto.getIsbn()) && existsByIsbnAndIdNot(dto.getIsbn(), id)) {
            throw ValidationException.builder()
                    .withErrorCode(ErrorCodes.Book.DUPLICATE_ISBN)
                    .withUserMessage(String.format(AppConstants.ErrorMessages.DUPLICATE_ISBN, dto.getIsbn()))
                    .addFieldError("isbn", "ISBN already exists")
                    .withContext("isbn", dto.getIsbn())
                    .withContext("excludeId", id)
                    .build();
        }
        
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                getServiceName() + ".validateForUpdate",
                "Book update validation completed");
    }
    
    @Override
    protected void performPreUpdateValidation(Book existingEntity, BookDTO dto) {
        // Additional business logic validation before update
        if (existingEntity.getIsbn() != null && 
            !existingEntity.getIsbn().equals(dto.getIsbn()) && 
            ValidationUtils.isNotBlank(dto.getIsbn())) {
            
            log.info(AppConstants.Logging.SERVICE_LOG_PATTERN,
                    AppConstants.Logging.CATEGORY_SERVICE,
                    getServiceName() + ".performPreUpdateValidation",
                    String.format("ISBN change detected: %s -> %s", existingEntity.getIsbn(), dto.getIsbn()));
        }
        
        log.debug(AppConstants.Logging.SERVICE_LOG_PATTERN,
                AppConstants.Logging.CATEGORY_VALIDATION,
                getServiceName() + ".performPreUpdateValidation",
                "Pre-update business validation completed");
    }
    
    @Override
    protected String getServiceName() {
        return "BookService";
    }
    
    // ============= LEGACY CRUD METHODS FOR INTERFACE COMPATIBILITY =============
    
    @Override
    @Transactional
    public BookDTO create(@Valid @NotNull BookDTO bookDTO) {
        return super.create(bookDTO);
    }
    
    @Override
    @Transactional
    public BookDTO update(@NotNull Long id, @Valid @NotNull BookDTO bookDTO) {
        return super.update(id, bookDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BookDTO> getById(@NotNull Long id) {
        return super.getById(id);
    }
    
    @Override
    @Transactional
    public void softDelete(@NotNull Long id) {
        super.softDelete(id);
    }
    
    @Override
    @Transactional
    public void restore(@NotNull Long id) {
        super.restore(id);
    }
}