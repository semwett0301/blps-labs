package com.example.code.services.UserService;

import com.example.code.model.entities.UserInfo;
import com.example.code.model.exceptions.UserNotFoundException;

public interface UserService {

    UserInfo getUser(String username) throws UserNotFoundException;

    void allowNotification(String username) throws UserNotFoundException;

    void forbidNotification(String username) throws UserNotFoundException;
}
