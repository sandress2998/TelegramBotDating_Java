package ru.mephi.telegrambotdating_java.model.data.text_message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.TelegramBotRepository;
import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AlarmSendingForm extends AbstractInput {
    final String receiverTag;
    final String deactivationCode;
    final String address;
    final String time;
    LocalDateTime datingTime;

    public AlarmSendingForm(String receiverTag, String deactivationCode, String address, String time) {
        this.receiverTag = receiverTag;
        this.deactivationCode = deactivationCode;
        this.address = address;
        this.time = time;
    }

    @Override
    public SendMessage handle(SpareMessageData data, TelegramBotRepository repository) {
        if (repository == null) {
            return new InternalErrorResponse(data.getChatId(), "Request is null");
        }
        try {
            repository.updateAlarmSendingForm(
                    UUID.fromString(data.getChatId()),
                    receiverTag,
                    deactivationCode != null ? Integer.parseInt(deactivationCode) : null,
                    address,
                    datingTime
            );
            return new SendMessage(data.getChatId(), "Анкета успешно обновилась.");
        } catch (Exception e) {
            return new InternalErrorResponse(data.getChatId(), "Internal server error");
        }
    }

    public boolean isDataCorrect() {
        return isDeactivationCodeCorrect() && isAddressCorrect() && isDatingTimeCorrect();
    }

    public boolean isDeactivationCodeCorrect() {
        try {
            return deactivationCode == null || (Integer.parseInt(deactivationCode) >= 1000 && Integer.parseInt(deactivationCode) <= 9999);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isAddressCorrect() {
        return address == null || address.length() < 200;
    }

    public boolean isDatingTimeCorrect() {
        String pattern = "dd.MM.yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            if (time == null) return true;
            datingTime = LocalDateTime.parse(time, formatter);
            return !datingTime.isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
}