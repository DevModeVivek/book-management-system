package com.vivek.bookms.controller;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.service.IBookService;
import com.vivek.bookms.service.IGoogleBooksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Book controller extending BaseController with comprehensive book management operations
 */
@RestController
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Operations for managing books")
public class BookController extends BaseController<BookDTO, Long> {

    private final IBookService bookService;
    private final IGoogleBooksService googleBooksService;

    @Autowired
    public BookController(IBookService bookService, IGoogleBooksService googleBooksService) {
        this.bookService = bookService;
        this.googleBooksService = googleBooksService;
    }

    @Operation(summary = "Get all books", description = "Retrieve all books from the database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all books")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllBooks() {
        logRequest("getAllBooks");
        try {
            List<BookDTO> books = bookService.getAll();
            return handleListResponse(books, "Books retrieved successfully", "No books found in the database");
        } catch (Exception e) {
            logError("getAllBooks", e);
            throw e;
        }
    }

    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logRequest("getBookById", id);
        try {
            Optional<BookDTO> book = bookService.getById(id);
            return handleOptionalResponse(book, "Book retrieved successfully", "Book not found with ID: " + id);
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
    public ResponseEntity<Map<String, Object>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        logRequest("createBook", bookDTO.getTitle());
        try {
            BookDTO createdBook = bookService.create(bookDTO);
            logSuccess("createBook", "Created book with ID: " + createdBook.getId());
            return handleCreationResponse(createdBook, "Book created successfully");
        } catch (Exception e) {
            logError("createBook", e);
            throw e;
        }
    }

    @Operation(summary = "Update an existing book", description = "Update book details by ID")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        logRequest("updateBook", id, bookDTO.getTitle());
        try {
            BookDTO updatedBook = bookService.update(id, bookDTO);
            logSuccess("updateBook", "Updated book with ID: " + id);
            return handleSuccessResponse(updatedBook, "Book updated successfully");
        } catch (Exception e) {
            logError("updateBook", e);
            throw e;
        }
    }

    @Operation(summary = "Delete a book", description = "Remove a book from the database")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logRequest("deleteBook", id);
        try {
            bookService.delete(id);
            logSuccess("deleteBook", "Deleted book with ID: " + id);
            return handleDeletionResponse("Book deleted successfully");
        } catch (Exception e) {
            logError("deleteBook", e);
            throw e;
        }
    }

    @Operation(summary = "Search books", description = "Search books by title or author")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @Parameter(description = "Search query") @RequestParam String query) {
        logRequest("searchBooks", query);
        try {
            List<BookDTO> books = bookService.searchBooks(query);
            return handleListResponse(books, "Search completed successfully", "No books found matching query: " + query);
        } catch (Exception e) {
            logError("searchBooks", e);
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
        try {
            List<BookDTO> books = googleBooksService.searchBooksByTitle(title);
            return handleListResponse(books, "External search by title completed successfully", "No external books found for title: " + title);
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
        try {
            List<BookDTO> books = googleBooksService.searchBooksByAuthor(author);
            return handleListResponse(books, "External search by author completed successfully", "No external books found for author: " + author);
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
        try {
            List<BookDTO> books = googleBooksService.searchBooks(query);
            return handleListResponse(books, "External search completed successfully", "No external books found for query: " + query);
        } catch (Exception e) {
            logError("searchExternalBooks", e);
            throw e;
        }
    }
}
