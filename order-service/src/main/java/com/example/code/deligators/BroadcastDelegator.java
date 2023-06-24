package com.example.code.deligators;

import com.example.code.model.entities.Order;
import com.example.code.model.entities.UserInfo;
import com.example.code.repositories.UserRepository;
import com.example.code.services.DeliveryService.DeliveryServiceLitRes;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Component
public class BroadcastDelegator implements JavaDelegate {

    private final DeliveryServiceLitRes deliveryServiceLitRes;
    private final UserRepository userRepository;

    @Value("${mail.topic}")
    private String topic;

    @Value("${spring.mail.username}")
    private String fromUser;

    @Autowired
    private final JavaMailSender javaMailSender;

    public BroadcastDelegator(DeliveryServiceLitRes deliveryServiceLitRes, UserRepository userRepository, JavaMailSender javaMailSender) {
        this.deliveryServiceLitRes = deliveryServiceLitRes;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Map<String, String> usersToMessages = new HashMap<>();
        Map<Integer, Order> ordersMap = deliveryServiceLitRes.ordersMap;
        ordersMap.keySet().forEach(a -> {
            Order orderDTO = ordersMap.get(a);
            UserInfo userInfo = userRepository.findByUsername(orderDTO.getUser().getUsername()).orElseThrow();
            if (userInfo.isNotificated()) {
                String email = userInfo.getEmail();
                usersToMessages.put(email, orderDTO.getOrderStatus().getValue());

            }
        });

        usersToMessages.keySet().forEach(email ->
                send(email, usersToMessages.get(email)));

    }




    @Async
    public void send(String to, String message) {
        try {
            MimeMessage mimeMessage = createMessage(to, message);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailSendException e) {
            throw new IllegalStateException("Failed to send email to " + to);
        }
    }

    private MimeMessage createMessage(String to, String message) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        setHelper(mimeMessage, to, message);
        return mimeMessage;
    }

    private void setHelper(MimeMessage mimeMessage, String to, String message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setFrom(fromUser);
        helper.setTo(to);
        helper.setSubject(topic);
        helper.setText(message, true);
    }
}
