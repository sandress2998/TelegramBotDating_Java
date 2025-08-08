package ru.mephi.telegrambotdating_java.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mephi.telegrambotdating_java.database.repository.AuthorizationCodeRepository;
import ru.mephi.telegrambotdating_java.model.service.AuthorizationScheduler;

import java.time.LocalDateTime;

@Service
public class AuthorizationSchedulerImpl implements AuthorizationScheduler {
    @Autowired
    private AuthorizationCodeRepository repository;

    // раз в 5 минут
    @Scheduled(fixedRate = 60000 * 5)
    @Override
    public void deleteExpiredCodes() {
        repository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
