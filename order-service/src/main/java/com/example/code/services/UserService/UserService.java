package com.example.code.services.UserService;

import com.example.code.model.exceptions.UserNotFoundException;

public interface UserService {

    void allowNotification(String username) throws UserNotFoundException;

    void forbidNotification(String username) throws UserNotFoundException;
}
