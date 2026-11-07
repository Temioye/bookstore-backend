# 📘 Application Requirements
## 1. Project Summary

The Bookstore Backend is a RESTful Spring Boot application that manages books in a bookstore database. It allows users to search for books, retrieve book details, create new books, purchase books (reduce stock), and refund books (increase stock). The application integrates with a MySQL database using JPA and provides OpenAPI documentation via Swagger. The application is containerised using Docker and can be deployed using Docker Compose, which runs both the application and MySQL database services.


## 2. Features

The application provides the following functionality:

- Search books by author and/or title

- View all books currently in stock

- Retrieve a single book by ID

- Create a new book

- Purchase a book (reduces stock quantity)

- Refund a book (increases stock quantity)

- Input validation using Jakarta Validation annotations

- Custom exception handling with a global exception handler

- OpenAPI (Swagger) documentation

## 3. Key Endpoints

| Method | Endpoint                                   | Description                         |
| ------ | ------------------------------------------ | ----------------------------------- |
| GET    | `/books/search`                            | Search books by author and/or title |
| GET    | `/books/get_stock`                         | Retrieve all books in stock         |
| GET    | `/books/{id}`                              | Retrieve a book by ID               |
| POST   | `/books`                                   | Create a new book                   |
| POST   | `/books/purchase/{id}?quantity={quantity}` | Purchase a book                     |
| POST   | `/books/refund/{id}?quantity={quantity}`   | Refund a book                       |

## 4. Marked Endpoints (For Assessment)

The two primary endpoints selected for assessment are:

#### 1. `GET /books/search`

- Supports optional query parameters (author, title)

- Returns 404 if no books are found

- Demonstrates filtering logic and exception handling

#### 2. `POST /books/purchase/{id}?quantity={quantity}`

- Validates quantity input

- Reduces stock accordingly

- Throws appropriate exceptions for:

- Invalid quantity (400)

- Book not found (404)

- Insufficient stock (409)

These endpoints demonstrate core business logic, validation, and error handling.

5. Data Model Summary
   Book Entity

The Book entity contains the following fields:

- id (Integer) – Primary key

- title (String) – Book title

- author (String) – Book author

- price (BigDecimal) – Book price

- isbn (String) – Unique ISBN identifier

- stock (Integer) – Available quantity in stock

The entity is mapped using JPA and stored in a MySQL database.

## 6. Error Handling

The application uses custom exceptions and a global exception handler to ensure consistent error responses.

### HTTP Status Codes Used

- #### 400 Bad Request

    - Invalid quantity (e.g., quantity ≤ 0)

    - Validation errors (e.g., blank title or author)

- #### 404 Not Found

    - Book not found by ID

    - No books found during search

- #### 409 Conflict

    - Duplicate ISBN

    - Insufficient stock for purchase

All exceptions are handled centrally using @RestControllerAdvice to return structured JSON responses.

## 7. OpenAPI Documentation

The API documentation is available via Swagger:

- Swagger UI:
http://localhost:8080/swagger-ui/index.html

- OpenAPI JSON:
http://localhost:8080/v3/api-docs

This documentation provides interactive testing of all endpoints and displays request/response schemas.