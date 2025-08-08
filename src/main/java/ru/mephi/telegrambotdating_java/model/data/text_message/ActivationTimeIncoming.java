package ru.mephi.telegrambotdating_java.model.data.text_message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;

import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.AccessDeniedResponse;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InvalidDataInput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ActivationTimeIncoming extends AbstractInput {
    private final LocalDateTime time;

    public ActivationTimeIncoming(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        if (repository == null) {
            return new InternalErrorResponse(data.getChatId(), "Repository is null");
        }

        UUID chatId = UUID.fromString(data.getChatId());
        if (repository.isTimeBeforeNextAvailable(chatId, LocalDateTime.now())) {
            LocalDateTime nextAvailableTime = repository.getByChatId(chatId).nextAvailableTime;
            return new AccessDeniedResponse(data.getChatId(), String.format("Анкета недоступна. Подождите до %s", nextAvailableTime));
        } else {
            repository.saveActivationTime(chatId, time);
            return new SendMessage(data.getChatId(), "Время успешно изменилось");
        }
    }

    static public AbstractInput tryParse(String text) {
        String pattern = "dd.MM.yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDateTime activationTime = LocalDateTime.parse(text, formatter);
            if (activationTime.isBefore(LocalDateTime.now())) {
                return new InvalidDataInput("Некорректный ввод: введеное время уже прошло");
            } else {
                return new ActivationTimeIncoming(activationTime);
            }
        } catch (Exception e) {
            return null;
        }
    }
}