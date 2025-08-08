package ru.mephi.telegrambotdating_java.database.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class AlarmToSend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(name = "chat_id", unique = true, nullable = false)
    public String chatId;

    @Column(nullable = false)
    public int retries = 3;

    @Column(nullable = false)
    public String receiver;

    @Column(nullable = false)
    public String text;

    public AlarmToSend(String chatId, String receiver, String text) {
        this.chatId = chatId;
        this.receiver = receiver;
        this.text = text;
    }

    public AlarmToSend() {}
}
