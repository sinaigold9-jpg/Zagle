package com.zajel.app.domain;

public final class NotificationPreferences {
    public final boolean enabled, messages, groups, rooms, announcements;
    public NotificationPreferences(boolean enabled, boolean messages, boolean groups, boolean rooms, boolean announcements) {
        this.enabled = enabled; this.messages = messages; this.groups = groups; this.rooms = rooms; this.announcements = announcements;
    }
}
