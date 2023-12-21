package me.andreasmelone.chatsocket;

import java.util.UUID;

public class User {
    public String name;
    public UUID sessionUUID;
    public int missingResponses = 0;

    public User(String name, UUID sessionUUID) {
        this.name = name;
        this.sessionUUID = sessionUUID;
    }
}
