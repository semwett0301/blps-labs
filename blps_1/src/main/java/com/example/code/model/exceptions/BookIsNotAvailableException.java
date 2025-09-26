package com.example.code.model.exceptions;

public class BookIsNotAvailableException extends Exception {
    @Override
    public String getMessage() {
        return "The book that u asked for isn't available";
    }
}
