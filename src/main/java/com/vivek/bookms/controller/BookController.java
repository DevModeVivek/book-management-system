package com.vivek.bookms.controller;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.service.IBookService;
import com.vivek.bookms.service.IGoogleBooksService;
import com.vivek.bookms.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Operations for managing books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    
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
    public ResponseEntity<Map<String, Object>> getAllBooks() {
        logger.info("Request to get all books");
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseBuilder.buildSuccessResponse(books, "Books retrieved successfully");
    }

    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logger.info("Request to get book with id: {}", id);
        BookDTO book = bookService.getBookById(id);
        return ResponseBuilder.buildSuccessResponse(book, "Book retrieved successfully");
    }

    @Operation(summary = "Create a new book", description = "Add a new book to the database")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        logger.info("Request to create new book: {}", bookDTO.getTitle());
        BookDTO createdBook = bookService.createBook(bookDTO);
        return ResponseBuilder.buildCreatedResponse(createdBook, "Book created successfully");
    }

    @Operation(summary = "Update an existing book", description = "Update book details by ID")
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        logger.info("Request to update book with id: {}", id);
        BookDTO updatedBook = bookService.updateBook(id, bookDTO);
        return ResponseBuilder.buildSuccessResponse(updatedBook, "Book updated successfully");
    }

    @Operation(summary = "Delete a book", description = "Remove a book from the database")
    @ApiResponse(responseCode = "204", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        logger.info("Request to delete book with id: {}", id);
        bookService.deleteBook(id);
        return ResponseBuilder.buildNoContentResponse("Book deleted successfully");
    }

    @Operation(summary = "Search books", description = "Search books by title or author")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(
            @Parameter(description = "Search query") @RequestParam String query) {
        logger.info("Request to search books with query: {}", query);
        List<BookDTO> books = bookService.searchBooks(query);
        return ResponseBuilder.buildSuccessResponse(books, "Search completed successfully");
    }

    @Operation(summary = "Search external books by title", description = "Search books from Google Books API by title")
    @ApiResponse(responseCode = "200", description = "External search completed successfully")
    @GetMapping("/external/search/title")
    public ResponseEntity<Map<String, Object>> searchExternalBooksByTitle(
            @Parameter(description = "Book title to search") @RequestParam String title) {
        logger.info("Request to search external books by title: {}", title);
        List<BookDTO> books = googleBooksService.searchBooksByTitle(title);
        return ResponseBuilder.buildSuccessResponse(books, "External search by title completed successfully");
    }

    @Operation(summary = "Search external books by author", description = "Search books from Google Books API by author")
    @ApiResponse(responseCode = "200", description = "External search completed successfully")
    @GetMapping("/external/search/author")
    public ResponseEntity<Map<String, Object>> searchExternalBooksByAuthor(
            @Parameter(description = "Author name to search") @RequestParam String author) {
        logger.info("Request to search external books by author: {}", author);
        List<BookDTO> books = googleBooksService.searchBooksByAuthor(author);
        return ResponseBuilder.buildSuccessResponse(books, "External search by author completed successfully");
    }

    @Operation(summary = "Search external books", description = "General search in Google Books API")
    @ApiResponse(responseCode = "200", description = "External search completed successfully")
    @GetMapping("/external/search")
    public ResponseEntity<Map<String, Object>> searchExternalBooks(
            @Parameter(description = "Search query") @RequestParam String query) {
        logger.info("Request to search external books with query: {}", query);
        List<BookDTO> books = googleBooksService.searchBooks(query);
        return ResponseBuilder.buildSuccessResponse(books, "External search completed successfully");
    }
}
