package com.zajel.app.domain;

public final class RoomMessage {
    public final String id;
    public final String roomId;
    public final String senderId;
    public final String body;
    public final String createdAt;

    public RoomMessage(String id, String roomId, String senderId, String body, String createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.body = body;
        this.createdAt = createdAt;
    }
}
