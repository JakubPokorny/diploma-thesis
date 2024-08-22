package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.dto.CheckStockDto;
import cz.upce.fei.dt.backend.dto.ICheckExpiredFinalDeadline;
import cz.upce.fei.dt.backend.dto.ICheckExpiredPartialDeadline;
import cz.upce.fei.dt.backend.services.mappers.MailMapper;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final MailMapper mailMapper;
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
    public void notifyUserAboutMissingComponents(List<CheckStockDto> componentsUnderLimit) throws MailException {
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Async
    public void notifyUserWhichCreatedExpiredContract(List<ICheckExpiredFinalDeadline> expired) {
        var mailMap = mailMapper.mapICheckExpiredFinalDeadline(expired);
        sendToEach(mailMap, "Expirované konečné termíny zakázek");
    }

    @Async
    public void notifyUserWhichCreatedExpiredPartialDeadline(List<ICheckExpiredPartialDeadline> expired) {
        var mailMap = mailMapper.mapICheckExpiredPartialDeadline(expired);
        sendToEach(mailMap, "Expirované dílčí termíny zakázek");
    }

    private void sendToEach(Map<String, List<String>> mailMap, String subject) {
        mailMap.forEach((email, expiredDetails) -> {

            StringBuilder text = new StringBuilder("Zakázky (" + expiredDetails.size() + "):\n");
            for (String expiredDetail : expiredDetails) {
                text.append(expiredDetail).append("\n");
            }

            text.append("\n\nZasláno automatem.");
            try {
                send(email, subject, text.toString());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
