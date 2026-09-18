package com.zajel.app.domain;

public final class ExploreItem {
    public final String id;
    public final String type;
    public final String title;
    public final String description;
    public final String ownerId;
    public ExploreItem(String id, String type, String title, String description, String ownerId) {
        this.id=id; this.type=type; this.title=title; this.description=description; this.ownerId=ownerId;
    }
}
