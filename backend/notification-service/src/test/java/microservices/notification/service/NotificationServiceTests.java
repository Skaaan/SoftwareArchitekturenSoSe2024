package microservices.notification.service;

import com.microservices.notification.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

public class NotificationServiceTests {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Value("${spring.mail.username}")
    private String fromEmail = "test@example.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(notificationService, "fromEmail", fromEmail);
    }

    @Test
    public void testInvalidEmail() {
        String message = "invalid-email;Test User;12345";
        notificationService.handleOrderPlaced(message);
        verify(javaMailSender, times(0)).send(any(MimeMessage.class));
    }
}
