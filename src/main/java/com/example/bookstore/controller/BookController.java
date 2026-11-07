package com.example.bookstore.controller;

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.NoBooksFoundException;
import com.example.bookstore.request.BookRequest;
import com.example.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//Controller: This layer is responsible for handling user input, processing HTTP requests, and returning responses.
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Books",
        description = "Operations for searching books, viewing stock, purchasing and refunding books"
)
public class BookController {

    private final BookService bookService;
    @Operation(
            summary = "Search books",
            description = "Search for books by author and/or title. Returns 404 if no books are found."
    )
    @GetMapping("/search")
    public List<Book> searchBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title
    ) {
        Page<Book> result = bookService.search(author, title);
        if (result.isEmpty()) {
            throw new NoBooksFoundException(author, title);
        }

        return result.getContent();

    }

    @Operation(
            summary = "Get all books in stock",
            description = "Returns a list of all books currently stored in the database."
    )
    @GetMapping("/get_stock")
    public List<Book> getStock() {
        return bookService.getAllBooks();
    }

    // Get one book by ID (service throws BookNotFoundException → handled globally)
    @Operation(
            summary = "Get book by ID",
            description = "Returns a single book by its ID. Returns 404 if the book is not found."
    )
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable int id) {
        return bookService.getBookById(id);
    }

    /**
     * Create a new book.
     * Uses validation annotations in BookRequest (@NotBlank, @Min, etc.)
     * If ISBN already exists, service should throw DuplicateIsbnException (409).
     */
    @Operation(
            summary = "Create a new book",
            description = "Creates a new book using the provided details. Returns 409 if ISBN already exists."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@Valid @RequestBody BookRequest request) {
        return bookService.createBook(request);
    }


    //Reference[String Formatting]: https://www.w3schools.com/java/ref_string_format.asp
    /**
     * Purchase a book.
     * If quantity <= 0 => 400
     * If book not found => 404
     * If insufficient stock => 409
     *
     * Returning 204 No Content keeps it simple (success = no error).
     */
    @Operation(
            summary = "Purchase a book",
            description = "Purchases a book by ID and quantity. Returns 409 if insufficient stock."
    )
    @PostMapping("/purchase/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purchaseBook(@PathVariable int id, @RequestParam int quantity) {
        bookService.purchaseBook(id, quantity);
        log.info("Purchase successful for book id={}, quantity={}", id, quantity);
    }


    /**
     * Refund a book.
     * If quantity <= 0 => 400
     * If book not found => 404
     *
     * Returning 204 No Content keeps it consistent with purchase.
     */
    @Operation(
            summary = "Refund a book",
            description = "Refunds a book by ID and quantity, increasing stock accordingly."
    )
    @PostMapping("/refund/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refundBook(@PathVariable int id, @RequestParam int quantity) {
        bookService.refundBook(id, quantity);
        log.info("Refund successful for book id={}, quantity={}", id, quantity);
    }
}

