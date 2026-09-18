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

public final class PrivacySecurityActivity extends Activity {
    private AppContainer container;
    private LinearLayout root;
    private Switch profileVisible;
    private Switch lastSeenVisible;
    private Switch readReceipts;
    private Switch contactsInvites;
    private Switch allowGroupInvites;
    private TextView status;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        container = ((ZajelApplication) getApplication()).container();
        render();
        loadPrivacySettings();
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

        TextView title = text("Privacy & Security", 28, Color.rgb(24, 45, 61));
        title.setGravity(Gravity.RIGHT);
        root.addView(title);

        profileVisible = toggle("Profile visible in discovery");
        lastSeenVisible = toggle("Show last seen");
        readReceipts = toggle("Read receipts");
        contactsInvites = toggle("Invite from contacts");
        allowGroupInvites = toggle("Accept group invites");

        root.addView(profileVisible);
        root.addView(lastSeenVisible);
        root.addView(readReceipts);
        root.addView(contactsInvites);
        root.addView(allowGroupInvites);

        Button save = new Button(this);
        save.setText("Save privacy settings");
        save.setOnClickListener(v -> savePrivacySettings());
        root.addView(save);

        status = text("Loading…", 15, Color.rgb(55, 70, 85));
        status.setPadding(0, 16, 0, 8);
        root.addView(status);
    }

    private void loadPrivacySettings() {
        container.security.privacySettings(new SecurityRepository.Callback<PrivacySettings>() {
            @Override public void success(PrivacySettings settings) {
                runOnUiThread(() -> {
                    profileVisible.setChecked(settings.profileVisible);
                    lastSeenVisible.setChecked(settings.lastSeenVisible);
                    readReceipts.setChecked(settings.readReceipts);
                    contactsInvites.setChecked(settings.contactsInvites);
                    allowGroupInvites.setChecked(settings.allowGroupInvites);
                    status.setText("Privacy settings loaded");
                });
            }

            @Override public void error(Exception error) {
                runOnUiThread(() -> status.setText("Unable to load privacy settings"));
            }
        });
    }

    private void savePrivacySettings() {
        PrivacySettings next = new PrivacySettings(
                profileVisible.isChecked(),
                lastSeenVisible.isChecked(),
                readReceipts.isChecked(),
                contactsInvites.isChecked(),
                allowGroupInvites.isChecked()
        );
        status.setText("Saving settings…");
        container.security.updatePrivacySettings(next, new SecurityRepository.Callback<Void>() {
            @Override public void success(Void value) {
                runOnUiThread(() -> status.setText("Privacy settings saved"));
            }

            @Override public void error(Exception error) {
                runOnUiThread(() -> status.setText("Could not save privacy settings"));
            }
        });
    }

    private void loadSessions() {
        container.security.sessions(new SecurityRepository.Callback<List<SessionDevice>>() {
            @Override public void success(List<SessionDevice> value) {
                runOnUiThread(() -> {
                    TextView label = text("Connected devices", 18, Color.rgb(24, 45, 61));
                    label.setPadding(0, 24, 0, 8);
                    root.addView(label);
                    if (value == null || value.isEmpty()) {
                        root.addView(text("No active sessions", 15, Color.rgb(82, 97, 111)));
                        return;
                    }
                    for (final SessionDevice item : value) {
                        TextView row = text(
                                (item.current ? "Current • " : "") + item.deviceName + " • " + item.platform + "\nLast seen: " + item.lastSeenAt,
                                15,
                                Color.rgb(41, 58, 74)
                        );
                        row.setPadding(0, 8, 0, 8);
                        root.addView(row);
                        if (!item.revoked && !item.current) {
                            Button revoke = new Button(PrivacySecurityActivity.this);
                            revoke.setText("Revoke");
                            revoke.setOnClickListener(v -> revokeSession(item.id));
                            root.addView(revoke);
                        }
                    }
                });
            }

            @Override public void error(Exception error) {
                runOnUiThread(() -> status.setText("Sessions unavailable"));
            }
        });
    }

    private void revokeSession(final String sessionId) {
        container.security.revokeSession(sessionId, new SecurityRepository.Callback<Void>() {
            @Override public void success(Void value) {
                runOnUiThread(() -> status.setText("Device revoked"));
                loadSessions();
            }

            @Override public void error(Exception error) {
                runOnUiThread(() -> status.setText("Could not revoke session"));
            }
        });
    }

    private Switch toggle(String label) {
        Switch toggle = new Switch(this);
        toggle.setText(label);
        toggle.setTextSize(16f);
        toggle.setPadding(0, 10, 0, 10);
        return toggle;
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.RIGHT);
        return view;
    }
}
