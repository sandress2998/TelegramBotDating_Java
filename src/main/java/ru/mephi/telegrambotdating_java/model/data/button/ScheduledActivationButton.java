package ru.mephi.telegrambotdating_java.model.data.button;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.TelegramBotRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;

public class ScheduledActivationButton extends AbstractInput {

    @Override
    public SendMessage handle(SpareMessageData data, TelegramBotRepository repository) {
        if (repository == null) {
            return new InternalErrorResponse(data.getChatId(), "Repository is null");
        }
        return new SendMessage(
                data.getChatId(),
                "Напишите время активизации в формате day.month.year hour:minute.\n" +
                        "К примеру, 15 августа 2025 года в 18:07 - это 15.08.2025 18:07"
        );
    }
}
