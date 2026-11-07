# Manual Test Plan - Book Service

## Document Information
- **Project:** Bookstore Backend
- **Components:** BookController & BookService
- **Version:** 1.0
- **Last Updated:** February 2026

---

## Test Objectives

Validate that the Book Service API endpoints and business logic function correctly, handling both valid inputs and error scenarios appropriately.

**Goals:**
- ✅ Verify all CRUD operations work end-to-end
- ✅ Confirm proper error handling and validation
- ✅ Test business logic (stock management, ISBN validation)
- ✅ Validate API responses match specifications

---

## Test Environment Setup

### Prerequisites
1. **Start Application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Verify: `http://localhost:8080`

3. **Test Tools:** Postman, cURL, or Browser

4. **Test Data:** Database with sample books (various stock levels including zero)

### Verification
```bash
curl http://localhost:8080/books/get_stock
```

---

## Test Cases

### **CONTROLLER TESTS** (7 Tests)

#### TC-C01: Search Books - Valid Search
**Endpoint:** `GET /books/search?author={author}&title={title}`

| Test Data | Expected Result |
|-----------|----------------|
| `?author=Test&title=Book` | HTTP 200, JSON array with matching books |

**Pass Criteria:** Returns books with all fields (`id`, `title`, `author`, `price`, `isbn`, `stock`)

---

#### TC-C02: Search Books - No Results
**Endpoint:** `GET /books/search?author=NonExistent&title=Fake`

| Expected Result |
|----------------|
| HTTP 404, Error message: "No books found for author='NonExistent', title='Fake'" |

**Pass Criteria:** Returns 404 with clear error message

---

#### TC-C03: Get All Books
**Endpoint:** `GET /books/get_stock`

| Expected Result |
|----------------|
| HTTP 200, JSON array containing all books in inventory |

**Pass Criteria:** Returns complete inventory with all book details

---

#### TC-C04: Get Book by ID - Valid
**Endpoint:** `GET /books/{id}`

| Test Data | Expected Result |
|-----------|----------------|
| `GET /books/1` | HTTP 200, Single book object with matching ID |

**Pass Criteria:** Returns correct book with all fields populated

---

#### TC-C05: Purchase Book - Sufficient Stock
**Endpoint:** `POST /books/purchase/1?quantity=3`

| Initial Stock | Quantity | Expected Result |
|---------------|----------|----------------|
| 10 | 3 | Success message: "You have successfully purchased the book {title}!" |

**Verification Steps:**
1. Check success message contains book title
2. Verify stock decreased by 3 (call `GET /books/1`)

**Pass Criteria:** Success message returned AND stock updated correctly

---

#### TC-C06: Purchase Book - Insufficient Stock
**Endpoint:** `POST /books/purchase/1?quantity=100`

| Initial Stock | Quantity | Expected Result |
|---------------|----------|----------------|
| 10 | 100 | Error message: "Insufficient stock available" |

**Verification:** Stock remains unchanged

**Pass Criteria:** Error message returned, stock NOT modified

---

#### TC-C07: Refund Book
**Endpoint:** `POST /books/refund/1?quantity=2`

| Initial Stock | Quantity | Expected Result |
|---------------|----------|----------------|
| 10 | 2 | Success message: "You have successfully returned the book {title}!" |

**Verification:** Stock increased by 2

**Pass Criteria:** Success message AND stock increased correctly

---

### **SERVICE TESTS** (10 Tests)

#### TC-S01: Create Book - Valid Request
**Business Logic Test**

**Test Data:**
```json
{
  "title": "New Book",
  "author": "New Author",
  "price": 19.99,
  "isbn": "111-1-111111-11-1",
  "stock": 5
}
```

**Expected Behavior:**
- Check ISBN doesn't exist in database
- Save new book
- Return saved book with generated ID

**Pass Criteria:** Book created successfully with unique ISBN

---

#### TC-S02: Create Book - Duplicate ISBN
**Business Logic Test**

**Test Data:** Use existing ISBN (e.g., "978-0-123456-78-9")

**Expected Behavior:**
- Throw `DuplicateIsbnException`
- Do NOT save book to database

**Pass Criteria:** Exception thrown, no database save occurs

---

#### TC-S03: Get Book by ID - Existing Book
**Business Logic Test**

**Test Data:** Valid book ID (e.g., 1)

**Expected Behavior:**
- Retrieve book from database
- Return book object

**Pass Criteria:** Returns correct book with all details

---

#### TC-S04: Get Book by ID - Non-existent Book
**Business Logic Test**

**Test Data:** Invalid ID (e.g., 999)

**Expected Behavior:**
- Throw `BookNotFoundException`

**Pass Criteria:** Exception thrown for missing book

---

#### TC-S05: Purchase Book - Valid Transaction
**Business Logic Test**

**Initial State:** Book with stock = 10

**Action:** Purchase quantity = 3

**Expected Behavior:**
- Decrease stock to 7
- Save updated book to database

**Pass Criteria:** Stock decreases correctly and persists

---

#### TC-S06: Purchase Book - Zero Quantity
**Business Logic Test**

**Action:** Purchase quantity = 0

**Expected Behavior:**
- Throw `IllegalArgumentException`
- No database changes

**Pass Criteria:** Exception thrown, stock unchanged

---

#### TC-S07: Purchase Book - Insufficient Stock
**Business Logic Test**

**Initial State:** Book with stock = 2

**Action:** Purchase quantity = 5

**Expected Behavior:**
- Throw `InsufficientStockException`
- No database changes

**Pass Criteria:** Exception thrown, stock unchanged

---

#### TC-S08: Refund Book - Valid Transaction
**Business Logic Test**

**Initial State:** Book with stock = 10

**Action:** Refund quantity = 2

**Expected Behavior:**
- Increase stock to 12
- Save updated book to database

**Pass Criteria:** Stock increases correctly and persists

---

#### TC-S09: Refund Book - Negative Quantity
**Business Logic Test**

**Action:** Refund quantity = -1

**Expected Behavior:**
- Throw `IllegalArgumentException`
- No database changes

**Pass Criteria:** Exception thrown, stock unchanged

---

#### TC-S10: Stock Management - Boundary Testing
**Business Logic Test**

**Test Scenarios:**
| Initial Stock | Action | Quantity | Expected Stock |
|---------------|--------|----------|----------------|
| 1 | Purchase | 1 | 0 |
| 0 | Purchase | 1 | InsufficientStockException |
| 0 | Refund | 5 | 5 |

**Pass Criteria:** All boundary cases handled correctly

---

## Test Execution Guide

### Quick Test Workflow

**1. Controller Tests (Using Postman/cURL):**
```bash
# TC-C01: Search
curl "http://localhost:8080/books/search?author=Test&title=Book"

# TC-C03: Get all books
curl "http://localhost:8080/books/get_stock"

# TC-C04: Get by ID
curl "http://localhost:8080/books/1"

# TC-C05: Purchase
curl -X POST "http://localhost:8080/books/purchase/1?quantity=3"

# TC-C07: Refund
curl -X POST "http://localhost:8080/books/refund/1?quantity=2"
```

**2. Service Tests (Unit Tests):**
```bash
# Run all service tests
./mvnw test -Dtest=BookServiceTest

# Run specific test
./mvnw test -Dtest=BookServiceTest#createBook_ValidRequest_SavesAndReturnsBook
```

---

## Test Coverage Summary

| Component | Tests | Coverage |
|-----------|-------|----------|
| **BookController** | 7 tests | All 5 endpoints (search, getStock, getById, purchase, refund) |
| **BookService** | 10 tests | Business logic (create, retrieve, purchase, refund, validation) |
| **Total** | **17 tests** | End-to-end API + Business logic |

---

## Key Validation Points

### Controller Layer
✅ HTTP status codes (200 OK, 404 Not Found)  
✅ Response format (JSON arrays/objects)  
✅ Error messages user-friendly  
✅ Data completeness in responses

### Service Layer
✅ ISBN uniqueness enforced  
✅ Stock calculations accurate  
✅ Exceptions thrown appropriately  
✅ Database transactions complete  
✅ Input validation (quantity > 0)

---

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| 404 on all endpoints | Check application is running on port 8080 |
| Empty response on search | Verify database has matching data |
| Stock not updating | Check database transaction commits |
| ISBN duplicate error | Use unique ISBN for each test |

---

## Appendix: Test Data

### Sample Books for Testing
```sql
INSERT INTO books (title, author, price, isbn, stock) VALUES
('Test Book', 'Test Author', 29.99, '978-0-123456-78-9', 10),
('Low Stock Book', 'Author 2', 19.99, '978-0-111111-11-1', 2),
('Out of Stock', 'Author 3', 24.99, '978-0-222222-22-2', 0);
```

### Expected Exceptions
- `BookNotFoundException` - Book ID doesn't exist
- `DuplicateIsbnException` - ISBN already in use
- `InsufficientStockException` - Not enough stock for purchase
- `IllegalArgumentException` - Invalid quantity (≤ 0)

---

 
**Total Test Cases:** 17 (7 Controller + 10 Service)  
**Estimated Execution Time:** 30-45 minutes
