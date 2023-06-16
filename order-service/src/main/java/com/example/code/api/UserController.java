package com.example.code.api;

import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/allow-notification")
    public void allowNotification() throws UserNotFoundException {
        userService.allowNotification(username);
    }

    @PostMapping("/forbid-notification")
    public void forbidNotification() throws UserNotFoundException {
        userService.forbidNotification(username);
    }
}
