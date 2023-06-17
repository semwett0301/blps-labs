package com.example.mailservice.services.NotificationService;

import com.example.mailservice.model.dto.kafka.OrderDTO;
import com.example.mailservice.model.entities.UserInfo;
import com.example.mailservice.model.exceptions.UserNotFoundException;
import com.example.mailservice.repositories.UserRepository;
import com.example.mailservice.services.MailService.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceMail implements NotificationService {
    private final UserRepository userRepository;
    private final MailService mailService;

    private Map<Integer, OrderDTO> ordersMap = new HashMap<>();

    @Autowired
    public NotificationServiceMail(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    @KafkaListener(id = "order", topics = "order-topic", containerFactory = "kafkaListenerContainerFactory")
    public void sendNotification(String orderDTOString) throws UserNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        OrderDTO orderDTO = objectMapper.readValue(orderDTOString, OrderDTO.class);

        ordersMap.put(orderDTO.getNumber(), orderDTO);

        UserInfo userInfo = userRepository.findByUsername(orderDTO.getUsername()).orElseThrow(UserNotFoundException::new);
        if (userInfo.isNotificated()) {
            mailService.send(userInfo.getEmail(), orderDTO.toEmail());
        }
    }

    @Override
    @Scheduled(fixedRate = 60 * 1000)
    public void broadcast() {
        Map<String, String> usersToMessages = new HashMap<>();

        ordersMap.keySet().forEach(a -> {
            OrderDTO orderDTO = ordersMap.get(a);
            UserInfo userInfo = userRepository.findByUsername(orderDTO.getUsername()).orElseThrow();
            if (userInfo.isNotificated()) {
                String email = userInfo.getEmail();

                if (usersToMessages.get(email) == null) {
                    usersToMessages.put(email, orderDTO.toEmail());
                } else {
                    usersToMessages.put(email, usersToMessages.get(orderDTO.getUsername()) + orderDTO.toEmail());
                }
            }
        });

        usersToMessages.keySet().forEach(email -> {
            mailService.send(email, usersToMessages.get(email));
        });
    }
}
