package ru.mephi.telegrambotdating_java.model.handler.text_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.model.data.AlarmSendingForm;
import ru.mephi.telegrambotdating_java.model.data.response.AccessDeniedResponse;
import ru.mephi.telegrambotdating_java.model.data.response.IncorrectInputFormat;
import ru.mephi.telegrambotdating_java.model.data.response.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.model.handler.AbstractTextInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class AlarmSendingFormIncoming extends AbstractTextInput {
    @Autowired
    ActivityButtonChatRepository chatRepository;

    @Override
    public SendMessage handle(SpareMessageData data) {
        String chatId = data.getChatId();
        AlarmSendingForm parsedForm = tryParse(data.getText());

        if (parsedForm == null) {
            return new IncorrectInputFormat(chatId, "Неверный формат анкеты");
        }

        try {
            ActivityButtonChat form = chatRepository.getByChatId(chatId);
            if (form == null) {
                return new InternalErrorResponse(chatId, "Что-то пошло не так...");
            }

            // проверяем, доступна ли сейчас анкета (прошло как минимум 24 часа после последней активации)
            if (form.nextAvailableTime.isAfter(LocalDateTime.now())) {
                System.out.println("Заполнение анкеты недоступно.");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                return new AccessDeniedResponse(chatId, "Заполнение анкеты недоступно. Следующее доступное время: " +
                        form.nextAvailableTime.format(formatter));
            }
            System.out.println("Заполнение анкеты доступно.");
            chatRepository.updateAlarmSendingForm(
                chatId,
                form.receiverTag,
                Integer.parseInt(form.deactivationCode.toString()),
                form.address,
                form.datingTime
            );
            return new SendMessage(chatId, "Анкета успешно обновилась.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new InternalErrorResponse(data.getChatId(), "Internal server error");
        }
    }

    static private AlarmSendingForm tryParse(String text) {
        List<String> lines = Arrays.stream(text.split("\n"))
                .map(String::trim)
                .map(line -> line.equals("-") || line.isEmpty()
                        ? null
                        : line.startsWith("-")
                        ? line.substring(1).trim()
                        : line)
                .map(content -> content != null && content.isEmpty() ? null : content)
                .toList();

        if (lines.size() != 4) {
            return null;
        }

        AlarmSendingForm alarmSendingFormIncoming = new AlarmSendingForm(lines.get(0), lines.get(1), lines.get(2), lines.get(3));

        return alarmSendingFormIncoming.isDataCorrect() ? alarmSendingFormIncoming : null;
    }

    @Override
    public boolean isBelongToType(String text) {
        return text.split("\n").length == 4;
    }
}