package ru.mephi.telegrambotdating_java.model.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

public abstract class AbstractInput {
    public abstract SendMessage handle(SpareMessageData data);
}

