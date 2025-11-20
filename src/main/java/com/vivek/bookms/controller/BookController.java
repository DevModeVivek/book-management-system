package com.vivek.bookms.controller;

import com.vivek.bookms.core.Messages;
import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.service.IBookService;
import com.vivek.bookms.service.IGoogleBooksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Book controller extending BaseController with comprehensive book management operations
 * Uses dependency injection and improved error handling with validation
 */
@RestController
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Operations for managing books")
@RequiredArgsConstructor
public class BookController extends BaseController<BookDTO, Long> {

    private final IBookService bookService;
    private final IGoogleBooksService googleBooksService;

    @Operation(summary = "Get all books", description = "Retrieve all active books from the database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllBooks() {
        logRequest("getAllBooks");
        try {
            List<BookDTO> books = bookService.getAll();
            return handleListResponse(books, Messages.BOOKS_RETRIEVED_SUCCESS, Messages.BOOKS_NOT_FOUND);
        } catch (Exception e) {
            logError("getAllBooks", e);
            throw e;
        }
    }

    @Operation(summary = "Get all books with pagination", description = "Retrieve all active books with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllBooksWithPagination(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        logRequest("getAllBooksWithPagination", pageable);
        try {
            Page<BookDTO> books = bookService.getAll(pageable);
            return handlePageResponse(books, Messages.BOOKS_RETRIEVED_SUCCESS, Messages.BOOKS_NOT_FOUND);
        } catch (Exception e) {
            logError("getAllBooksWithPagination", e);
            throw e;
        }
    }

    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logRequest("getBookById", id);
        validateId(id);
        
        try {
            Optional<BookDTO> book = bookService.getById(id);
            return handleOptionalResponse(book, Messages.BOOK_RETRIEVED_SUCCESS, 
                    String.format(Messages.BOOK_NOT_FOUND, id));
        } catch (Exception e) {
            logError("getBookById", e);
            throw e;
        }
    }

    @Operation(summary = "Create a new book", description = "Add a new book to the database")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createBook(
            @Valid @RequestBody BookDTO bookDTO, 
            BindingResult bindingResult) {
        logRequest("createBook", bookDTO != null ? bookDTO.getTitle() : "null");
        
        // Handle validation errors
        ResponseEntity<Map<String, Object>> validationResponse = handleValidationErrors(bindingResult);
        if (validationResponse != null) {
            return validationResponse;
        }
        
        try {
            BookDTO createdBook = bookService.create(bookDTO);
            logSuccess("createBook", "Created book with ID: " + createdBook.getId());
            return handleCreationResponse(createdBook, Messages.BOOK_CREATED_SUCCESS);
        } catch (Exception e) {
            logError("createBook", e);
            throw e;
        }
    }

    @Operation(summary = "Update an existing book", description = "Update book details by ID")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO,
            BindingResult bindingResult) {
        logRequest("updateBook", id, bookDTO != null ? bookDTO.getTitle() : "null");
        validateId(id);
        
        // Handle validation errors
        ResponseEntity<Map<String, Object>> validationResponse = handleValidationErrors(bindingResult);
        if (validationResponse != null) {
            return validationResponse;
        }
        
        try {
            BookDTO updatedBook = bookService.update(id, bookDTO);
            logSuccess("updateBook", "Updated book with ID: " + id);
            return handleUpdateResponse(updatedBook, Messages.BOOK_UPDATED_SUCCESS);
        } catch (Exception e) {
            logError("updateBook", e);
            throw e;
        }
    }

    @Operation(summary = "Delete a book", description = "Soft delete a book from the database")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logRequest("deleteBook", id);
        validateId(id);
        
        try {
            bookService.softDelete(id);
            logSuccess("deleteBook", "Soft deleted book with ID: " + id);
            return handleDeletionResponse(Messages.BOOK_DELETED_SUCCESS);
        } catch (Exception e) {
            logError("deleteBook", e);
            throw e;
        }
    }

    @Operation(summary = "Hard delete a book", description = "Permanently delete a book from the database")
    @ApiResponse(responseCode = "204", description = "Book permanently deleted")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> hardDeleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logRequest("hardDeleteBook", id);
        validateId(id);
        
        try {
            bookService.delete(id);
            logSuccess("hardDeleteBook", "Hard deleted book with ID: " + id);
            return handleDeletionResponse("Book permanently deleted successfully");
        } catch (Exception e) {
            logError("hardDeleteBook", e);
            throw e;
        }
    }

    @Operation(summary = "Restore a soft-deleted book", description = "Restore a previously soft-deleted book")
    @ApiResponse(responseCode = "200", description = "Book restored successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> restoreBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logRequest("restoreBook", id);
        validateId(id);
        
        try {
            bookService.restore(id);
            logSuccess("restoreBook", "Restored book with ID: " + id);
            return handleSuccessResponse(null, "Book restored successfully");
        } catch (Exception e) {
            logError("restoreBook", e);
            throw e;
        }
    }

    @Operation(summary = "Search books", description = "Search books by title or author")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @Parameter(description = "Search query") @RequestParam String query) {
        logRequest("searchBooks", query);
        validateRequired(query, "Search query");
        
        try {
            List<BookDTO> books = bookService.searchBooks(query.trim());
            return handleListResponse(books, Messages.SEARCH_COMPLETED_SUCCESS, 
                    String.format(Messages.NO_BOOKS_FOUND_FOR_QUERY, query));
        } catch (Exception e) {
            logError("searchBooks", e);
            throw e;
        }
    }

    @Operation(summary = "Search books with pagination", description = "Search books by title or author with pagination")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search/page")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> searchBooksWithPagination(
            @Parameter(description = "Search query") @RequestParam String query,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        logRequest("searchBooksWithPagination", query, pageable);
        validateRequired(query, "Search query");
        
        try {
            Page<BookDTO> books = bookService.searchBooks(query.trim(), pageable);
            return handlePageResponse(books, Messages.SEARCH_COMPLETED_SUCCESS, 
                    String.format(Messages.NO_BOOKS_FOUND_FOR_QUERY, query));
        } catch (Exception e) {
            logError("searchBooksWithPagination", e);
            throw e;
        }
    }

    @Operation(summary = "Find books by title", description = "Find books by title (case insensitive)")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/title/{title}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> findByTitle(
            @Parameter(description = "Book title") @PathVariable String title) {
        logRequest("findByTitle", title);
        validateRequired(title, "Title");
        
        try {
            List<BookDTO> books = bookService.findByTitle(title);
            return handleListResponse(books, "Books found by title", 
                    "No books found with title: " + title);
        } catch (Exception e) {
            logError("findByTitle", e);
            throw e;
        }
    }

    @Operation(summary = "Find books by author", description = "Find books by author (case insensitive)")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/author/{author}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> findByAuthor(
            @Parameter(description = "Author name") @PathVariable String author) {
        logRequest("findByAuthor", author);
        validateRequired(author, "Author");
        
        try {
            List<BookDTO> books = bookService.findByAuthor(author);
            return handleListResponse(books, "Books found by author", 
                    "No books found by author: " + author);
        } catch (Exception e) {
            logError("findByAuthor", e);
            throw e;
        }
    }

    @Operation(summary = "Find book by ISBN", description = "Find a specific book by its ISBN")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/isbn/{isbn}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> findByIsbn(
            @Parameter(description = "Book ISBN") @PathVariable String isbn) {
        logRequest("findByIsbn", isbn);
        validateRequired(isbn, "ISBN");
        
        try {
            Optional<BookDTO> book = bookService.findByIsbn(isbn);
            return handleOptionalResponse(book, "Book found by ISBN", 
                    "No book found with ISBN: " + isbn);
        } catch (Exception e) {
            logError("findByIsbn", e);
            throw e;
        }
    }

    @Operation(summary = "Find books by genre", description = "Find books by genre (case insensitive)")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/genre/{genre}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> findByGenre(
            @Parameter(description = "Book genre") @PathVariable String genre) {
        logRequest("findByGenre", genre);
        validateRequired(genre, "Genre");
        
        try {
            List<BookDTO> books = bookService.findByGenre(genre);
            return handleListResponse(books, "Books found by genre", 
                    "No books found with genre: " + genre);
        } catch (Exception e) {
            logError("findByGenre", e);
            throw e;
        }
    }

    @Operation(summary = "Find books by publisher", description = "Find books by publisher (case insensitive)")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/publisher/{publisher}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> findByPublisher(
            @Parameter(description = "Publisher name") @PathVariable String publisher) {
        logRequest("findByPublisher", publisher);
        validateRequired(publisher, "Publisher");
        
        try {
            List<BookDTO> books = bookService.findByPublisher(publisher);
            return handleListResponse(books, "Books found by publisher", 
                    "No books found by publisher: " + publisher);
        } catch (Exception e) {
            logError("findByPublisher", e);
            throw e;
        }
    }

    @Operation(summary = "Search external books by title", description = "Search books from Google Books API by title")
    @ApiResponse(responseCode = "200", description = "External search completed successfully")
    @GetMapping("/external/search/title")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> searchExternalBooksByTitle(
            @Parameter(description = "Book title to search") @RequestParam String title) {
        logRequest("searchExternalBooksByTitle", title);
        validateRequired(title, "Title");
        
        try {
            List<BookDTO> books = googleBooksService.searchBooksByTitle(title);
            return handleListResponse(books, Messages.EXTERNAL_SEARCH_SUCCESS, 
                    String.format(Messages.NO_EXTERNAL_BOOKS_FOUND, title));
        } catch (Exception e) {
            logError("searchExternalBooksByTitle", e);
            throw e;
        }
    }

    @Operation(summary = "Search external books by author", description = "Search books from Google Books API by author")
    @ApiResponse(responseCode = "200", description = "External search completed successfully")
    @GetMapping("/external/search/author")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> searchExternalBooksByAuthor(
            @Parameter(description = "Author name to search") @RequestParam String author) {
        logRequest("searchExternalBooksByAuthor", author);
        validateRequired(author, "Author");
        
        try {
            List<BookDTO> books = googleBooksService.searchBooksByAuthor(author);
            return handleListResponse(books, Messages.EXTERNAL_SEARCH_SUCCESS, 
                    String.format(Messages.NO_EXTERNAL_BOOKS_FOUND, author));
        } catch (Exception e) {
            logError("searchExternalBooksByAuthor", e);
            throw e;
        }
    }

    @Operation(summary = "Search external books", description = "General search in Google Books API")
    @ApiResponse(responseCode = "200", description = "External search completed successfully")
    @GetMapping("/external/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> searchExternalBooks(
            @Parameter(description = "Search query") @RequestParam String query) {
        logRequest("searchExternalBooks", query);
        validateRequired(query, "Query");
        
        try {
            List<BookDTO> books = googleBooksService.searchBooks(query);
            return handleListResponse(books, Messages.EXTERNAL_SEARCH_SUCCESS, 
                    String.format(Messages.NO_EXTERNAL_BOOKS_FOUND, query));
        } catch (Exception e) {
            logError("searchExternalBooks", e);
            throw e;
        }
    }
}
