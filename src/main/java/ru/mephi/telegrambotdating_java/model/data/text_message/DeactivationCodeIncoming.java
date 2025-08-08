package ru.mephi.telegrambotdating_java.model.data.text_message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InvalidDataInput;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeactivationCodeIncoming extends AbstractInput {
    private final int deactivationCode;

    public DeactivationCodeIncoming(int deactivationCode) {
        this.deactivationCode = deactivationCode;
    }

    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        String chatId = data.getChatId();

        if (repository == null) {
            return new InternalErrorResponse(data.getChatId(), "Repository is null");
        }

        if (!repository.isDeactivationCodeValid(chatId, deactivationCode)) {
            if (repository.isCountdownActive(chatId)) {
                repository.saveActivationTime(chatId, LocalDateTime.now()); // изменяем время отправки alarms на СЕЙЧАС
                repository.resetCountdownData(chatId, LocalDateTime.now().plusDays(1)); // изменяем время для следующей доступной таблицы
                return new SendMessage(data.getChatId(), "Деактивировано");
            } else {
                return new SendMessage(data.getChatId(), "Неверный код деактивации");
            }
        } else {
            repository.resetCountdownData(chatId, LocalDateTime.now().plusDays(1));
            return new SendMessage(data.getChatId(), "Деактивировано");
        }
    }

    static public AbstractInput tryParse(String text) {
        try {
            int code = Integer.parseInt(text);
            if (code >= 1000 && code <= 9999) {
                return new DeactivationCodeIncoming(code);
            }
            return new InvalidDataInput("Нужно ввести четырехзначное число");
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
