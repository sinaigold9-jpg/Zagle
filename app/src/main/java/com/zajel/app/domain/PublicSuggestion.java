package com.zajel.app.domain;

public final class PublicSuggestion {
    public final String id, type, title, reason;
    public PublicSuggestion(String id, String type, String title, String reason) { this.id=id; this.type=type; this.title=title; this.reason=reason; }
}
