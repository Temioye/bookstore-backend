package com.example.bookstore.repository;
//Repository Layer:Data Access layer - manages interaction with the database.
import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


//Reference[JPA findBy field In ignore case]: https://github.com/spring-projects/spring-boot/issues/17574
//Reference[Query Methods Documents]: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
public interface BookRepository extends JpaRepository<Book, Integer> {

    // Prevent duplicates (ISBN is unique if provided)
    boolean existsByIsbn(String isbn);

    // Filter endpoints (optional for GET /books)
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByAuthorContainingIgnoreCaseAndTitleContainingIgnoreCase(String author, String title, Pageable pageable);


}