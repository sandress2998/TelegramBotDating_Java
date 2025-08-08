package ru.mephi.telegrambotdating_java.model.service.impl;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.service.TelegramBotService;

@Component
public class DatingTelegramBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String botUsername;
    private final TelegramBotService telegramBotService;

    public DatingTelegramBot(
            @Value("${telegram.bot.username}") String botUsername,
            TelegramBotService telegramBotService
    ) {
        super(Dotenv.load().get("TELEGRAM_TOKEN"));
        this.botToken = Dotenv.load().get("TELEGRAM_TOKEN");
        this.botUsername = botUsername;
        this.telegramBotService = telegramBotService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage response = telegramBotService.handleIncomingInfo(
                new SpareMessageData(
                        String.valueOf(update.getMessage().getChatId()),
                        update.getMessage().getText(),
                        (long) update.getMessage().getMessageId(),
                        this
                )
            );

            try {
                execute(response);
            } catch (TelegramApiException e) {
                // должно быть логирование
                e.printStackTrace();
            }
        }
    }
}