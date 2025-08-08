package ru.mephi.telegrambotdating_java.model.data.bad_request;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class AccessDeniedResponse extends SendMessage {
    public AccessDeniedResponse(String chatId, String message) {
        new SendMessage(chatId, "Ошибка доступа: " + message);
    }
}