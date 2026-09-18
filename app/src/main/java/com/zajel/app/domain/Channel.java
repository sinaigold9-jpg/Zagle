package com.zajel.app.domain;

public final class Channel {
    public final String id, title, description, ownerId, inviteToken;
    public final boolean isPrivate;
    public Channel(String id, String title, String description, String ownerId, boolean isPrivate, String inviteToken) {
        this.id=id; this.title=title; this.description=description; this.ownerId=ownerId; this.isPrivate=isPrivate; this.inviteToken=inviteToken;
    }
}
