package com.zajel.app.domain;

public final class SessionDevice {
    public final String id;
    public final String deviceName;
    public final String platform;
    public final String lastSeenAt;
    public final boolean current;
    public final boolean revoked;

    public SessionDevice(String id, String deviceName, String platform,
                         String lastSeenAt, boolean current, boolean revoked) {
        this.id = id;
        this.deviceName = deviceName;
        this.platform = platform;
        this.lastSeenAt = lastSeenAt;
        this.current = current;
        this.revoked = revoked;
    }
}
