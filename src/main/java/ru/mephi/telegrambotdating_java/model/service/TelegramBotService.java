package ru.mephi.telegrambotdating_java.model.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

public interface TelegramBotService {
    SendMessage handleIncomingInfo(SpareMessageData data);
}