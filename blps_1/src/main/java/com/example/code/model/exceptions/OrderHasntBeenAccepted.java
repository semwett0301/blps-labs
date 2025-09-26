package com.example.code.model.exceptions;

public class OrderHasntBeenAccepted extends Exception {
    @Override
    public String getMessage() {
        return "The order hasn't been accepted";
    }
}
