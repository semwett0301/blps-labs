package com.example.code.model.exceptions;

public class UserAlreadyExistException extends Exception {
    @Override
    public String getMessage() {
        return "User already exist";
    }
}
