package ru.mephi.telegrambotdating_java.model.service;

public interface AuthorizationScheduler {
    void deleteExpiredCodes();
}
