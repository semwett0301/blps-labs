package com.example.mailservice.services.MailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.topic}")
    private String topic;

    @Value("${spring.mail.username}")
    private String fromUser;

    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
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
