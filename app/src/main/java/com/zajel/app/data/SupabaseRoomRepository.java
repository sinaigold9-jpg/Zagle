package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.Room;
import com.zajel.app.domain.RoomMember;
import com.zajel.app.domain.RoomMessage;
import com.zajel.app.domain.UserSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseRoomRepository implements RoomRepository {
    private final SupabaseClient api;
    private final SecureSessionStore sessions;

    public SupabaseRoomRepository(SupabaseClient api, SecureSessionStore sessions) {
        this.api = api;
        this.sessions = sessions;
    }

    @Override
    public void myRooms(Callback<List<Room>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                String path = "/rest/v1/room_members?user_id=eq." + user.userId + "&select=room_id,rooms(id,title,description,is_private,owner_id)";
                JSONObject response = api.request("GET", path, user.accessToken, null);
                JSONArray rows = response.optJSONArray("rows");
                List<Room> out = new ArrayList<>();
                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);
                        if (row == null) continue;
                        JSONObject roomObj = row.optJSONObject("rooms");
                        if (roomObj == null) continue;
                        out.add(new Room(
                                roomObj.optString("id"),
                                roomObj.optString("title"),
                                roomObj.optString("description"),
                                roomObj.optString("owner_id"),
                                roomObj.optBoolean("is_private")
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
    public void createRoom(String title, String description, boolean isPrivate, Callback<Room> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject()
                        .put("title", title)
                        .put("description", description)
                        .put("is_private", isPrivate)
                        .put("owner_id", user.userId);
                JSONObject response = api.request("POST", "/rest/v1/rooms", user.accessToken, payload);
                JSONObject row = firstRow(response);
                if (row == null) throw new IllegalStateException("Room creation failed");
                JSONObject memberPayload = new JSONObject()
                        .put("room_id", row.optString("id"))
                        .put("user_id", user.userId)
                        .put("role", "owner");
                api.request("POST", "/rest/v1/room_members", user.accessToken, memberPayload);
                cb.success(new Room(
                        row.optString("id"),
                        row.optString("title"),
                        row.optString("description"),
                        row.optString("owner_id"),
                        row.optBoolean("is_private")
                ));
            } catch (Exception e) {
                cb.error(e);
            }
        });
    }

    @Override
    public void joinRoom(String roomId, Callback<RoomMember> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject()
                        .put("room_id", roomId)
                        .put("user_id", user.userId)
                        .put("role", "member");
                JSONObject response = api.request("POST", "/rest/v1/room_members", user.accessToken, payload);
                JSONObject row = firstRow(response);
                if (row == null) throw new IllegalStateException("Unable to join room");
                cb.success(new RoomMember(
                        row.optString("id"),
                        row.optString("room_id"),
                        row.optString("user_id"),
                        row.optString("role")
                ));
            } catch (Exception e) {
                cb.error(e);
            }
        });
    }

    @Override
    public void members(String roomId, Callback<List<RoomMember>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                String path = "/rest/v1/room_members?room_id=eq." + roomId + "&select=id,room_id,user_id,role";
                JSONObject response = api.request("GET", path, user.accessToken, null);
                JSONArray rows = response.optJSONArray("rows");
                List<RoomMember> out = new ArrayList<>();
                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);
                        if (row == null) continue;
                        out.add(new RoomMember(
                                row.optString("id"),
                                row.optString("room_id"),
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
    public void messages(String roomId, Callback<List<RoomMessage>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                String path = "/rest/v1/room_messages?room_id=eq." + roomId + "&select=id,room_id,sender_id,body,created_at&order=created_at.asc";
                JSONObject response = api.request("GET", path, user.accessToken, null);
                JSONArray rows = response.optJSONArray("rows");
                List<RoomMessage> out = new ArrayList<>();
                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);
                        if (row == null) continue;
                        out.add(new RoomMessage(
                                row.optString("id"),
                                row.optString("room_id"),
                                row.optString("sender_id"),
                                row.optString("body"),
                                row.optString("created_at")
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
    public void sendMessage(String roomId, String body, Callback<RoomMessage> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession user = required();
                JSONObject payload = new JSONObject()
                        .put("room_id", roomId)
                        .put("sender_id", user.userId)
                        .put("body", body);
                JSONObject response = api.request("POST", "/rest/v1/room_messages", user.accessToken, payload);
                JSONObject row = firstRow(response);
                if (row == null) throw new IllegalStateException("Message send failed");
                cb.success(new RoomMessage(
                        row.optString("id"),
                        row.optString("room_id"),
                        row.optString("sender_id"),
                        row.optString("body"),
                        row.optString("created_at")
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
