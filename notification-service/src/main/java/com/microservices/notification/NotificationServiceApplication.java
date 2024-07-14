package com.microservices.notification;
import com.microservices.notification.service.NotificationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
public class NotificationServiceApplication {

        public static void main(String[] args) {
            SpringApplication.run(NotificationServiceApplication.class, args);
        }

    }
