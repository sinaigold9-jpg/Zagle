package com.zajel.app.domain;

public final class PrivacySettings {
    public final boolean profileVisible;
    public final boolean lastSeenVisible;
    public final boolean readReceipts;
    public final boolean contactsInvites;
    public final boolean allowGroupInvites;

    public PrivacySettings(boolean profileVisible, boolean lastSeenVisible,
                          boolean readReceipts, boolean contactsInvites,
                          boolean allowGroupInvites) {
        this.profileVisible = profileVisible;
        this.lastSeenVisible = lastSeenVisible;
        this.readReceipts = readReceipts;
        this.contactsInvites = contactsInvites;
        this.allowGroupInvites = allowGroupInvites;
    }
}
