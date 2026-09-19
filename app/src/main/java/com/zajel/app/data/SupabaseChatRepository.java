package com.zajel.app.data;

import android.content.Context;
import android.net.Uri;
import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.Attachment;
import com.zajel.app.domain.Conversation;
import com.zajel.app.domain.Message;
import com.zajel.app.domain.MessageStatus;
import com.zajel.app.domain.UserSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseChatRepository implements ChatRepository {
    private final SupabaseClient api;
    private final SecureSessionStore sessions;
    private final LocalMediaCache cache;
    private final MediaStorageRepository mediaStorage;

    public SupabaseChatRepository(Context context, SupabaseClient api, SecureSessionStore sessions) {
        this(context, api, sessions, new SupabaseMediaStorageRepository(api));
    }

    public SupabaseChatRepository(Context context, SupabaseClient api, SecureSessionStore sessions, MediaStorageRepository mediaStorage) {
        this.api = api;
        this.sessions = sessions;
        this.cache = new LocalMediaCache(context);
        this.mediaStorage = mediaStorage;
    }

    @Override public void conversations(String id, Callback<List<Conversation>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession u = required();
                JSONObject r = api.request("GET", "/rest/v1/conversation_members?user_id=eq." + u.userId + "&select=conversation_id,conversations(id,title,is_direct)&order=created_at.desc", u.accessToken, null);
                JSONArray rows = r.optJSONArray("rows");
                List<Conversation> out = new ArrayList<>();
                if (rows != null) for (int i = 0; i < rows.length(); i++) {
                    JSONObject c = rows.optJSONObject(i);
                    JSONObject x = c == null ? null : c.optJSONObject("conversations");
                    if (x != null) out.add(new Conversation(x.optString("id"), x.optString("title"), x.optBoolean("is_direct")));
                }
                cb.success(out);
            } catch (Exception e) { cb.error(e); }
        });
    }

    @Override public void messages(String id, Callback<List<Message>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserSession u = required();
                JSONObject r = api.request("GET", "/rest/v1/messages?conversation_id=eq." + id + "&select=id,conversation_id,sender_id,body,created_at,status,attachments(id,message_id,name,mime_type,storage_path,size_bytes)&order=created_at.asc&limit=100", u.accessToken, null);
                JSONArray rows = r.optJSONArray("rows");
                List<Message> out = new ArrayList<>();
                if (rows != null) for (int i = 0; i < rows.length(); i++) {
                    JSONObject x = rows.optJSONObject(i);
                    if (x != null) out.add(message(x));
                }
                cb.success(out);
            } catch (Exception e) { cb.error(e); }
        });
    }

    @Override public void sendText(String id, String body, Callback<Message> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try { cb.success(insertMessage(required(), id, body, "text", null, null, 0)); }
            catch (Exception e) { cb.error(e); }
        });
    }

    @Override public void sendAttachment(String id, Uri source, String name, String mime, Callback<Message> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            File temp = null;
            try {
                UserSession u = required();
                temp = cache.copy(source, name);
                String path = u.userId + "/" + UUID.randomUUID() + "_" + name;
                MediaStorageRepository.StoredMedia stored = mediaStorage.upload(u.accessToken, temp, path, mime);
                cb.success(insertMessage(u, id, "", mime, stored.path, name, stored.sizeBytes, mime));
            } catch (Exception e) { cb.error(e); }
            finally { cache.delete(temp); }
        });
    }

    private Message insertMessage(UserSession u, String id, String body, String type, String path, String name, long sizeBytes) throws Exception {
        return insertMessage(u, id, body, type, path, name, sizeBytes, type);
    }

    private Message insertMessage(UserSession u, String id, String body, String type, String path, String name, long sizeBytes, String mimeType) throws Exception {
        JSONObject payload = new JSONObject().put("conversation_id", id).put("sender_id", u.userId).put("body", body).put("message_type", type).put("status", "SENT");
        JSONObject r = api.request("POST", "/rest/v1/messages", u.accessToken, payload);
        JSONObject x = first(r);
        if (x == null) throw new IOException("Message was not acknowledged");
        if (path != null && name != null) {
            JSONObject attachmentPayload = new JSONObject().put("message_id", x.optString("id")).put("name", name).put("mime_type", mimeType).put("storage_path", path).put("size_bytes", sizeBytes);
            JSONObject attachmentResponse = api.request("POST", "/rest/v1/attachments", u.accessToken, attachmentPayload);
            JSONObject attachment = first(attachmentResponse);
            if (attachment != null) x.put("attachments", new JSONArray().put(attachment));
        }
        return message(x);
    }

    @Override public void updateStatus(String id, MessageStatus status, Callback<Boolean> cb) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try { api.request("PATCH", "/rest/v1/messages?id=eq." + id, required().accessToken, new JSONObject().put("status", status.name())); cb.success(true); }
            catch (Exception e) { cb.error(e); }
        });
    }

    private Message message(JSONObject x) {
        Attachment a = null;
        JSONArray aa = x.optJSONArray("attachments");
        if (aa != null && aa.length() > 0) {
            JSONObject z = aa.optJSONObject(0);
            if (z != null) a = new Attachment(z.optString("id"), z.optString("message_id"), z.optString("name"), z.optString("mime_type"), z.optString("storage_path"), z.optLong("size_bytes"));
        }
        MessageStatus st;
        try { st = MessageStatus.valueOf(x.optString("status", "SENT")); } catch (Exception e) { st = MessageStatus.SENT; }
        return new Message(x.optString("id"), x.optString("conversation_id"), x.optString("sender_id"), x.optString("body"), x.optString("created_at"), st, a);
    }

    private JSONObject first(JSONObject r) { JSONArray a = r.optJSONArray("rows"); return a == null ? null : a.optJSONObject(0); }
    private UserSession required() { UserSession u = sessions.read(); if (u == null) throw new IllegalStateException("Not signed in"); return u; }
}
