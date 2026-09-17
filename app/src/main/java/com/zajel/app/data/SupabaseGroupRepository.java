package com.zajel.app.data;

import android.content.Context;
import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.Group;
import com.zajel.app.domain.GroupMember;
import com.zajel.app.domain.UserSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseGroupRepository implements GroupRepository {
    private final SupabaseClient api;
    private final SecureSessionStore sessions;

    public SupabaseGroupRepository(SupabaseClient api, SecureSessionStore sessions) {
        this.api = api;
        this.sessions = sessions;
    }

    @Override
    public void myGroups(Callback<List<Group>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                String path = "/rest/v1/group_members?user_id=eq." + user.userId + "&select=group_id,groups(id,title,description,avatar_url,is_private,owner_id)";
                JSONObject response = api.request("GET", path, user.accessToken, null);
                JSONArray rows = response.optJSONArray("rows");
                List<Group> out = new ArrayList<>();
                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);
                        if (row == null) continue;
                        JSONObject groupObj = row.optJSONObject("groups");
                        if (groupObj == null) continue;
                        out.add(new Group(
                                groupObj.optString("id"),
                                groupObj.optString("title"),
                                groupObj.optString("description"),
                                groupObj.optString("avatar_url"),
                                groupObj.optString("owner_id"),
                                groupObj.optBoolean("is_private")
                        ));
                    }
                }
                cb.success(out);
            } catch (Exception e) {
                cb.error(e);
            }
        });
    }

    @Override
    public void createGroup(String title, String description, String avatarUrl, boolean isPrivate, Callback<Group> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject()
                        .put("title", title)
                        .put("description", description)
                        .put("avatar_url", avatarUrl)
                        .put("is_private", isPrivate)
                        .put("owner_id", user.userId);
                JSONObject response = api.request("POST", "/rest/v1/groups", user.accessToken, payload);
                JSONObject row = firstRow(response);
                if (row == null) throw new IllegalStateException("Group creation failed");
                String groupId = row.optString("id");
                JSONObject memberPayload = new JSONObject()
                        .put("group_id", groupId)
                        .put("user_id", user.userId)
                        .put("role", "owner");
                api.request("POST", "/rest/v1/group_members", user.accessToken, memberPayload);
                cb.success(new Group(
                        row.optString("id"),
                        row.optString("title"),
                        row.optString("description"),
                        row.optString("avatar_url"),
                        row.optString("owner_id"),
                        row.optBoolean("is_private")
                ));
            } catch (Exception e) {
                cb.error(e);
            }
        });
    }

    @Override
    public void members(String groupId, Callback<List<GroupMember>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                String path = "/rest/v1/group_members?group_id=eq." + groupId + "&select=id,group_id,user_id,role";
                JSONObject response = api.request("GET", path, user.accessToken, null);
                JSONArray rows = response.optJSONArray("rows");
                List<GroupMember> out = new ArrayList<>();
                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);
                        if (row == null) continue;
                        out.add(new GroupMember(
                                row.optString("id"),
                                row.optString("group_id"),
                                row.optString("user_id"),
                                row.optString("role")
                        ));
                    }
                }
                cb.success(out);
            } catch (Exception e) {
                cb.error(e);
            }
        });
    }

    @Override
    public void addMember(String groupId, String userId, String role, Callback<GroupMember> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject()
                        .put("group_id", groupId)
                        .put("user_id", userId)
                        .put("role", role == null || role.isEmpty() ? "member" : role);
                JSONObject response = api.request("POST", "/rest/v1/group_members", user.accessToken, payload);
                JSONObject row = firstRow(response);
                if (row == null) throw new IllegalStateException("Member add failed");
                cb.success(new GroupMember(
                        row.optString("id"),
                        row.optString("group_id"),
                        row.optString("user_id"),
                        row.optString("role")
                ));
            } catch (Exception e) {
                cb.error(e);
            }
        });
    }

    private UserSession required() {
        UserSession current = sessions.read();
        if (current == null) throw new IllegalStateException("Not signed in");
        return current;
    }

    private JSONObject firstRow(JSONObject response) {
        JSONArray rows = response.optJSONArray("rows");
        if (rows == null || rows.length() == 0) return null;
        return rows.optJSONObject(0);
    }
}
