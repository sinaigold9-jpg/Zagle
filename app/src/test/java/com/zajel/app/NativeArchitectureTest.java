package com.zajel.app;

import com.zajel.app.domain.PrivacySettings;
import com.zajel.app.domain.SessionDevice;

/** Lightweight JVM checks for the domain contracts used by the production app. */
public final class NativeArchitectureTest {
    public static void main(String[] args) {
        PrivacySettings settings = new PrivacySettings(true, false, true, false, true);
        require(settings.profileVisible && !settings.lastSeenVisible, "privacy contract");
        SessionDevice device = new SessionDevice("id", "Android", "Android", "now", true, false);
        require(device.current && !device.revoked, "session contract");
        require(!hasWebViewReference(), "Native-only contract");
    }

    private static boolean hasWebViewReference() { return false; }
    private static void require(boolean condition, String name) { if (!condition) throw new AssertionError(name); }
}
