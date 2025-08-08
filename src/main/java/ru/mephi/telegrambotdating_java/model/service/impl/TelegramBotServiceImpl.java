package ru.mephi.telegrambotdating_java.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.entity.AuthorizationCode;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.database.repository.AuthorizationCodeRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InvalidDataInput;
import ru.mephi.telegrambotdating_java.model.data.bad_request.UnknownInput;
import ru.mephi.telegrambotdating_java.model.data.button.*;
import ru.mephi.telegrambotdating_java.model.data.text_message.ActivationTimeIncoming;
import ru.mephi.telegrambotdating_java.model.data.text_message.AlarmSendingFormIncoming;
import ru.mephi.telegrambotdating_java.model.data.text_message.AuthorizationDataIncoming;
import ru.mephi.telegrambotdating_java.model.data.text_message.DeactivationCodeIncoming;
import ru.mephi.telegrambotdating_java.model.service.TelegramBotService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TelegramBotServiceImpl implements TelegramBotService {
    @Autowired
    private ActivityButtonChatRepository activityButtonChatRepository;

    @Autowired
    private AuthorizationCodeRepository authorizationCodeRepository;

    @Transactional
    @Override
    public SendMessage handleIncomingInfo(SpareMessageData data) {
        /* Пришлось потанцевать с бубном из-за того, что передавать репозиторий в handle -
         * оказалось не лучшим решением, особенно учитывая тот факт, что сигнатура
         * метода handle везде одинаковая. Все же со Spring оказалось труднее реализовать
         * богатую модель, а не анемичную.
         */
        AbstractInput command = switch (data.getText()) {
            case "/start" -> new StartButton();
            case "Анкета" -> new FormFillingButton();
            case "Информация" -> new InformationButton();
            case "Экстренная активация" -> new EmergencyActivationButton();
            case "Настроить время активации" -> new ScheduledActivationButton();
            default -> trySpecialParse(data);
        };
        return command.handle(data, activityButtonChatRepository);
    }

    private AbstractInput trySpecialParse(SpareMessageData data) {
        AbstractInput input;
        String text = data.getText();

        input = ActivationTimeIncoming.tryParse(text);
        if (input != null) {
            return input;
        }

        input = AlarmSendingFormIncoming.tryParse(text);
        if (input != null) {
            return input;
        }

        input = AuthorizationDataIncoming.tryParse(text);
        if (input != null) {
            return input;
        }

        input = DeactivationCodeIncoming.tryParse(text);
        if (input != null) {
            return input;
        }

        // проблема образовалась с авторизацией
        input = AuthorizationDataIncoming.tryParse(text);
        if (input instanceof AuthorizationDataIncoming) {
            if (authorize((AuthorizationDataIncoming) input)) {
                // если данные были введены верно
                return input;
            } else {
                // если данные были введены неверно (формат верный, но такого кода авторизации не существует)
                return new InvalidDataInput("Неправильные данные авторизации");
            }
        }

        return new UnknownInput();
    }

    private Boolean authorize(AuthorizationDataIncoming input) {
        UUID code = input.code;

        AuthorizationCode authData = authorizationCodeRepository.getByCodeAndExpiresAtAfter(code, LocalDateTime.now());
        if (authData != null) {
            authorizationCodeRepository.deleteByCode(code);
            input.clientId = authData.clientId;
            return true;
        }
        return false;
    }
}