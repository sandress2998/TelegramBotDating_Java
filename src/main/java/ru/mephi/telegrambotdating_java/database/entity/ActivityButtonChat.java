package ru.mephi.telegrambotdating_java.database.entity;

import jakarta.persistence.*;
import ru.mephi.telegrambotdating_java.model.data.text_message.AlarmSendingForm;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "activity_button_chat")
public class ActivityButtonChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(name = "chat_id", unique = true)
    public UUID chatId;

    @Column(name = "client_id", unique = true)
    public UUID clientId;

    @Column
    public String name;

    @Column(name = "next_available", nullable = false)
    public LocalDateTime nextAvailableTime = LocalDateTime.now();

    @Column(name = "activation_time", nullable = true)
    public LocalDateTime activationTime = null;

    @Column(name = "receiver_tag", nullable = true)
    public String receiverTag = null;

    @Column(name = "deactivation_code", nullable = true)
    public Integer deactivationCode = null;

    @Column(name = "address", nullable = true)
    public String address = null;

    @Column(name = "dating_time", nullable = true)
    public LocalDateTime datingTime = null;

    @Column(name = "is_countdown_active", nullable = false)
    public boolean isCountdownActive = false;

    public ActivityButtonChat() {}

    public ActivityButtonChat(UUID chatId, UUID clientId, String name) {
        this.chatId = chatId;
        this.clientId = clientId;
    }

    public AlarmSendingForm convertToAlarmSendingForm() {
        return new AlarmSendingForm(receiverTag, deactivationCode != null ? deactivationCode.toString() : null, address, datingTime != null ? datingTime.toString() : null);
    }

    public AlarmToSend convertToAlarmToSend() {
        String data = "Адрес - " + address + "; Время встречи - " + datingTime;
        return new AlarmToSend(
                "@" + receiverTag,
                "Привет! Это " + name + ". Пишу, потому что пошла на свидание и поставила таймер.\n" +
                        "Если ты читаешь это, значит, я его не выключила и возможно что-то случилось.\n" +
                        "Вот данные о моем местоположении: " + data
        );
    }
}