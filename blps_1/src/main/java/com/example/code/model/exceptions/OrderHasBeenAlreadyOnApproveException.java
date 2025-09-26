package com.example.code.model.exceptions;

public class OrderHasBeenAlreadyOnApproveException extends Exception {
    @Override
    public String getMessage() {
        return "The order has been already on approve";
    }
}

