package ru.mephi.telegrambotdating_java.model.data.button;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

import java.util.ArrayList;
import java.util.List;

public class StartButton extends AbstractInput {
    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        SendMessage message = new SendMessage(data.getChatId(), "Привет! Я помогу обеспечить твою безопасность.\n" +
                "Для лучшего понимания работы бота нажмите на кнопку *Информация*\n" +
                "Следующим сообщением введите временный код доступа, который Вы сгенерировали в приложении и " +
                "фамилию и имя в следующей строке. К примеру:\n" +
                "5b310ba4-23e0-488f-8293-93ec696bd1d9\n" + "Ваня Пупкин");

        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Экстренная активация");
        keyboard.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Настроить время активации");
        keyboard.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Анкета");
        row3.add("Информация");
        keyboard.add(row3);

        replyMarkup.setKeyboard(keyboard);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(false);
        message.setReplyMarkup(replyMarkup);

        return message;
    }
}