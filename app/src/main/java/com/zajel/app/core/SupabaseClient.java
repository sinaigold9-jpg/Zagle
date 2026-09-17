package com.zajel.app.core;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public final class SupabaseClient {
    public JSONObject request(String method, String path, String bearer, JSONObject body) throws Exception {
        if (!SupabaseConfig.isConfigured()) throw new IOException("Supabase is not configured for this build");
        HttpURLConnection c = (HttpURLConnection) new URL(SupabaseConfig.url() + path).openConnection();
        c.setRequestMethod(method); c.setConnectTimeout(15000); c.setReadTimeout(20000);
        c.setRequestProperty("apikey", SupabaseConfig.key()); c.setRequestProperty("Accept", "application/json");
        if (bearer != null && !bearer.isEmpty()) c.setRequestProperty("Authorization", "Bearer " + bearer);
        if (body != null) { c.setDoOutput(true); c.setRequestProperty("Content-Type", "application/json"); c.setRequestProperty("Prefer", "return=representation"); try (OutputStream o=c.getOutputStream()) { o.write(body.toString().getBytes("UTF-8")); } }
        int code=c.getResponseCode(); InputStream in=code >= 400 ? c.getErrorStream() : c.getInputStream();
        String text=read(in); if (code >= 400) throw new IOException(text.isEmpty() ? "Request failed: " + code : text);
        return text.isEmpty() ? new JSONObject() : new JSONObject(text.startsWith("[") ? "{\"rows\":" + text + "}" : text);
    }
    private String read(InputStream in) throws IOException { if (in==null) return ""; StringBuilder s=new StringBuilder(); try(BufferedReader r=new BufferedReader(new InputStreamReader(in,"UTF-8"))){ String l; while((l=r.readLine())!=null)s.append(l); } return s.toString(); }
}
