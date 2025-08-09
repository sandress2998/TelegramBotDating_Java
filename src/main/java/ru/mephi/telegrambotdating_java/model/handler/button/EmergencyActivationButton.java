package ru.mephi.telegrambotdating_java.model.handler.button;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.mephi.telegrambotdating_java.model.handler.AbstractButtonInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.response.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EmergencyActivationButton extends AbstractButtonInput {
    @Autowired
    ActivityButtonChatRepository chatRepository;

    {
        super.title = "Экстренная активация";
    }

    @Override
    public SendMessage handle(SpareMessageData data) {
        String chatId = data.getChatId();
        SendMessage initialMessage = new SendMessage(chatId, "Вы нажали на кнопку активации. Если хотите отменить это действие, введите написанный ранее код деактивации.");

        AbsSender sender = data.getSender();
        if (sender == null) {
            throw new IllegalStateException("Отправитель не найден");
        }

        Message sentMessage;

        try {
            sentMessage = sender.execute(initialMessage);
        } catch (Exception e) {
            return new InternalErrorResponse(data.getChatId(), "Что-то пошло не так");
        }

        int secondsRemaining = 60;

        chatRepository.updateCountdownStatus(data.getChatId(), true);
        startTimer(sender, secondsRemaining, data.getChatId(), sentMessage.getMessageId());
        chatRepository.updateCountdownStatus(data.getChatId(), false);

        return initialMessage;
    }

    private void startTimer(AbsSender sender, int seconds, String chatId, int messageId) {
        final int[] secondsRemaining = {seconds};
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            try {
                if (secondsRemaining[0] > 0) {
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(chatId);
                    editMessageText.setMessageId(messageId);
                    editMessageText.setText("⏳ Осталось: " + secondsRemaining[0] + " сек.");
                    sender.execute(editMessageText);
                    secondsRemaining[0]--;
                } else {
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(chatId);
                    editMessageText.setMessageId(messageId);
                    editMessageText.setText("✅ Активация завершена!");
                    sender.execute(editMessageText);
                    executor.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                executor.shutdown();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}