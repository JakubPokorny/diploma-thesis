package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.dto.CheckStockDto;
import cz.upce.fei.dt.generator.CheckStockDtoGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender sender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Value("${spring.mail.username}")
    private String FROM;

    @Test
    void send() throws InterruptedException {
        emailService.send("email@email.com", "subject", "text");

        verify(sender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("subject", message.getSubject());
        assertEquals("text", message.getText());
        assertTrue(Arrays.asList(Objects.requireNonNull(message.getTo())).contains("email@email.com"));
        assertEquals(FROM, message.getFrom());
    }

    @Test
    void sendStockNotification() {
        List<CheckStockDto> underLimit = CheckStockDtoGenerator.generateCheckStockDto(List.of(1L, 2L));

        emailService.sendStockNotification(underLimit);

        verify(sender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("Skladové zásoby pod limitem", message.getSubject());
        assertTrue(Objects.requireNonNull(message.getText()).contains("Component1"));
        assertTrue(Objects.requireNonNull(message.getText()).contains("Component2"));
        assertTrue(Arrays.asList(Objects.requireNonNull(message.getTo())).contains("email@email.com"));
        assertEquals(FROM, message.getFrom());
    }
}