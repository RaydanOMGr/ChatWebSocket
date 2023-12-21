package me.andreasmelone.chatsocket.models;

import java.util.UUID;

public class IDAssignModel extends BasicModel {
    public String assignedUUID;

    public IDAssignModel(UUID assignedUUID) {
        this.assignedUUID = assignedUUID.toString();
    }
}
