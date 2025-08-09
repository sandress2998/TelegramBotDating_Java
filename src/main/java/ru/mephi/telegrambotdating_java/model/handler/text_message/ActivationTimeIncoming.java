package ru.mephi.telegrambotdating_java.model.handler.text_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.ActivationTime;
import ru.mephi.telegrambotdating_java.model.data.response.AccessDeniedResponse;
import ru.mephi.telegrambotdating_java.model.handler.AbstractTextInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ActivationTimeIncoming extends AbstractTextInput {
    @Autowired
    ActivityButtonChatRepository chatRepository;

    @Override
    public SendMessage handle(SpareMessageData data) {
        String chatId = data.getChatId();
        ActivationTime activationTime = tryParse(data.getText());

        if (activationTime == null) {
            return new SendMessage(chatId, "Введеное время неверно");
        }

        if (chatRepository.isTimeBeforeNextAvailable(chatId, LocalDateTime.now())) {
            LocalDateTime nextAvailableTime = chatRepository.getByChatId(chatId).nextAvailableTime;
            return new AccessDeniedResponse(chatId, String.format("Анкета недоступна. Подождите до %s", nextAvailableTime));
        } else {
            chatRepository.saveActivationTime(chatId, activationTime.getTime());
            return new SendMessage(chatId, "Время успешно изменилось");
        }
    }

    static public ActivationTime tryParse(String text) {
        String pattern = "dd.MM.yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDateTime activationTime = LocalDateTime.parse(text, formatter);
            if (activationTime.isBefore(LocalDateTime.now())) {
                return null;
            } else {
                return new ActivationTime(activationTime);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isBelongToType(String text) {
        return text.split("\n").length == 1 && text.split(" ").length == 2;
    }
}