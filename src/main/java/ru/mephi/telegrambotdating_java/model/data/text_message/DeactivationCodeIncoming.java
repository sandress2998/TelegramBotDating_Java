package ru.mephi.telegrambotdating_java.model.data.text_message;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.mephi.telegrambotdating_java.database.repository.TelegramBotRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;


import java.time.LocalDateTime;
import java.util.UUID;

public class DeactivationCodeIncoming extends AbstractInput {
    private final int deactivationCode;

    public DeactivationCodeIncoming(int deactivationCode) {
        this.deactivationCode = deactivationCode;
    }

    @Override
    public SendMessage handle(SpareMessageData data, TelegramBotRepository repository) {
        UUID chatId = UUID.fromString(data.getChatId());

        if (repository == null) {
            return new InternalErrorResponse(data.getChatId(), "Repository is null");
        }

        if (!repository.isDeactivationCodeValid(chatId, deactivationCode)) {
            if (repository.isCountdownActive(chatId)) {
                AlarmSendingForm alarmMessage = repository.getByChatId(chatId).convertToAlarmSendingForm();
                if (alarmMessage == null) {
                    return new InternalErrorResponse(data.getChatId(), "Alarm is null");
                }
                sendAlarmMessage(data, repository);
                repository.resetCountdownData(chatId, LocalDateTime.now().plusDays(1));
                return new SendMessage(data.getChatId(), "Деактивировано");
            } else {
                return new SendMessage(data.getChatId(), "Неверный код деактивации");
            }
        } else {
            repository.resetCountdownData(chatId, LocalDateTime.now().plusDays(1));
            return new SendMessage(data.getChatId(), "Деактивировано");
        }
    }

    public void sendAlarmMessage(SpareMessageData data, TelegramBotRepository repository) {
        AlarmSendingForm alarmSendingForm = repository.getByChatId(UUID.fromString(data.getChatId())).convertToAlarmSendingForm();
        // и здесь по-хорошему должно быть обращение к таблице clients,
        // чтобы извлечь имя того, кто посылает alarm
        // ну или вначале диалога с ботом надо указать свое имя, чтобы потом в таблицу ActivityButtonChat это имя запихать
        Chat chat;
        try {
            chat = data.getSender().execute(new GetChat("@" + (alarmSendingForm != null ? alarmSendingForm.receiverTag : ""))); // Добавляем @ если его нет
        } catch (Exception e) {
            // по-хорошему должна быть обработка
            return;
        }
        String text = String.format("Пишу тебе, потому что был установлен таймер на это время. Если он не был мной выключен, " +
                        "значит, что-то случилось. Вот информация о моем местонахождении:\n%s",
                (alarmSendingForm != null ? alarmSendingForm.address : ""));
        new SendMessage(chat != null ? chat.getId().toString() : "", text);
    }
}
