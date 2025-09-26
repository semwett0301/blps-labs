package com.example.code.model.exceptions;

public class TimeIsNotAvailableException extends Exception{
    @Override
    public String getMessage() {
        return "The time u're trying to choose isn't available";
    }
}
