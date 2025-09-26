package com.example.mailservice.services.NotificationService;

import com.example.mailservice.model.exceptions.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface NotificationService {
    void sendNotification(String orderDTOString) throws UserNotFoundException, JsonProcessingException;

    void broadcast() throws UserNotFoundException;
}
