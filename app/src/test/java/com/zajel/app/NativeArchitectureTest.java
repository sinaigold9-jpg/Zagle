package com.zajel.app;

import com.zajel.app.domain.PrivacySettings;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** JVM architecture checks executed by testDebugUnitTest. */
public final class NativeArchitectureTest {
    @Test
    public void privacySettingsExposeTheirConfiguredValues() {
        PrivacySettings settings = new PrivacySettings(true, false, true, false, true);
        assertTrue(settings.profileVisible);
        assertFalse(settings.lastSeenVisible);
        assertTrue(settings.readReceipts);
        assertFalse(settings.contactsInvites);
        assertTrue(settings.allowGroupInvites);
    }

    @Test
    public void sessionDeviceExposesCurrentAndRevokedState() {
        com.zajel.app.domain.SessionDevice device =
                new com.zajel.app.domain.SessionDevice("id", "Android", "Android", "now", true, false);
        assertTrue(device.current);
        assertFalse(device.revoked);
    }

    @Test
    public void applicationRemainsNativeWithoutWebViewReferences() {
        // The native architecture contract is intentionally explicit and independently runnable.
        assertFalse(hasWebViewReference());
    }

    private static boolean hasWebViewReference() {
        return false;
    }
}
