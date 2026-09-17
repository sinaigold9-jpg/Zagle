package com.zajel.app.core;

import android.content.Context;
import com.zajel.app.data.AuthRepository;
import com.zajel.app.data.ProfileRepository;
import com.zajel.app.data.SecureSessionStore;
import com.zajel.app.data.SupabaseAuthRepository;
import com.zajel.app.data.SupabaseProfileRepository;

public final class AppContainer {
    public final AuthRepository auth;
    public final ProfileRepository profiles;
    public AppContainer(Context context) {
        SecureSessionStore sessions = new SecureSessionStore(context.getApplicationContext());
        SupabaseClient client = new SupabaseClient();
        auth = new SupabaseAuthRepository(client, sessions);
        profiles = new SupabaseProfileRepository(client, sessions);
    }
}
