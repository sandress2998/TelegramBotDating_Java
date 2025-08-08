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
        return new SendMessage(data.getChatId(), String.format("""
                        Текущая анкета:
                        Код деактивации: XXXX\s
                        Адрес: %s
                        Время встречи: %s

                        Для редактирования анкеты введите данные в формате:\s
                        <код деактивации (4 цифры)> ENTER
                        <адрес (не более 200 символов)> ENTER
                        <время встречи (к примеру, 18.09.2025 23:09)>""",
                currentForm != null ? currentForm.address : "N/A",
                currentForm != null ? currentForm.datingTime : "N/A"));
    }
}