package com.microservices.notification;
import com.microservices.notification.service.NotificationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(NotificationServiceApplication.class, args);

    // Retrieve the NotificationService bean and call the trigger method
    NotificationService notificationService = context.getBean(NotificationService.class);
        notificationService.sendConfirmationEmail("yasmine.haidri@student.htw-berlin.de", "Order details here");
        context.close();
}
   // public static void main(String[] args) {SpringApplication.run(NotificationServiceApplication.class, args);
}
