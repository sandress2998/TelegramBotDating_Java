package ru.mephi.telegrambotdating_java.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.telegrambotdating_java.database.entity.AuthorizationCode;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.database.repository.AuthorizationCodeRepository;

import java.time.LocalDateTime;

/**
 * Чисто тестовый контроллер, чтобы протестировать систему
 * Я знаю, что контроллер передает данные сервисам, но здесь пропустим
 * слой "сервис", потому что нам нужен только 1 тестовый метод
 */
@RestController
@RequestMapping("/api/client")
public class ClientController {
    @Autowired
    private AuthorizationCodeRepository authRepository;

    @Autowired
    private ActivityButtonChatRepository formRepository;

    @PostMapping
    AuthorizationCode generateNewClient() {
        return authRepository.save(AuthorizationCode.generateRandom());
    }

    @PostMapping("/time")
    @Transactional
    void resetNextAvailableTime(@RequestParam String chatId) {
        formRepository.setNextAvailableTime(chatId, LocalDateTime.now());
    }
}
