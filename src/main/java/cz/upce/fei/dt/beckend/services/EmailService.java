package cz.upce.fei.dt.beckend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender sender;
    @Value("${spring.mail.username}")
    private String FROM;
    public void send(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        sender.send(message);
    }

    public void sendStockNotification(String to, String componentName, int componentsInStock, int minComponentsInStock){
        String subject = "Sklad: %s na %d pod %d".formatted(componentName, componentsInStock, minComponentsInStock);
        String text = "Komponenta %s skladem %d, tedy pod nastavenou hranici %d.".formatted(componentName, componentsInStock, minComponentsInStock);
        send(to, subject, text);
    }
}
