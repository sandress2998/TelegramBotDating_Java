package ru.mephi.telegrambotdating_java.model.data;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.TelegramBotRepository;

public abstract class AbstractInput {
    public abstract SendMessage handle(SpareMessageData data, TelegramBotRepository repository);
}

