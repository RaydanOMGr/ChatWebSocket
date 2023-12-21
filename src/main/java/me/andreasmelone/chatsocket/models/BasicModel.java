package me.andreasmelone.chatsocket.models;

import java.util.UUID;

public class BasicModel {
    public String sessionUUID;
    public int id;

    public UUID getSessionUUID() throws IllegalArgumentException {
        return UUID.fromString(sessionUUID);
    }
}
