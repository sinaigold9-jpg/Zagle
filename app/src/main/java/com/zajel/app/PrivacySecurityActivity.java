package com.zajel.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import com.zajel.app.core.AppContainer;
import com.zajel.app.data.SecurityRepository;
import com.zajel.app.domain.PrivacySettings;
import com.zajel.app.domain.SessionDevice;
import java.util.List;

/** Native Privacy Center and Security Center. */
public final class PrivacySecurityActivity extends Activity {
    private AppContainer container;
    private LinearLayout root;
    private Switch profileVisible, lastSeenVisible, readReceipts, contactsInvites, allowGroupInvites;
    private TextView status;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        container = ((ZajelApplication) getApplication()).container();
        render();
        loadPrivacy();
        loadSessions();
    }

    private void render() {
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 48, 28, 28);
        root.setBackgroundColor(Color.WHITE);
        scroll.addView(root);
        setContentView(scroll);

        TextView title = text("Privacy & Security", 28);
        title.setGravity(Gravity.RIGHT);
        root.addView(title);
        profileVisible = toggle("Profile visible in discovery");
        lastSeenVisible = toggle("Show last seen");
        readReceipts = toggle("Read receipts");
        contactsInvites = toggle("Allow contact invitations");
        allowGroupInvites = toggle("Allow group invitations");
        root.addView(profileVisible); root.addView(lastSeenVisible); root.addView(readReceipts);
        root.addView(contactsInvites); root.addView(allowGroupInvites);
        Button save = new Button(this);
        save.setText("Save privacy settings");
        save.setOnClickListener(v -> savePrivacy());
        root.addView(save);
        status = text("Loading…", 15);
        root.addView(status);
    }

    private void loadPrivacy() {
        container.security.privacySettings(new SecurityRepository.Callback<PrivacySettings>() {
            @Override public void success(PrivacySettings value) {
                runOnUiThread(() -> {
                    profileVisible.setChecked(value.profileVisible);
                    lastSeenVisible.setChecked(value.lastSeenVisible);
                    readReceipts.setChecked(value.readReceipts);
                    contactsInvites.setChecked(value.contactsInvites);
                    allowGroupInvites.setChecked(value.allowGroupInvites);
                    status.setText("Privacy settings loaded");
                });
            }
            @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Unable to load privacy settings")); }
        });
    }

    private void savePrivacy() {
        status.setText("Saving settings…");
        PrivacySettings value = new PrivacySettings(profileVisible.isChecked(), lastSeenVisible.isChecked(),
                readReceipts.isChecked(), contactsInvites.isChecked(), allowGroupInvites.isChecked());
        container.security.updatePrivacySettings(value, new SecurityRepository.Callback<Void>() {
            @Override public void success(Void ignored) { runOnUiThread(() -> status.setText("Privacy settings saved")); }
            @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Could not save privacy settings")); }
        });
    }

    private void loadSessions() {
        container.security.sessions(new SecurityRepository.Callback<List<SessionDevice>>() {
            @Override public void success(List<SessionDevice> values) {
                runOnUiThread(() -> {
                    TextView heading = text("Connected devices", 19);
                    heading.setPadding(0, 24, 0, 8);
                    root.addView(heading);
                    if (values == null || values.isEmpty()) { root.addView(text("No active sessions", 15)); return; }
                    for (SessionDevice value : values) {
                        TextView row = text((value.current ? "Current • " : "") + value.deviceName + " • " + value.platform
                                + "\nLast seen: " + value.lastSeenAt, 15);
                        row.setPadding(0, 8, 0, 8); root.addView(row);
                        if (!value.current && !value.revoked) {
                            Button revoke = new Button(PrivacySecurityActivity.this);
                            revoke.setText("Revoke");
                            revoke.setOnClickListener(v -> revoke(value.id));
                            root.addView(revoke);
                        }
                    }
                });
            }
            @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Sessions unavailable")); }
        });
    }

    private void revoke(String sessionId) {
        container.security.revokeSession(sessionId, new SecurityRepository.Callback<Void>() {
            @Override public void success(Void ignored) { runOnUiThread(() -> status.setText("Device revoked")); loadSessions(); }
            @Override public void error(Exception error) { runOnUiThread(() -> status.setText("Could not revoke device")); }
        });
    }

    private Switch toggle(String label) { Switch value = new Switch(this); value.setText(label); value.setTextSize(16); value.setPadding(0, 10, 0, 10); return value; }
    private TextView text(String value, int size) { TextView view = new TextView(this); view.setText(value); view.setTextSize(size); view.setTextColor(Color.rgb(24, 45, 61)); view.setGravity(Gravity.RIGHT); return view; }
}
