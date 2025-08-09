package ru.mephi.telegrambotdating_java.model.data.response;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class IncorrectInputFormat extends SendMessage {
    public IncorrectInputFormat(String chatId, String text) {
        this.setChatId(chatId);
        this.setText("Неверный формат ввода" + text);
    }
}
