package com.zajel.app.domain;

public final class Community {
    public final String id, title, description, ownerId;
    public Community(String id, String title, String description, String ownerId) {
        this.id=id; this.title=title; this.description=description; this.ownerId=ownerId;
    }
}
