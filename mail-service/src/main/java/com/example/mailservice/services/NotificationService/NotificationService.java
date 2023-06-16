package com.example.mailservice.services.NotificationService;

import com.example.mailservice.model.dto.kafka.OrderDTO;
import com.example.mailservice.model.exceptions.UserNotFoundException;

public interface NotificationService {
    void sendNotification(OrderDTO orderDTO) throws UserNotFoundException;
}
