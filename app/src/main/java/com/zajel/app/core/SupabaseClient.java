package com.zajel.app.core;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import org.json.JSONObject;

/** Small HTTP boundary with connectivity checks and bounded retries for transient failures. */
public final class SupabaseClient {
    private static final int MAX_ATTEMPTS = 3;
    private final Context context;

    public SupabaseClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public JSONObject request(String method, String path, String bearer, JSONObject body) throws Exception {
        if (!SupabaseConfig.isConfigured()) throw new IOException("Supabase is not configured for this build");
        Exception last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            if (!NetworkStatus.isOnline(context)) {
                throw new IOException("لا يوجد اتصال بالإنترنت. تحقق من الشبكة ثم حاول مرة أخرى.");
            }
            try {
                return execute(method, path, bearer, body);
            } catch (IOException error) {
                last = error;
                if (attempt < MAX_ATTEMPTS) {
                    try { Thread.sleep(attempt * 1000L); } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt(); throw interrupted;
                    }
                }
            }
        }
        throw new IOException("تعذر الاتصال بالخادم بعد عدة محاولات. تحقق من الإنترنت وحاول مرة أخرى.", last);
    }

    private JSONObject execute(String method, String path, String bearer, JSONObject body) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(SupabaseConfig.url() + path).openConnection();
        try {
            connection.setRequestMethod(method);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("apikey", SupabaseConfig.key());
            connection.setRequestProperty("Accept", "application/json");
            if (bearer != null && !bearer.isEmpty()) connection.setRequestProperty("Authorization", "Bearer " + bearer);
            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Prefer", "return=representation");
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(body.toString().getBytes("UTF-8"));
                }
            }
            return finish(connection);
        } finally {
            connection.disconnect();
        }
    }

    /** Uploads are kept behind the same connectivity boundary as JSON requests. */
    public void upload(String path, String bearer, java.io.File file, String mime) throws Exception {
        if (!NetworkStatus.isOnline(context)) throw new IOException("لا يوجد اتصال بالإنترنت. تحقق من الشبكة ثم حاول مرة أخرى.");
        Exception last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(SupabaseConfig.url() + path).openConnection();
                try {
                    connection.setRequestMethod("POST"); connection.setDoOutput(true);
                    connection.setConnectTimeout(15000); connection.setReadTimeout(20000);
                    connection.setRequestProperty("apikey", SupabaseConfig.key());
                    if (bearer != null && !bearer.isEmpty()) connection.setRequestProperty("Authorization", "Bearer " + bearer);
                    connection.setRequestProperty("Content-Type", mime == null ? "application/octet-stream" : mime);
                    try (java.io.InputStream input = new java.io.FileInputStream(file); OutputStream output = connection.getOutputStream()) {
                        byte[] buffer = new byte[8192]; int count;
                        while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                    }
                    finish(connection); return;
                } finally { connection.disconnect(); }
            } catch (IOException error) {
                last = error;
                if (attempt < MAX_ATTEMPTS) try { Thread.sleep(attempt * 1000L); } catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); throw interrupted; }
            }
        }
        throw new IOException("تعذر رفع الملف بعد عدة محاولات. تحقق من الإنترنت وحاول مرة أخرى.", last);
    }

    private JSONObject finish(HttpURLConnection connection) throws Exception {
        int code = connection.getResponseCode();
        InputStream input = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
        String text = read(input);
        if (code >= 400) throw new IOException(text.isEmpty() ? "Request failed: " + code : text);
        return text.isEmpty() ? new JSONObject() : new JSONObject(text.startsWith("[") ? "{\"rows\":" + text + "}" : text);
    }

    private String read(InputStream input) throws IOException {
        if (input == null) return "";
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            String line; while ((line = reader.readLine()) != null) result.append(line);
        }
        return result.toString();
    }
}
