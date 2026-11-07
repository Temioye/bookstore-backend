package com.example.bookstore.exception;

public class NoBooksFoundException extends RuntimeException {
    public NoBooksFoundException(String author, String title) {
        super("No books found for author='" + author + "', title='" + title + "'");
    }
}