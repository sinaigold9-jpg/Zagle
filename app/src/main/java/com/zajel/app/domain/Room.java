package com.zajel.app.domain;

public final class Room {
    public final String id;
    public final String title;
    public final String description;
    public final String ownerId;
    public final boolean isPrivate;

    public Room(String id, String title, String description, String ownerId, boolean isPrivate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
    }
}
