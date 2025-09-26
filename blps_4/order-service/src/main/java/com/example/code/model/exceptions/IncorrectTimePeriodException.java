package com.example.code.model.exceptions;

public class IncorrectTimePeriodException extends Exception {
    @Override
    public String getMessage() {
        return "You sent incorrect period of time";
    }
}
