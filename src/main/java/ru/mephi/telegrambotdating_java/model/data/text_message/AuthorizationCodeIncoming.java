package ru.mephi.telegrambotdating_java.model.data.text_message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

import java.util.UUID;

public class AuthorizationCodeIncoming extends AbstractInput {

    UUID code;

    public AuthorizationCodeIncoming(UUID code) {
        this.code = code;
    }

    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        // ЗДЕСЬ ДОЛЖНЫ БЫТЬ ПРОВЕРКА и ДОБАВЛЕНИЕ новой записи в ActivityButtonChatRepository
    }
}
