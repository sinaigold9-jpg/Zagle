package com.zajel.app.data;

import android.content.Context;
import android.net.Uri;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONObject;

public final class LocalBackupRepository {
    private final Context context;
    public LocalBackupRepository(Context context) { this.context = context.getApplicationContext(); }
    public void exportTo(Uri destination, boolean messages, boolean media, boolean files) throws Exception {
        JSONObject manifest = new JSONObject().put("format", "zajel-backup-v1").put("created_at", System.currentTimeMillis()).put("messages", messages).put("media", media).put("files", files);
        try (OutputStream out = context.getContentResolver().openOutputStream(destination)) { if (out == null) throw new IllegalStateException("Cannot open backup destination"); out.write(manifest.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)); }
    }
    public JSONObject inspect(Uri source) throws Exception {
        try (InputStream in = context.getContentResolver().openInputStream(source)) { if (in == null) throw new IllegalStateException("Cannot open backup"); java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream(); byte[] chunk = new byte[4096]; int count; while ((count = in.read(chunk)) != -1) buffer.write(chunk, 0, count); return new JSONObject(buffer.toString("UTF-8")); }
    }
}
