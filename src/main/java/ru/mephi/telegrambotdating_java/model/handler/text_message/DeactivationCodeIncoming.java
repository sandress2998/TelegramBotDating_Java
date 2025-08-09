package ru.mephi.telegrambotdating_java.model.handler.text_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.DeactivationCode;
import ru.mephi.telegrambotdating_java.model.data.response.IncorrectInputFormat;
import ru.mephi.telegrambotdating_java.model.data.response.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.model.handler.AbstractTextInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeactivationCodeIncoming extends AbstractTextInput {
    @Autowired
    ActivityButtonChatRepository chatRepository;

    @Override
    public SendMessage handle(SpareMessageData data) {
        String chatId = data.getChatId();
        DeactivationCode deactivationCode = tryParse(data.getText());

        if (deactivationCode == null) {
            return new IncorrectInputFormat(chatId, "Код деактивации должен быть четырехзначным числом.");
        }

        try {
            if (!chatRepository.isDeactivationCodeValid(chatId, deactivationCode.getCode())) {
                if (chatRepository.isCountdownActive(chatId)) {
                    chatRepository.saveActivationTime(chatId, LocalDateTime.now()); // изменяем время отправки alarms на СЕЙЧАС
                    chatRepository.resetCountdownData(chatId, LocalDateTime.now().plusDays(1)); // изменяем время для следующей доступной таблицы, сбрасываем все другие нужные столбцы
                    return new SendMessage(data.getChatId(), "Деактивировано");
                } else {
                    return new SendMessage(data.getChatId(), "Неверный код деактивации");
                }
            } else {
                chatRepository.resetCountdownData(chatId, LocalDateTime.now().plusDays(1));
                return new SendMessage(data.getChatId(), "Деактивировано");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new InternalErrorResponse(chatId, "Что-то пошло не так...");
        }

    }

    static private DeactivationCode tryParse(String text) {
        try {
            int code = Integer.parseInt(text);
            if (code >= 1000 && code <= 9999) {
                return new DeactivationCode(code);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean isBelongToType(String text) {
        return text.split("\n").length == 1 && text.split(" ").length == 1;
    }

    // так как метод handle не транзакционный, то могут появиться некоторые проблемы
    @Scheduled
    @Transactional
    void deleteZombieDeactivationCodes() {
        List<ActivityButtonChat> zombieForms = chatRepository.getByCountdownActiveAndActivationTimeIsBefore(true, LocalDateTime.now().minusMinutes(5));
        zombieForms.forEach(chat -> {
            chatRepository.saveActivationTime(chat.chatId, LocalDateTime.now()); // изменяем время отправки alarms на СЕЙЧАС
            chatRepository.resetCountdownData(chat.chatId, LocalDateTime.now().plusDays(1)); // изменяем время для следующей доступной таблицы, сбрасываем все другие нужные столбцы
        });
    }
}
