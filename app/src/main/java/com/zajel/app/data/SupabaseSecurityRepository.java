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

/** Supabase-backed privacy and device-session data source. */
public final class SupabaseSecurityRepository implements SecurityRepository {
    private final SupabaseClient api;
    private final SecureSessionStore sessionStore;

    public SupabaseSecurityRepository(SupabaseClient api, SecureSessionStore sessionStore) {
        this.api = api;
        this.sessionStore = sessionStore;
    }

    @Override public void privacySettings(Callback<PrivacySettings> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject response = api.request("GET", "/rest/v1/privacy_settings?user_id=eq." + user.userId
                        + "&select=profile_visible,last_seen_visible,read_receipts,contacts_invites,allow_group_invites",
                        user.accessToken, null);
                JSONArray rows = rows(response);
                if (rows.length() == 0) { callback.success(defaultSettings()); return; }
                JSONObject row = rows.optJSONObject(0);
                callback.success(new PrivacySettings(row.optBoolean("profile_visible", true),
                        row.optBoolean("last_seen_visible", true), row.optBoolean("read_receipts", true),
                        row.optBoolean("contacts_invites", true), row.optBoolean("allow_group_invites", true)));
            } catch (Exception error) { callback.error(error); }
        });
    }

    @Override public void updatePrivacySettings(PrivacySettings settings, Callback<Void> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject().put("user_id", user.userId)
                        .put("profile_visible", settings.profileVisible)
                        .put("last_seen_visible", settings.lastSeenVisible)
                        .put("read_receipts", settings.readReceipts)
                        .put("contacts_invites", settings.contactsInvites)
                        .put("allow_group_invites", settings.allowGroupInvites);
                api.request("POST", "/rest/v1/privacy_settings?on_conflict=user_id", user.accessToken, payload);
                callback.success(null);
            } catch (Exception error) { callback.error(error); }
        });
    }

    @Override public void sessions(Callback<List<SessionDevice>> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                JSONObject response = api.request("GET", "/rest/v1/device_sessions?user_id=eq." + user.userId
                        + "&select=id,device_name,platform,last_seen_at,is_current,revoked&order=last_seen_at.desc",
                        user.accessToken, null);
                JSONArray values = rows(response);
                List<SessionDevice> result = new ArrayList<>();
                for (int i = 0; i < values.length(); i++) {
                    JSONObject row = values.optJSONObject(i);
                    if (row != null) result.add(new SessionDevice(row.optString("id"), row.optString("device_name"),
                            row.optString("platform"), row.optString("last_seen_at"),
                            row.optBoolean("is_current"), row.optBoolean("revoked")));
                }
                callback.success(result);
            } catch (Exception error) { callback.error(error); }
        });
    }

    @Override public void revokeSession(String sessionId, Callback<Void> callback) {
        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();
                api.request("PATCH", "/rest/v1/device_sessions?id=eq." + sessionId + "&user_id=eq." + user.userId,
                        user.accessToken, new JSONObject().put("revoked", true).put("is_current", false));
                callback.success(null);
            } catch (Exception error) { callback.error(error); }
        });
    }

    @Override public void currentSession(Callback<SessionDevice> callback) {
        sessions(new Callback<List<SessionDevice>>() {
            @Override public void success(List<SessionDevice> values) {
                SessionDevice current = null;
                for (SessionDevice value : values) if (value.current && !value.revoked) { current = value; break; }
                callback.success(current);
            }
            @Override public void error(Exception error) { callback.error(error); }
        });
    }

    private UserSession required() {
        UserSession user = sessionStore.read();
        if (user == null) throw new IllegalStateException("Not signed in");
        return user;
    }

    private JSONArray rows(JSONObject response) {
        if (response == null) return new JSONArray();
        JSONArray result = response.optJSONArray("rows");
        if (result != null) return result;
        result = response.optJSONArray("data");
        return result == null ? new JSONArray() : result;
    }

    private PrivacySettings defaultSettings() { return new PrivacySettings(true, true, true, true, true); }
}
