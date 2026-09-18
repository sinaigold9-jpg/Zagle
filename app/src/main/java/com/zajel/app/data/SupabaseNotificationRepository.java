package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.AppNotification;
import com.zajel.app.domain.NotificationPreferences;
import com.zajel.app.domain.UserSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseNotificationRepository implements NotificationRepository {
    private final SupabaseClient api; private final SecureSessionStore sessions;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public SupabaseNotificationRepository(SupabaseClient api, SecureSessionStore sessions) { this.api = api; this.sessions = sessions; }
    @Override public void list(Callback<List<AppNotification>> callback) {
        executor.execute(() -> { try {
            UserSession user = required(); JSONObject response = api.request("GET", "/rest/v1/notifications?user_id=eq." + user.userId + "&select=id,type,title,body,conversation_id,created_at,read&order=created_at.desc&limit=50", user.accessToken, null);
            JSONArray rows = rows(response); List<AppNotification> result = new ArrayList<>();
            for (int i = 0; i < rows.length(); i++) { JSONObject x = rows.optJSONObject(i); if (x != null) result.add(new AppNotification(x.optString("id"), x.optString("type"), x.optString("title"), x.optString("body"), x.optString("conversation_id"), x.optString("created_at"), x.optBoolean("read"))); }
            callback.success(result);
        } catch (Exception error) { callback.error(error); } });
    }
    @Override public void markRead(String id, Callback<Void> callback) {
        executor.execute(() -> { try { UserSession user = required(); api.request("PATCH", "/rest/v1/notifications?id=eq." + id + "&user_id=eq." + user.userId, user.accessToken, new JSONObject().put("read", true)); callback.success(null); } catch (Exception error) { callback.error(error); } });
    }
    @Override public void preferences(Callback<NotificationPreferences> callback) {
        executor.execute(() -> { try { UserSession user = required(); JSONObject r = api.request("GET", "/rest/v1/notification_preferences?user_id=eq." + user.userId + "&select=enabled,messages,groups,rooms,announcements&limit=1", user.accessToken, null); JSONArray a = rows(r); JSONObject x = a.length() == 0 ? new JSONObject() : a.optJSONObject(0); callback.success(new NotificationPreferences(x.optBoolean("enabled", true), x.optBoolean("messages", true), x.optBoolean("groups", true), x.optBoolean("rooms", true), x.optBoolean("announcements", true))); } catch (Exception error) { callback.error(error); } });
    }
    @Override public void updatePreferences(NotificationPreferences p, Callback<Void> callback) {
        executor.execute(() -> { try { UserSession user = required(); JSONObject body = new JSONObject().put("user_id", user.userId).put("enabled", p.enabled).put("messages", p.messages).put("groups", p.groups).put("rooms", p.rooms).put("announcements", p.announcements); api.request("POST", "/rest/v1/notification_preferences?on_conflict=user_id", user.accessToken, body); callback.success(null); } catch (Exception error) { callback.error(error); } });
    }
    private UserSession required() { UserSession u = sessions.read(); if (u == null) throw new IllegalStateException("Not signed in"); return u; }
    private JSONArray rows(JSONObject r) { if (r == null) return new JSONArray(); JSONArray a = r.optJSONArray("rows"); if (a != null) return a; a = r.optJSONArray("data"); return a == null ? new JSONArray() : a; }
}
