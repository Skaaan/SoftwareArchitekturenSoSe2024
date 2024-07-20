package com.microservices.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender javaMailSender;

    @RabbitListener(queues = "order.confirmation.queue")
    public void handleOrderPlaced(String message) {
        log.info("Received message for order confirmation: {}", message);
        try {
            // Split the incoming message to extract email, name, and order number
            String[] parts = message.split(";");
            if (parts.length < 3) {
                log.error("Invalid message format: {}", message);
                return;
            }
            String toEmail = parts[0];
            String userName = parts[1];
            String orderNumber = parts[2];

            // Validate email address format
            if (toEmail == null || toEmail.isEmpty() || !isValidEmail(toEmail)) {
                log.error("Invalid email address in message: {}", message);
                return;
            }

            log.info("Received request for email confirmation for order number: {}", orderNumber);
            sendConfirmationEmail(toEmail, userName, orderNumber);
        } catch (Exception e) {
            log.error("Failed to process order confirmation message: {}", message, e);
        }
    }

    public void sendConfirmationEmail(String toEmail, String userName, String orderNumber) {
        try {
            if (toEmail == null || toEmail.isEmpty()) {
                log.error("Email address is null or empty, cannot send confirmation email for order number: {}", orderNumber);
                return;
            }

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("📚 Order Confirmation from Readers Insel");
            helper.setFrom(fromEmail);

            // Customize your email content here
            String emailContent = "<html>"
                    + "<body style='font-family: Arial, sans-serif;'>"
                    + "<h2 style='color: #2E8B57;'>Hello, " + userName + "</h2>"
                    + "<p>Thank you for shopping with us at Readers Insel. Your order has been successfully placed.</p>"
                    + "<p>Below are your order details:</p>"
                    + "<table style='border-collapse: collapse; width: 100%; margin-top: 20px;'>"
                    + "  <tr style='background-color: #f2f2f2;'>"
                    + "    <th style='border: 1px solid #ddd; padding: 10px;'>Order Number</th>"
                    + "    <td style='border: 1px solid #ddd; padding: 10px;'>" + orderNumber + "</td>"
                    + "  </tr>"
                    + "</table>"
                    + "<p>Your order will be processed shortly and you will receive a separate email when your items are shipped.</p>"
                    + "<p>If you have any questions or need further assistance, please feel free to contact us.</p>"
                    + "<p>Thank you once again for choosing Readers Insel.</p>"
                    + "<p style='font-size: 0.8em; color: #888;'>"
                    + "This is an automated email. Please do not reply directly to this email. If you have any queries, please contact our customer support."
                    + "</p>"
                    + "<p style='margin-top: 20px; font-size: 0.8em;'>Best regards,<br/>Your Bookstore Team</p>"
                    + "</body>"
                    + "</html>";

            helper.setText(emailContent, true);

            javaMailSender.send(message);
            log.info("Order confirmation email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", toEmail, e);
        }
    }

    // Method to validate email address format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }
}
