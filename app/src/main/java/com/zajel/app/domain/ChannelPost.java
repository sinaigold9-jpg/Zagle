package com.zajel.app.domain;

public final class ChannelPost {
    public final String id, channelId, authorId, body, createdAt;
    public ChannelPost(String id, String channelId, String authorId, String body, String createdAt) {
        this.id=id; this.channelId=channelId; this.authorId=authorId; this.body=body; this.createdAt=createdAt;
    }
}
