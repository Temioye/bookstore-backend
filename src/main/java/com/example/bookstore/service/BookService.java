package com.example.bookstore.service;

// Service Layer: This layer contains the main logic of the business used in the application. Purpose is the same Util - so we don't need to create

import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.exception.DuplicateIsbnException;
import com.example.bookstore.exception.InsufficientStockException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.request.BookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * log.warn() -  warnings/anomalies - in user behavior or data inputs that are not breaking the system but still require attention.
 * log.error() - critical issues/failures - where the system is unable to proceed due to an unrecoverable state.
 * log.info() - successful operations or key state changes in the system which provide useful context for monitoring, tracing, or auditing.
 * */
@Service
@RequiredArgsConstructor
@Slf4j

public class BookService {

    private final BookRepository bookRepository;

    public Book createBook(BookRequest request) {

        // Check duplicate ISBN if provided
        if (request.getIsbn() != null && !request.getIsbn().isBlank()
                && bookRepository.existsByIsbn(request.getIsbn())) {
            log.warn("Attempt to create duplicate ISBN: isbn='{}', title='{}', author='{}'", request.getIsbn(), request.getTitle(), request.getAuthor());
            throw new DuplicateIsbnException(request.getIsbn());
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .price(request.getPrice())
                .isbn(request.getIsbn())
                .stock(request.getStock() == null ? 0 : request.getStock())
                .build();

        Book saved = bookRepository.save(book);
        log.info("Created book id={}, title='{}', author='{}'", saved.getId(), saved.getTitle(), saved.getAuthor());
        return saved;

    }

    // Search by author and/or title. If none provided, returns all (paged).
    //Reference[Pagination]: https://www.baeldung.com/spring-data-jpa-pagination-sorting
    //Reference[Pagination]: https://www.geeksforgeeks.org/advance-java/pagination-and-sorting-with-spring-data-jpa/
    public Page<Book> search(String author, String title) {
        Pageable pageable = PageRequest.of(0, 10);
        boolean hasAuthor = author != null && !author.isBlank();
        boolean hasTitle = title != null && !title.isBlank();

        if (hasAuthor && hasTitle) {
            return bookRepository.findByAuthorContainingIgnoreCaseAndTitleContainingIgnoreCase(author, title, pageable);
        } else if (hasAuthor) {
            return bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
        } else if (hasTitle) {
            return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        }

        return bookRepository.findAll(pageable);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found with ID '{}'. Check the ID or contact support for assistance.", id);
                    return new BookNotFoundException(id);
                });
    }

    public void purchaseBook(int id, int quantity) {
        if (quantity <= 0) {
            log.warn("Invalid purchase quantity: quantity='{}'. Purchase attempt rejected.", quantity);
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        Book book = getBookById(id);
        if (book.getStock() <= 0) {
            log.error("Insufficient stock for purchase: bookId={}, title='{}', currentStock={}, requestedQuantity={}", book.getId(), book.getTitle(), book.getStock(), quantity);
            throw new InsufficientStockException(book.getTitle(), book.getStock(), quantity);
        }

        if (book.getStock() < quantity) {
            log.error("Insufficient stock to fulfill purchase request: bookId={}, title='{}', currentStock={}, requestedQuantity={}", book.getId(), book.getTitle(), book.getStock(), quantity);
            throw new InsufficientStockException(book.getTitle(), book.getStock(), quantity);
        }

        book.setStock(book.getStock() - quantity);
        bookRepository.save(book);
    }


    public void refundBook(int id, int quantity) {
        if (quantity <= 0) {
            log.warn("Invalid refund quantity: quantity='{}'. Refund attempt rejected.", quantity);
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        Book book = getBookById(id);
        book.setStock(book.getStock() + quantity);
        bookRepository.save(book);}

}