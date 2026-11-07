package com.example.bookstore.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String title, int available, int requested) {
        super("Insufficient stock for '" + title + "'. Available=" + available + ", requested=" + requested);
    }
}