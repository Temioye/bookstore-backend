package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.DuplicateIsbnException;
import com.example.bookstore.exception.InsufficientStockException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.request.BookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book existingBook;

    @BeforeEach
    void setUp() {
        existingBook = Book.builder()
                .id(1)
                .title("Test Book")
                .author("Test Author")
                .price(new BigDecimal("29.99"))
                .isbn("978-0-123456-78-9")
                .stock(10)
                .build();
    }


    // Create book
    @Test
    void createBook_ValidRequest_SavesAndReturnsBook() {
        BookRequest request = BookRequest.builder()
                .title("New Book")
                .author("New Author")
                .price(new BigDecimal("10.00"))
                .isbn("111-1-111111-11-1")
                .stock(5)
                .build();

        when(bookRepository.existsByIsbn("111-1-111111-11-1")).thenReturn(false);

        Book saved = Book.builder()
                .id(2)
                .title("New Book")
                .author("New Author")
                .price(new BigDecimal("10.00"))
                .isbn("111-1-111111-11-1")
                .stock(5)
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        Book result = bookService.createBook(request);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("New Book", result.getTitle());

        verify(bookRepository).existsByIsbn("111-1-111111-11-1");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_DuplicateIsbn_ThrowsDuplicateIsbnException() {
        BookRequest request = BookRequest.builder()
                .title("New Book")
                .author("New Author")
                .price(new BigDecimal("10.00"))
                .isbn("978-0-123456-78-9")
                .stock(5)
                .build();

        when(bookRepository.existsByIsbn("978-0-123456-78-9")).thenReturn(true);

        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(request));

        verify(bookRepository).existsByIsbn("978-0-123456-78-9");
        verify(bookRepository, never()).save(any(Book.class));
    }


    // Get book by ID
    @Test
    void getBookById_ExistingId_ReturnsBook() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));

        Book result = bookService.getBookById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(bookRepository).findById(1);
    }

    @Test
    void getBookById_MissingId_ThrowsBookNotFoundException() {
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(999));

        verify(bookRepository).findById(999);
    }

    // Purchase
    @Test
    void purchaseBook_ValidQuantity_DecreasesStockAndSaves() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.purchaseBook(1, 3);

        assertEquals(7, existingBook.getStock());
        verify(bookRepository).findById(1);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void purchaseBook_InvalidQuantity_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> bookService.purchaseBook(1, 0));

        verify(bookRepository, never()).findById(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void purchaseBook_InsufficientStock_ThrowsInsufficientStockException() {
        existingBook.setStock(2);
        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));

        assertThrows(InsufficientStockException.class, () -> bookService.purchaseBook(1, 5));

        verify(bookRepository).findById(1);
        verify(bookRepository, never()).save(any(Book.class));
    }

    // Refund
    @Test
    void refundBook_ValidQuantity_IncreasesStockAndSaves() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.refundBook(1, 2);

        assertEquals(12, existingBook.getStock());
        verify(bookRepository).findById(1);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void refundBook_InvalidQuantity_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> bookService.refundBook(1, -1));

        verify(bookRepository, never()).findById(anyInt());
        verify(bookRepository, never()).save(any(Book.class));
    }
}