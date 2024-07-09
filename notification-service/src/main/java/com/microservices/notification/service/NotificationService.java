package com.microservices.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    /*
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderPlaced(String orderDetails) {
        log.info("Received order placed message: {}", orderDetails);
        // Extract order and user details from orderDetails
        // Send email
        sendConfirmationEmail("user@example.com", orderDetails);
    }

     */

    public void sendConfirmationEmail(String toEmail, String orderDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("yasmine.haidri@student.htw-berlin.de");
        message.setSubject("Order Confirmation");
        message.setText("Your order has been placed successfully. Order details: " + "orderDetails");
        message.setFrom("yasmine.haidri@student.htw-berlin.de");

        javaMailSender.send(message);
        log.info("Order confirmation email sent to: {}", toEmail);
    }
}

