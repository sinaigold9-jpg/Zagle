package com.zajel.app.domain;

public final class AppNotification {
    public final String id, type, title, body, conversationId, createdAt;
    public final boolean read;
    public AppNotification(String id, String type, String title, String body, String conversationId, String createdAt, boolean read) {
        this.id = id; this.type = type; this.title = title; this.body = body; this.conversationId = conversationId; this.createdAt = createdAt; this.read = read;
    }
}
