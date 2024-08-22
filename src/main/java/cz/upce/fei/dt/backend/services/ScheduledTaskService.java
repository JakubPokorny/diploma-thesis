package cz.upce.fei.dt.backend.services;

import cz.upce.fei.dt.backend.repositories.ContractRepository;
import cz.upce.fei.dt.backend.repositories.DeadlineRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduledTaskService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);
    private final ContractRepository contractRepository;
    private final DeadlineRepository deadlineRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 7 * * *")
    private void checkExpiredContractByFinalDeadline() {
        var expired = contractRepository.findAllByExpiredFinalDeadline();
        if (!expired.isEmpty())
            emailService.notifyUserWhichCreatedExpiredContract(expired);

        logger.info("Check expired contract by final deadline. Expired: {} contract.", expired.size());
    }

    @Scheduled(cron = "0 0 7 * * *")
    private void checkExpiredContractByCurrentPartialDeadline() {
        var expired = deadlineRepository.findAllExpiredCurrentPartialDeadlines();
        if (!expired.isEmpty())
            emailService.notifyUserWhichCreatedExpiredPartialDeadline(expired);

        logger.info("Check expired contract's partial deadlines. Expired: {} deadlines", expired.size());
    }
}
