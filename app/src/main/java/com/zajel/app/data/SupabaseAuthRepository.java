package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.UserSession;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

/** Supabase Auth boundary supporting either email or phone identifiers. */
public final class SupabaseAuthRepository implements AuthRepository {
    private final SupabaseClient api;
    private final SecureSessionStore store;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SupabaseAuthRepository(SupabaseClient api, SecureSessionStore store) {
        this.api = api;
        this.store = store;
    }

    @Override public UserSession current() { return store.read(); }

    @Override public void signIn(String email, String password, Callback<UserSession> callback) {
        signInIdentifier(email, password, callback);
    }

    @Override public void signUp(String email, String password, Callback<UserSession> callback) {
        signUpIdentifier("", "", email, password, callback);
    }

    @Override public void signInIdentifier(String identifier, String password, Callback<UserSession> callback) {
        executor.execute(() -> {
            try {
                String field = isEmail(identifier) ? "email" : "phone";
                JSONObject body = new JSONObject().put(field, identifier.trim()).put("password", password);
                callback.success(save(session(api.request("POST", "/auth/v1/token?grant_type=password", null, body))));
            } catch (Exception error) { callback.error(error); }
        });
    }

    @Override public void signUpIdentifier(String firstName, String lastName, String identifier, String password, Callback<UserSession> callback) {
        executor.execute(() -> {
            try {
                String field = isEmail(identifier) ? "email" : "phone";
                String first = firstName == null ? "" : firstName.trim();
                String last = lastName == null ? "" : lastName.trim();
                JSONObject metadata = new JSONObject().put("first_name", first).put("last_name", last).put("full_name", (first + " " + last).trim());
                JSONObject body = new JSONObject().put(field, identifier.trim()).put("password", password).put("data", metadata);
                JSONObject response = api.request("POST", "/auth/v1/signup", null, body);
                String token = response.optString("access_token", "");
                if (token.isEmpty()) {
                    callback.success(null); // Email/phone confirmation is required before a session exists.
                } else {
                    callback.success(save(session(response)));
                }
            } catch (Exception error) { callback.error(error); }
        });
    }

    @Override public void signOut() { store.clear(); }

    private UserSession save(UserSession session) { if (session != null) store.write(session); return session; }

    private UserSession session(JSONObject response) {
        JSONObject user = response.optJSONObject("user");
        String id = user == null ? "" : user.optString("id", "");
        String email = user == null ? "" : user.optString("email", user.optString("phone", ""));
        return new UserSession(response.optString("access_token"), id, email);
    }

    private boolean isEmail(String value) { return value != null && value.contains("@"); }
}
