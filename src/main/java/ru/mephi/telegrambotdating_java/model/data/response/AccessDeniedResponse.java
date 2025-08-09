package ru.mephi.telegrambotdating_java.model.data.response;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class AccessDeniedResponse extends SendMessage {
    public AccessDeniedResponse(String chatId, String message) {
        this.setChatId(chatId);
        this.setText("Ошибка доступа: " + message);
    }
}