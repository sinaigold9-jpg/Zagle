package com.zajel.app.data;

import com.zajel.app.core.AppExecutors;
import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.PrivacySettings;
import com.zajel.app.domain.SessionDevice;
import com.zajel.app.domain.UserSession;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseSecurityRepository implements SecurityRepository {
    private final SupabaseClient api;
    private final SecureSessionStore sessions;

    public SupabaseSecurityRepository(SupabaseClient api, SecureSessionStore sessions) {
        this.api = api;
        this.sessions = sessions;
    }

    @Override
    public void privacySettings(final Callback<PrivacySettings> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject response = api.request(
                        "GET",
                        "/rest/v1/privacy_settings?user_id=eq." + user.userId + "&select=user_id,profile_visible,last_seen_visible,read_receipts,contacts_invites,allow_group_invites",
                        user.accessToken,
                        null
                );
                JSONArray rows = response.optJSONArray("rows");
                if (rows == null || rows.length() == 0) {
                    callback.success(defaultPrivacySettings());
                    return;
                }
                JSONObject row = rows.optJSONObject(0);
                callback.success(new PrivacySettings(
                        row.optBoolean("profile_visible", true),
                        row.optBoolean("last_seen_visible", true),
                        row.optBoolean("read_receipts", true),
                        row.optBoolean("contacts_invites", true),
                        row.optBoolean("allow_group_invites", true)
                ));
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void updatePrivacySettings(final PrivacySettings settings, final Callback<Void> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject()
                        .put("user_id", user.userId)
                        .put("profile_visible", settings.profileVisible)
                        .put("last_seen_visible", settings.lastSeenVisible)
                        .put("read_receipts", settings.readReceipts)
                        .put("contacts_invites", settings.contactsInvites)
                        .put("allow_group_invites", settings.allowGroupInvites);
                api.request("POST", "/rest/v1/privacy_settings?on_conflict=user_id", user.accessToken, payload);
                callback.success(null);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void sessions(final Callback<List<SessionDevice>> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject response = api.request(
                        "GET",
                        "/rest/v1/device_sessions?user_id=eq." + user.userId + "&select=id,device_name,platform,last_seen_at,is_current,revoked&order=last_seen_at.desc",
                        user.accessToken,
                        null
                );
                JSONArray rows = response.optJSONArray("rows");
                List<SessionDevice> out = new ArrayList<>();
                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);
                        if (row == null) continue;
                        out.add(new SessionDevice(
                                row.optString("id"),
                                row.optString("device_name"),
                                row.optString("platform"),
                                row.optString("last_seen_at"),
                                row.optBoolean("is_current", false),
                                row.optBoolean("revoked", false)
                        ));
                    }
                }
                callback.success(out);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void revokeSession(final String sessionId, final Callback<Void> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject().put("revoked", true).put("is_current", false);
                api.request(
                        "PATCH",
                        "/rest/v1/device_sessions?id=eq." + sessionId + "&user_id=eq." + user.userId,
                        user.accessToken,
                        payload
                );
                callback.success(null);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void currentSession(final Callback<SessionDevice> callback) {
        sessions(new Callback<List<SessionDevice>>() {
            @Override public void success(List<SessionDevice> value) {
                SessionDevice current = null;
                for (SessionDevice session : value) {
                    if (session.current && !session.revoked) {
                        current = session;
                        break;
                    }
                }
                callback.success(current);
            }

            @Override public void error(Exception error) {
                callback.error(error);
            }
        });
    }

    private UserSession required() {
        UserSession current = sessions.read();
        if (current == null) throw new IllegalStateException("Not signed in");
        return current;
    }

    private PrivacySettings defaultPrivacySettings() {
        return new PrivacySettings(true, true, true, true, true);
    }
}
