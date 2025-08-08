package ru.mephi.telegrambotdating_java.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.repository.TelegramBotRepository;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InvalidDataInput;
import ru.mephi.telegrambotdating_java.model.data.bad_request.UnknownInput;
import ru.mephi.telegrambotdating_java.model.data.button.*;
import ru.mephi.telegrambotdating_java.model.data.text_message.DeactivationCodeIncoming;
import ru.mephi.telegrambotdating_java.model.service.TelegramBotService;

import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.text_message.ActivationTimeIncoming;
import ru.mephi.telegrambotdating_java.model.data.text_message.AlarmSendingForm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class TelegramBotServiceImpl implements TelegramBotService {
    @Autowired
    private TelegramBotRepository repository;

    @Transactional
    @Override
    public SendMessage handleIncomingInfo(SpareMessageData data) {
        AbstractInput text = null;
        AbstractInput button;
        switch (data.getText()) {
            case "/start":
                button = new StartButton();
                break;
            case "Анкета":
                button = new FormFillingButton();
                break;
            case "Информация":
                button = new InformationButton();
                break;
            case "Экстренная активация":
                button = new EmergencyActivationButton();
                break;
            case "Настроить время активации":
                button = new ScheduledActivationButton();
                break;
            default:
                text = trySpecialParse(data);
                button = new UnknownInput();
                break;
        }
        return text != null ? text.handle(data, repository) : button.handle(data, repository);
    }

    private AbstractInput trySpecialParse(SpareMessageData data) {
        AbstractInput input = tryParseToAlarmSendingForm(data);
        if (input != null) {
            return input;
        }

        input = tryParseToActivationTime(data);
        if (input != null) {
            return input;
        }

        input = tryParseToDeactivationCode(data);
        if (input != null) {
            return input;
        }

        return new UnknownInput();
    }

    private AbstractInput tryParseToAlarmSendingForm(SpareMessageData data) {
        List<String> lines = Arrays.stream(data.getText().split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
        if (lines.size() != 4) {
            return null;
        }
        AlarmSendingForm alarmSendingForm = new AlarmSendingForm(lines.get(0), lines.get(1), lines.get(2), lines.get(3));
        return alarmSendingForm.isDataCorrect() ? alarmSendingForm : null;
    }

    private AbstractInput tryParseToActivationTime(SpareMessageData data) {
        String pattern = "dd.MM.yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDateTime activationTime = LocalDateTime.parse(data.getText(), formatter);
            if (activationTime.isBefore(LocalDateTime.now())) {
                return new InvalidDataInput("Некорректный ввод: введеное время уже прошло");
            } else {
                return new ActivationTimeIncoming(activationTime);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private AbstractInput tryParseToDeactivationCode(SpareMessageData data) {
        try {
            int code = Integer.parseInt(data.getText());
            if (code >= 1000 && code <= 9999) {
                return new DeactivationCodeIncoming(code);
            }
            return new InvalidDataInput("Нужно ввести четырехзначное число");
        } catch (NumberFormatException e) {
            return null;
        }
    }
}