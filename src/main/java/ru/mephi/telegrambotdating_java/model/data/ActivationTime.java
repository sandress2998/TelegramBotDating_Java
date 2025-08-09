package ru.mephi.telegrambotdating_java.model.data;

import java.time.LocalDateTime;

public class ActivationTime {
    private final LocalDateTime time;

    public ActivationTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
