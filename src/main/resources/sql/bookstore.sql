CREATE DATABASE IF NOT EXISTS bookstore;

USE bookstore;

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    stock INT NOT NULL DEFAULT 0
    );


INSERT INTO books (title, author, price, isbn, stock)
VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', 10.99, '9780743273565', 20),
       ('To Kill a Mockingbird', 'Harper Lee', 7.99, '9780446310789', 15),
       ('1984', 'George Orwell', 8.99, '9780451524935', 25),
       ('Pride and Prejudice', 'Jane Austen', 6.99, '9780141040349', 30),
       ('Moby Dick', 'Herman Melville', 12.50, '9781503280786', 10);