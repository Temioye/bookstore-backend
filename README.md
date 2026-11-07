# 📚 Bookstore Backend
## Overview

The Bookstore Backend is a Spring Boot REST API that manages books stored in a MySQL database.
It allows users to search, create, purchase, and refund books while enforcing validation and business rules.

The application includes:

- RESTful endpoints

- MySQL database integration

- Custom exception handling

- Unit testing with Mockito

- OpenAPI documentation (Swagger)

- Docker containerisation

---

## You need

- Java 21

- Spring Boot

- Spring Data JPA

- MySQL

- Lombok

- Jakarta Validation

- Mockito (Unit Testing)

- OpenAPI / Swagger

- Docker & Docker Compose

---

# Running the Application (Docker – Recommended)

## You need
- Docker Desktop installed and running

---

## Step 1 – Build and Start Containers

From the project root (same directory as docker-compose.yml):

```
mvn clean package
docker compose up --build
```
This will:

- Build the Spring Boot jar

- Build the Docker image

- Start MySQL container

- Start the application container

- Automatically initialise the database

---

Step 2 – Access the Application

Application:

```
http://localhost:8080
```

Swagger UI:

http://localhost:8080/swagger-ui/index.html

OpenAPI JSON:

http://localhost:8080/v3/api-docs

---

Stop Containers

```
docker compose down
```

---

## Database Configuration

The MySQL database is configured in `docker-compose.yml`.

SQL script located in:

```
src/main/resources/sql
```
is mounted into the MySQL container at:

```
/docker-entrypoint-initdb.d
```

This enables automatic schema creation and initial data setup when the container starts for the first time.

---

## Alternative: Run Without Docker

If running locally without Docker:

1. Create the database manually:

```sql 
CREATE DATABASE bookstore;
```
2. Execute the SQL script:

``` css 
src/main/resources/sql/bookstore.sql
```
3. Update `application.yaml` with your local MySQL credentials.
4. Run:
```
mvn spring-boot:run
```
---

## Key Endpoints

| Method | Endpoint                         | Description     |
| ------ | -------------------------------- | --------------- |
| GET    | `/books/search?author=&title=`   | Search books    |
| GET    | `/books/get_stock`               | Get all books   |
| GET    | `/books/{id}`                    | Get book by ID  |
| POST   | `/books`                         | Create a book   |
| POST   | `/books/purchase/{id}?quantity=` | Purchase a book |
| POST   | `/books/refund/{id}?quantity=`   | Refund a book   |

---

## Marked Endpoints

The following endpoints demonstrate core business logic and validation:

- `GET /books/search`

- `POST /books/purchase/{id}?quantity=`

These include validation, exception handling, and proper HTTP status codes.

---

## Error Handling

The application uses custom exceptions handled globally.

| Status Code | Meaning                                                    |
| ----------- |------------------------------------------------------------|
| 400         | Validation error                                           |
| 404         | Book not found (id) / no books found (author and/or title) |
| 409         | Duplicate ISBN / insufficient stock                        |

---

## Running Tests

Run unit tests using:
```
mvn test
```
Tests cover:

- Service business logic

- Exception scenarios

- Controller behaviour

- Validation rules

---

## Documentation

Additional documentation is located in the `documentation/` folder:

- `application-requirements.md`

- `approach.md`

- `manual-test-plan.md`

- `deployment-plan.md`

## Authors

- Piyaporn Srimanta
- Temi Oyelade
- Ksenia Eroglu