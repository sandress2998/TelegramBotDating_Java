package ru.mephi.telegrambotdating_java.model.handler.button;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.handler.AbstractButtonInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

@Service
public class ScheduledActivationButton extends AbstractButtonInput {
    {
        super.title = "Настроить время активации";
    }

    @Override
    public SendMessage handle(SpareMessageData data) {
        return new SendMessage(
                data.getChatId(),
                "Напишите время активизации в формате day.month.year hour:minute.\n" +
                        "К примеру, 15 августа 2025 года в 18:07 - это 15.08.2025 18:07"
        );
    }
}
