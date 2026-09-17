package com.zajel.app.domain;

public final class RoomMember {
    public final String id;
    public final String roomId;
    public final String userId;
    public final String role;

    public RoomMember(String id, String roomId, String userId, String role) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.role = role;
    }
}
