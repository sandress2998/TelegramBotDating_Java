package ru.mephi.telegrambotdating_java.model.data.bad_request;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.TelegramBotRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

public class InvalidDataInput extends AbstractInput {
    private final String message;

    public InvalidDataInput(String message) {
        this.message = message;
    }

    @Override
    public SendMessage handle(SpareMessageData data, TelegramBotRepository repository) {
        return new SendMessage(data.getChatId(), "Некорректный ввод данных: " + message);
    }
}

