package com.microservices.notification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceApplication {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @RabbitListener(queues = "orderQueue")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());
        sendEmail(orderPlacedEvent.getUserEmail(), "Order Confirmation", 
                  "Thank you for your order. Your order number is " + orderPlacedEvent.getOrderNumber());
    }
    
    /* 
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(fromEmail);
        mailSender.send(message);
    }
    */
}
