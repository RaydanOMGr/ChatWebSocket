package me.andreasmelone.chatsocket.models;

import java.util.UUID;

public class MessageModel extends BasicModel {
    public String message;

    public MessageModel(UUID sessionUUID, String message) {
        this.sessionUUID = sessionUUID.toString();
        this.message = message;
    }
}
