package com.example.code.services.UserService;

import com.example.code.model.entities.UserInfo;
import com.example.code.model.exceptions.UserNotFoundException;
import com.example.code.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceLitRes implements UserService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceLitRes(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void allowNotification(String username) throws UserNotFoundException {
        UserInfo user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setNotificated(true);
        userRepository.save(user);
    }

    @Override
    public void forbidNotification(String username) throws UserNotFoundException {
        UserInfo user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setNotificated(false);
        userRepository.save(user);
    }
}
