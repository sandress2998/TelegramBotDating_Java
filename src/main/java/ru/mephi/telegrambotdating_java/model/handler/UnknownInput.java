package ru.mephi.telegrambotdating_java.model.handler;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

@Service
public class UnknownInput extends AbstractInput {
    @Override
    public SendMessage handle(SpareMessageData data) {
        return new SendMessage(data.getChatId(), "Неизвестная команда: " + data.getText());
    }
}