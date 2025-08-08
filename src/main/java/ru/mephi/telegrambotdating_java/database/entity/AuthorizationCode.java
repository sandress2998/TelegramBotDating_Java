package ru.mephi.telegrambotdating_java.database.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "authorization_code")
public class AuthorizationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(unique = true, nullable = false)
    public UUID code;

    @Column(name = "client_id", unique = true, nullable = false)
    public UUID clientId;

    @Column(name = "expires_at", nullable = false)
    public LocalDateTime expiresAt;
}
