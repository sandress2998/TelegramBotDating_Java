package ru.mephi.telegrambotdating_java.model.data.button;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmergencyActivationButton extends AbstractInput {
    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        if (repository == null) {
            return new SendMessage(data.getChatId(), "Repository is null");
        }

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

        repository.updateCountdownStatus(UUID.fromString(chatId), true);
        startTimer(sender, secondsRemaining, data.getChatId(), sentMessage.getMessageId());
        repository.updateCountdownStatus(UUID.fromString(chatId), false);

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