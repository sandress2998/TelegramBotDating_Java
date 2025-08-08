package ru.mephi.telegrambotdating_java.model.data.bad_request;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

public class UnknownInput extends AbstractInput {
    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        return new SendMessage(data.getChatId(), "Неизвестная команда: " + data.getText());
    }
}