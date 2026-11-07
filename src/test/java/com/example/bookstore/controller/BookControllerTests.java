package com.example.bookstore.controller;

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.InsufficientStockException;
import com.example.bookstore.exception.NoBooksFoundException;
import com.example.bookstore.request.BookRequest;
import com.example.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookController
 * Focused on core functionality with 9 essential test cases
 */
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book testBook;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1)
                .title("Test Book")
                .author("Test Author")
                .price(new BigDecimal("29.99"))
                .isbn("978-0-123456-78-9")
                .stock(10)
                .build();

        testBooks = List.of(testBook);
    }

    //Search Books
    @Test
    void searchBooks_WithResults_ReturnsBooks() {

        Page<Book> bookPage = new PageImpl<>(testBooks);
        when(bookService.search("Test Author", "Test Book")).thenReturn(bookPage);


        List<Book> result = bookController.searchBooks("Test Author", "Test Book");

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookService).search("Test Author", "Test Book");
    }



    @Test
    void searchBooks_WithNoResults_ThrowsNoBooksFoundException() {
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookService.search("Nobody", "Nothing")).thenReturn(emptyPage);

        NoBooksFoundException ex = assertThrows(
                NoBooksFoundException.class,
                () -> bookController.searchBooks("Nobody", "Nothing")
        );

        assertEquals("No books found for author='Nobody', title='Nothing'", ex.getMessage());
        assertTrue(ex.getMessage().contains("No books found"));
        verify(bookService).search("Nobody", "Nothing");
    }



    // Get Stock
    @Test
    void getStock_ReturnsAllBooks() {

        when(bookService.getAllBooks()).thenReturn(testBooks);

        List<Book> result = bookController.getStock();

        assertEquals(1, result.size());
        verify(bookService).getAllBooks();
    }

    //Get book by ID
    @Test
    void getBookById_WithValidId_ReturnsBook() {

        when(bookService.getBookById(1)).thenReturn(testBook);

        Book result = bookController.getBookById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(bookService).getBookById(1);
    }
    @Test
    void getBookById_WithInvalidId_ThrowsBookNotFoundException() {
        when(bookService.getBookById(999)).thenThrow(new BookNotFoundException(999));

        assertThrows(BookNotFoundException.class, () -> bookController.getBookById(999));

        verify(bookService).getBookById(999);
    }

    // Create book
    @Test
    void createBook_ValidRequest_ReturnsSavedBook() {
        BookRequest request = BookRequest.builder()
                .title("New Book")
                .author("New Author")
                .price(new BigDecimal("10.00"))
                .isbn("111-1-111111-11-1")
                .stock(5)
                .build();

        Book saved = Book.builder()
                .id(2)
                .title("New Book")
                .author("New Author")
                .price(new BigDecimal("10.00"))
                .isbn("111-1-111111-11-1")
                .stock(5)
                .build();

        when(bookService.createBook(request)).thenReturn(saved);

        Book result = bookController.createBook(request);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("New Book", result.getTitle());
        verify(bookService).createBook(request);
    }


    //Purchase book
    @Test
    void purchaseBook_ValidRequest_CallsService() {
        doNothing().when(bookService).purchaseBook(1, 3);

        assertDoesNotThrow(() -> bookController.purchaseBook(1, 3));

        verify(bookService).purchaseBook(1, 3);
    }

    @Test
    void purchaseBook_InsufficientStock_ThrowsInsufficientStockException() {
        doThrow(new InsufficientStockException("Test Book", 1, 20))
                .when(bookService).purchaseBook(1, 20);

        assertThrows(InsufficientStockException.class, () -> bookController.purchaseBook(1, 20));

        verify(bookService).purchaseBook(1, 20);
    }


    //Refund book
    @Test
    void refundBook_ValidRequest_CallsService() {
        doNothing().when(bookService).refundBook(1, 2);

        assertDoesNotThrow(() -> bookController.refundBook(1, 2));

        verify(bookService).refundBook(1, 2);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void refundBook_InvalidQuantity_ThrowsIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Quantity must be > 0"))
                .when(bookService).refundBook(1, 0);

        assertThrows(IllegalArgumentException.class, () -> bookController.refundBook(1, 0));

        verify(bookService).refundBook(1, 0);
    }

}


