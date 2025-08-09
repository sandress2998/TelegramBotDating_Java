package ru.mephi.telegrambotdating_java.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.model.data.response.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.model.handler.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.handler.UnknownInput;
import ru.mephi.telegrambotdating_java.model.handler.button.*;
import ru.mephi.telegrambotdating_java.model.handler.text_message.ActivationTimeIncoming;
import ru.mephi.telegrambotdating_java.model.handler.text_message.AlarmSendingFormIncoming;
import ru.mephi.telegrambotdating_java.model.handler.text_message.AuthorizationDataIncoming;
import ru.mephi.telegrambotdating_java.model.handler.text_message.DeactivationCodeIncoming;
import ru.mephi.telegrambotdating_java.model.service.TelegramBotService;

@Service
public class TelegramBotServiceImpl implements TelegramBotService {

    // обработчики для кнопок
    @Autowired
    EmergencyActivationButton emergencyActivationButton;
    @Autowired
    FormFillingButton formFillingButton;
    @Autowired
    InformationButton informationButton;
    @Autowired
    ScheduledActivationButton scheduledActivationButton;
    @Autowired
    StartButton sendButton;

    // обработчики для текста
    @Autowired
    ActivationTimeIncoming activationTimeIncoming;
    @Autowired
    AlarmSendingFormIncoming alarmSendingFormIncoming;
    @Autowired
    AuthorizationDataIncoming authorizationDataIncoming;
    @Autowired
    DeactivationCodeIncoming deactivationCodeIncoming;

    @Override
    public SendMessage handleIncomingInfo(SpareMessageData data) {
        /* Раньше я пыталась передавать репозитории в handle явно, но
         * Spring к этому, видимо, не приспособлен, поэтому я просто сделала
         *  обработчики сервисами. Богатая модель, видимо, не для Spring.
         */
        AbstractInput input = tryButtonParse(data.getText());
        if (input == null) {
            input = tryTextParse(data.getText());
        }

        try {
            return input.handle(data);
        } catch (Exception e) {
            return new InternalErrorResponse(data.getChatId(), "Что-то пошло не так...");
        }
    }

    private AbstractInput tryButtonParse(String text) {
        AbstractInput command = null;

        if (text.equals(emergencyActivationButton.getTitle())) command = emergencyActivationButton;
        else if (text.equals(formFillingButton.getTitle())) command = formFillingButton;
        else if (text.equals(informationButton.getTitle())) command = informationButton;
        else if (text.equals(scheduledActivationButton.getTitle())) command = scheduledActivationButton;
        else if (text.equals(sendButton.getTitle())) command = sendButton;

        return command;
    }

    private AbstractInput tryTextParse(String text) {
        AbstractInput command = null;
        if (activationTimeIncoming.isBelongToType(text)) command = activationTimeIncoming;
        else if (alarmSendingFormIncoming.isBelongToType(text)) command = alarmSendingFormIncoming;
        else if (authorizationDataIncoming.isBelongToType(text)) command = authorizationDataIncoming;
        else if (deactivationCodeIncoming.isBelongToType(text)) command = deactivationCodeIncoming;

        if (command == null) command = new UnknownInput();
        return command;
    }
}