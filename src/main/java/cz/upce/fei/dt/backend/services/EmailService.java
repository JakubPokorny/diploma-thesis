package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.dto.CheckStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender sender;
    @Value("${spring.mail.username}")
    private String FROM;

    @Async
    public void send(String to, String subject, String text) throws MailException, InterruptedException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        sender.send(message);
    }

    @Async
    public void sendStockNotification(List<CheckStockDto> componentsUnderLimit) throws MailException {
        HashMap<String, List<CheckStockDto>> mailMap = new HashMap<>();

        for (CheckStockDto checkStockDto : componentsUnderLimit) {
            String email = checkStockDto.getEmail();
            mailMap.computeIfAbsent(email, _ -> new ArrayList<>()).add(checkStockDto);
        }

        mailMap.forEach((email, checkStockDtoList) -> {
            String subject = "Skladové zásoby pod limitem";

            StringBuilder text = new StringBuilder("Komponenty pod limitem:\n");
            for (CheckStockDto checkStockDto : checkStockDtoList) {
                text.append("\t '%s' je skladem %d ks tj. pod limitem %d ks.\n".formatted(checkStockDto.getComponentName(), checkStockDto.getComponentsInStock(), checkStockDto.getMinComponentsInStock()));
            }

            text.append("\n\nZasláno automatem.");
            try {
                send(email, subject, text.toString());
            } catch (InterruptedException _) {
            }
        });
    }
}
