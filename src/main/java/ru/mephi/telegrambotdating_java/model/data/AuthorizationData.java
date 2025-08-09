package ru.mephi.telegrambotdating_java.model.data;

import java.util.UUID;

public class AuthorizationData {
    private UUID code;
    private String name;
    private UUID clientId;

    public AuthorizationData(UUID code, String name) {
        this.code = code;
        this.name = name;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getCode() {
        return code;
    }
}
