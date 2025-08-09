package ru.mephi.telegrambotdating_java.model.data.text_message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mephi.telegrambotdating_java.database.entity.ActivityButtonChat;
import ru.mephi.telegrambotdating_java.database.repository.ActivityButtonChatRepository;

import ru.mephi.telegrambotdating_java.model.data.AbstractInput;
import ru.mephi.telegrambotdating_java.model.data.SpareMessageData;
import ru.mephi.telegrambotdating_java.model.data.bad_request.InternalErrorResponse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuthorizationDataIncoming extends AbstractInput {

    public UUID code;
    public String name;
    public UUID clientId;

    public AuthorizationDataIncoming(UUID code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Эта функция вызывается уже после того, как были верифицированы данные
     */
    @Override
    public SendMessage handle(SpareMessageData data, ActivityButtonChatRepository repository) {
        if (name == null || clientId == null)
            return new InternalErrorResponse(data.getChatId(), "Something went wrong");

        // проверяем, есть ли пользователь с уже существующим chatId/clientId
        ActivityButtonChat existing = repository.getByChatId(data.getChatId());
        if (existing == null) {
            existing = repository.getByClientId(clientId);
        }

        if (existing != null) {
            System.out.println("Уже существует запись");
            // Обновляем существующую запись
            existing.clientId = clientId;
            existing.name = name;
        } else {
            // Создаём новую
            System.out.println("Создаем новую запись");
            existing = new ActivityButtonChat(data.getChatId(), clientId, name);
        }
        repository.save(existing);

        return new SendMessage(data.getChatId(), "Вы успешно авторизовались!");
    }

    static public AbstractInput tryParse(String text) {
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

            return new AuthorizationDataIncoming(code, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
