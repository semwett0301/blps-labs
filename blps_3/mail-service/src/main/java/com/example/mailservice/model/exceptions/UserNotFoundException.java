package com.example.mailservice.model.exceptions;

public class UserNotFoundException extends Exception{
    @Override
    public String getMessage() {
        return "The user that u request for isn't found";
    }
}
