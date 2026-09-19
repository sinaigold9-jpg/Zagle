package com.zajel.app.data;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public final class LocalBackupRepository {
    private final Context context;

    public LocalBackupRepository(Context context) { this.context = context.getApplicationContext(); }

    public void exportTo(Uri destination, boolean messages, boolean media, boolean files) throws Exception {
        JSONObject manifest = new JSONObject()
            .put("format", "zajel-backup-v1")
            .put("created_at", System.currentTimeMillis())
            .put("messages", messages)
            .put("media", media)
            .put("files", files)
            .put("messages_data", collectMessageRecords())
            .put("media_data", collectMediaRecords())
            .put("file_data", collectFileRecords());

        try (OutputStream out = context.getContentResolver().openOutputStream(destination)) {
            if (out == null) throw new IllegalStateException("Cannot open backup destination");
            out.write(manifest.toString(2).getBytes(StandardCharsets.UTF_8));
        }
    }

    public JSONObject inspect(Uri source) throws Exception {
        try (InputStream in = context.getContentResolver().openInputStream(source)) {
            if (in == null) throw new IllegalStateException("Cannot open backup");
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int count;
            while ((count = in.read(chunk)) != -1) buffer.write(chunk, 0, count);
            return new JSONObject(buffer.toString(StandardCharsets.UTF_8.name()));
        }
    }

    private JSONArray collectMessageRecords() {
        JSONArray records = new JSONArray();
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();
        if (files == null) return records;
        for (File file : files) {
            if (!file.isFile()) continue;
            if (!file.getName().toLowerCase().endsWith(".json") && !file.getName().toLowerCase().contains("message")) continue;
            JSONObject item = new JSONObject()
                .put("type", "message")
                .put("name", file.getName())
                .put("path", file.getAbsolutePath())
                .put("size_bytes", file.length())
                .put("modified_at", file.lastModified());
            try {
                String text = readText(file);
                if (!text.isEmpty()) item.put("preview", text.substring(0, Math.min(200, text.length())));
            } catch (Exception ignored) {
                item.put("preview", "");
            }
            records.put(item);
        }
        if (records.length() == 0) {
            records.put(new JSONObject().put("type", "message").put("source", "none").put("note", "No persisted message records were found in app storage."));
        }
        return records;
    }

    private JSONArray collectMediaRecords() {
        JSONArray records = new JSONArray();
        File dir = context.getCacheDir();
        appendFiles(records, dir, "media");
        if (records.length() == 0) {
            records.put(new JSONObject().put("type", "media").put("source", "none").put("note", "No cached media files were found."));
        }
        return records;
    }

    private JSONArray collectFileRecords() {
        JSONArray records = new JSONArray();
        File dir = new File(context.getFilesDir(), "received");
        appendFiles(records, dir, "file");
        if (records.length() == 0) {
            records.put(new JSONObject().put("type", "file").put("source", "none").put("note", "No imported files were found in the local file store."));
        }
        return records;
    }

    private void appendFiles(JSONArray records, File root, String type) {
        if (root == null || !root.exists()) return;
        File[] files = root.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isFile()) continue;
            JSONObject item = new JSONObject()
                .put("type", type)
                .put("name", file.getName())
                .put("path", file.getAbsolutePath())
                .put("size_bytes", file.length())
                .put("modified_at", file.lastModified());
            records.put(item);
        }
    }

    private String readText(File file) throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try (InputStream in = new java.io.FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
        }
        return out.toString(StandardCharsets.UTF_8.name());
    }
}
