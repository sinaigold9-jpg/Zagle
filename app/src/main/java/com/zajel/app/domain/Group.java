package com.zajel.app.domain;

public final class Group {
    public final String id;
    public final String title;
    public final String description;
    public final String avatarUrl;
    public final String ownerId;
    public final boolean isPrivate;

    public Group(String id, String title, String description, String avatarUrl, String ownerId, boolean isPrivate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
    }
}
