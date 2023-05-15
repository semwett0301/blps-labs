package com.example.code.model.exceptions;

public class OrderNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "The order that u were trying to find wasn't found";
    }
}
