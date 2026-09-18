package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.ExploreItem;
import com.zajel.app.domain.PublicAd;
import com.zajel.app.domain.PublicSuggestion;
import com.zajel.app.domain.UserSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/** Public-only data boundary for Explore. Private chats and messages are never queried here. */
public final class SupabaseExploreRepository implements ExploreRepository {
    private final SupabaseClient api;
    private final SecureSessionStore sessions;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SupabaseExploreRepository(SupabaseClient api, SecureSessionStore sessions) {
        this.api = api;
        this.sessions = sessions;
    }

    @Override
    public void search(String query, Callback<List<ExploreItem>> callback) {
        executor.execute(() -> {
            try {
                String normalized = query == null ? "" : query.trim();
                if (normalized.isEmpty()) {
                    callback.success(new ArrayList<>());
                    return;
                }
                UserSession user = required();
                String term = URLEncoder.encode(normalized, StandardCharsets.UTF_8.name());
                List<ExploreItem> result = new ArrayList<>();
                addGroups(result, api.request("GET", "/rest/v1/groups?is_private=eq.false&or=(title.ilike.*" + term + "*,description.ilike.*" + term + "*)&select=id,title,description,owner_id&limit=25", user.accessToken, null));
                addRooms(result, api.request("GET", "/rest/v1/rooms?is_private=eq.false&or=(title.ilike.*" + term + "*,description.ilike.*" + term + "*)&select=id,title,description,owner_id&limit=25", user.accessToken, null));
                addProfiles(result, api.request("GET", "/rest/v1/profiles?is_discoverable=eq.true&or=(username.ilike.*" + term + "*,display_name.ilike.*" + term + "*)&select=id,username,display_name&limit=25", user.accessToken, null));
                callback.success(result);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void suggestions(Callback<List<PublicSuggestion>> callback) {
        executor.execute(() -> {
            try {
                UserSession user = required();
                JSONArray rows = rows(api.request("GET", "/rest/v1/explore_suggestions?enabled=eq.true&select=id,type,title,reason&order=rank.asc&limit=20", user.accessToken, null));
                List<PublicSuggestion> result = new ArrayList<>();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject row = rows.optJSONObject(i);
                    if (row != null) result.add(new PublicSuggestion(row.optString("id"), row.optString("type"), row.optString("title"), row.optString("reason")));
                }
                callback.success(result);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void publicAds(Callback<List<PublicAd>> callback) {
        executor.execute(() -> {
            try {
                UserSession user = required();
                JSONArray rows = rows(api.request("GET", "/rest/v1/public_ads?enabled=eq.true&select=id,title,body,target_url&order=priority.desc&limit=5", user.accessToken, null));
                List<PublicAd> result = new ArrayList<>();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject row = rows.optJSONObject(i);
                    if (row != null) result.add(new PublicAd(row.optString("id"), row.optString("title"), row.optString("body"), row.optString("target_url")));
                }
                callback.success(result);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    private void addGroups(List<ExploreItem> out, JSONObject response) { addPublicRows(out, response, "group", "title", "description"); }
    private void addRooms(List<ExploreItem> out, JSONObject response) { addPublicRows(out, response, "room", "title", "description"); }
    private void addProfiles(List<ExploreItem> out, JSONObject response) { addPublicRows(out, response, "user", "display_name", "username"); }

    private void addPublicRows(List<ExploreItem> out, JSONObject response, String type, String titleKey, String descriptionKey) {
        JSONArray rows = rows(response);
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.optJSONObject(i);
            if (row != null) out.add(new ExploreItem(row.optString("id"), type, row.optString(titleKey), row.optString(descriptionKey), row.optString("owner_id")));
        }
    }

    private JSONArray rows(JSONObject response) {
        JSONArray wrapped = response == null ? null : response.optJSONArray("rows");
        if (wrapped != null) return wrapped;
        return response == null ? new JSONArray() : response.optJSONArray("data") == null ? new JSONArray() : response.optJSONArray("data");
    }

    private UserSession required() {
        UserSession user = sessions.read();
        if (user == null) throw new IllegalStateException("Not signed in");
        return user;
    }
}
