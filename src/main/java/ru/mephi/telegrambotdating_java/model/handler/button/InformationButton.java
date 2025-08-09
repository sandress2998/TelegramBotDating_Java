package ru.mephi.telegrambotdating_java.model.handler.button;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.handler.AbstractButtonInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

@Service
public class InformationButton extends AbstractButtonInput {
    {
        title = "Информация";
    }

    @Override
    public SendMessage handle(SpareMessageData data) {
        return new SendMessage(data.getChatId(), "Здесь должна быть какая-то информация про приложение.\n" +
            "Но я так устала писать код, что писать еще и тексты у меня не осталось сил");
    }
}