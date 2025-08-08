package ru.mephi.telegrambotdating_java.model.data;

import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

public class SpareMessageData {
    private final String chatId;
    private final String text;
    private final Long messageId;
    private final AbsSender sender;

    public SpareMessageData(String chatId, String text, Long messageId, AbsSender sender) {
        this.chatId = chatId;
        this.text = text;
        this.messageId = messageId;
        this.sender = sender;
    }

    public String getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public Optional<Long> getMessageId() {
        return Optional.ofNullable(messageId);
    }

    public AbsSender getSender() {
        return sender;
    }
}
