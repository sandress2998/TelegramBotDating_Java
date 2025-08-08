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
    public UUID clientId = UUID.randomUUID(); // так не должно быть, но таблицы client у нас нет

    @Column(name = "next_available")
    public LocalDateTime nextAvailableTime = LocalDateTime.now();

    @Column(name = "activation_time", nullable = true)
    public LocalDateTime activationTime;

    @Column(name = "receiver_tag", nullable = true)
    public String receiverTag;

    @Column(name = "deactivation_code", nullable = true)
    public Integer deactivationCode;

    @Column(name = "address", nullable = true)
    public String address;

    @Column(name = "dating_time", nullable = true)
    public LocalDateTime datingTime;

    @Column(name = "is_countdown_active")
    public boolean isCountdownActive = false;

    public ActivityButtonChat(UUID chatId) {
        this.chatId = chatId;
    }

    public ActivityButtonChat() {}

    public AlarmSendingForm convertToAlarmSendingForm() {
        return new AlarmSendingForm(receiverTag, deactivationCode != null ? deactivationCode.toString() : null, address, datingTime != null ? datingTime.toString() : null);
    }
}