package ru.mephi.telegrambotdating_java.model.data.button;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

public class InformationButton extends AbstractInput {

    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        return new SendMessage(data.getChatId(), "Здесь должна быть какая-то информация про приложение.\n" +
            "Но я так устала писать код, что писать еще и тексты у меня не осталось сил");
    }
}