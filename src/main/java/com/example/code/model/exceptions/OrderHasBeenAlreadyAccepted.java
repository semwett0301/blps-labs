package com.example.code.model.exceptions;

public class OrderHasBeenAlreadyAccepted extends Exception{
    public String getMessage() {
        return "Order has been already accepted";
    }
}
