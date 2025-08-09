package ru.mephi.telegrambotdating_java.model.handler.button;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.handler.AbstractButtonInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

@Service
public class StartButton extends AbstractButtonInput {
    {
        super.title = "/start";
    }

    @Override
    public SendMessage handle(SpareMessageData data) {

        return new SendMessage(data.getChatId(), "Привет! Я помогу обеспечить твою безопасность.\n" +
                "Для лучшего понимания работы бота нажмите на кнопку *Информация*\n" +
                "Следующим сообщением введите временный код доступа, который Вы сгенерировали в приложении и " +
                "фамилию и имя в следующей строке. К примеру:\n" +
                "5b310ba4-23e0-488f-8293-93ec696bd1d9\n" + "Ваня Пупкин");
    }
}