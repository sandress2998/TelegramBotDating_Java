package ru.mephi.telegrambotdating_java.model.data.bad_request;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class InternalErrorResponse extends SendMessage {
    public InternalErrorResponse(String chatId, String message) {
        new SendMessage(chatId, "Внутренняя ошибка: " + message);
    }
}