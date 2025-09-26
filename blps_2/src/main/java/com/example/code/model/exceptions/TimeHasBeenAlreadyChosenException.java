package com.example.code.model.exceptions;

public class TimeHasBeenAlreadyChosenException extends Exception {
    public String getMessage() {
        return "Time period has been already chosen";
    }

}
