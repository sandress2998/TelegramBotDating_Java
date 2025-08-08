package ru.mephi.telegrambotdating_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.telegrambotdating_java.database.entity.AuthorizationCode;
import ru.mephi.telegrambotdating_java.database.repository.AuthorizationCodeRepository;

/**
 * Чисто тестовый контроллер, чтобы протестировать систему
 * Я знаю, что контроллер передает данные сервисам, но здесь пропустим
 * слой "сервис", потому что нам нужен только 1 тестовый метод
 */
@RestController
@RequestMapping("/api/client")
public class ClientController {
    @Autowired
    private AuthorizationCodeRepository repository;

    @PostMapping
    AuthorizationCode generateNewClient() {
        return repository.save(AuthorizationCode.generateRandom());
    }
}
