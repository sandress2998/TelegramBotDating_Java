package ru.mephi.telegrambotdating_java.model.data.button;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;

import java.util.UUID;

public class FormFillingButton extends AbstractInput {
    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        if (repository == null) {
            return new InternalErrorResponse(data.getChatId(), "Repository is null");
        }

        ActivityButtonChat currentForm = repository.getByChatId(data.getChatId());
        boolean isAllFieldFilled =  currentForm != null &&
                currentForm.receiverTag != null &&
                currentForm.deactivationCode != null &&
                currentForm.address != null &&
                currentForm.datingTime != null;

        return new SendMessage(data.getChatId(), String.format("""
                        Текущая анкета:
                        Получатель: %s
                        Код деактивации: %s
                        Адрес: %s
                        Время встречи: %s
                        
                        Время отправки: %s
                        
                        %s

                        Для редактирования анкеты введите данные в формате:\s
                        - <тэг получателя (без @)> ENTER
                        - <код деактивации (4 цифры)> ENTER
                        - <адрес (не более 200 символов)> ENTER
                        - <время встречи (к примеру, 18.09.2025 23:09)>""",
                currentForm != null ? currentForm.receiverTag : "Не указано",
                currentForm != null ? "XXXX" : "Не указано",
                currentForm != null ? currentForm.address : "Не указано",
                currentForm != null ? currentForm.datingTime : "Не указано",
                currentForm != null ? currentForm.activationTime : "Не указано",
                isAllFieldFilled ? "" : "Учтите, что анкета отправится только в том случае, когда все поля анкеты" +
                        "заполнены. Время активации указывать не обязательно."
                ));
    }
}