package ru.mephi.telegrambotdating_java.model.handler.text_message;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;
import ru.mephi.telegrambotdating_java.database.entity.AuthorizationCode;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;
import ru.mephi.telegrambotdating_java.database.repository.AuthorizationCodeRepository;
import ru.mephi.telegrambotdating_java.model.data.AuthorizationData;
import ru.mephi.telegrambotdating_java.model.data.response.AccessDeniedResponse;
import ru.mephi.telegrambotdating_java.model.data.response.IncorrectInputFormat;
import ru.mephi.telegrambotdating_java.model.data.response.InternalErrorResponse;
import ru.mephi.telegrambotdating_java.model.handler.AbstractTextInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AuthorizationDataIncoming extends AbstractTextInput {
    @Autowired
    ActivityButtonChatRepository chatRepository;

    @Autowired
    AuthorizationCodeRepository authRepository;

    @Override
    @Transactional
    public SendMessage handle(SpareMessageData data) {
        String chatId = data.getChatId();
        AuthorizationData authData = tryParse(data.getText());

        if (authData == null) {
            return new IncorrectInputFormat(chatId, "Введены некорректные данные для авторизации");
        }

        if (authorize(authData)) {
            if (authData.getClientId() != null) {
                saveOrRefreshChat(chatId, authData.getClientId(), authData.getName());

                SendMessage message = new SendMessage(data.getChatId(), "Вы успешно авторизовались!");
                setReplyKeyboard(message);

                return message;
            } else {
                return new InternalErrorResponse(data.getChatId(), "Что-то пошло не так...");

            }
        } else {
            return new AccessDeniedResponse(data.getChatId(), "Данные авторизации введены неверно");
        }
    }

    @Override
    public boolean isBelongToType(String text) {
        return text.split("\n").length == 2;
    }

    private void saveOrRefreshChat(String chatId, UUID clientId, String name) {
        // проверяем, есть ли пользователь с уже существующим chatId/clientId
        ActivityButtonChat existing = chatRepository.getByChatId(chatId);
        if (existing == null) {
            existing = chatRepository.getByClientId(clientId);
        }

        if (existing != null) {
            // Обновляем существующую запись
            existing.clientId = clientId;
            existing.name = name;
        } else {
            // Создаём новую запись
            existing = new ActivityButtonChat(chatId, clientId, name);
        }
        chatRepository.save(existing);
    }

    // Возвращает true, если данные для авторизация верны, false - в другой случае
    private boolean authorize(AuthorizationData data) {
        UUID code = data.getCode();

        // authData будет не null только в том случае, если code не просрочен и совпадает
        AuthorizationCode authData = authRepository.getByCodeAndExpiresAtAfter(code, LocalDateTime.now());
        if (authData != null) {
            authRepository.deleteByCode(code);
            data.setClientId(authData.clientId);
            return true;
        }
        return false;
    }

    static private AuthorizationData tryParse(String text) {
        try {
            List<String> lines = Arrays.stream(text.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
            if (lines.size() != 2) {
                return null;
            }

            UUID code = UUID.fromString(lines.get(0));
            String name = lines.get(1);

            if (name.isEmpty()) return null;

            return new AuthorizationData(code, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void setReplyKeyboard(SendMessage message) {
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
    }
}
