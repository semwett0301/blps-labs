package com.example.mailservice.services.NotificationService;

import com.example.mailservice.model.dto.kafka.OrderDTO;
import com.example.mailservice.model.entities.UserInfo;
import com.example.mailservice.model.exceptions.UserNotFoundException;
import com.example.mailservice.model.modelUtils.KafkaTopics;
import com.example.mailservice.repositories.UserRepository;
import com.example.mailservice.services.MailService.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceMail implements NotificationService {
    private final UserRepository userRepository;
    private MailService mailService;

    @Autowired
    public NotificationServiceMail(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    @KafkaListener(id = "order-group", topics = "order-topic")
    public void sendNotification(OrderDTO orderDTO) throws UserNotFoundException {
        UserInfo userInfo = userRepository.findByUsername(orderDTO.getUsername()).orElseThrow(UserNotFoundException::new);
        if (userInfo.isNotificated()) {
            mailService.send(userInfo.getEmail(), orderDTO.toEmail());
        }
    }
}
