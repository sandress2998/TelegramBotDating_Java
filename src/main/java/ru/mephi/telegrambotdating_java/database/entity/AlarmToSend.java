package ru.mephi.telegrambotdating_java.database.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class AlarmToSend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(nullable = false)
    public int retries = 3;

    @Column(nullable = false)
    public String receiver;

    @Column(nullable = false)
    public String text;

    public AlarmToSend(String receiver, String text) {
        this.receiver = receiver;
        this.text = text;
    }

    public AlarmToSend() {}
}
