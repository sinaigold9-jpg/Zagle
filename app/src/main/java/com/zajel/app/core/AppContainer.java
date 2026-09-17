package com.zajel.app.core;

import android.content.Context;
import com.zajel.app.data.GroupRepository;
import com.zajel.app.data.RoomRepository;
import com.zajel.app.data.SecureSessionStore;
import com.zajel.app.data.SupabaseAuthRepository;
import com.zajel.app.data.SupabaseChatRepository;
import com.zajel.app.data.SupabaseGroupRepository;
import com.zajel.app.data.SupabaseProfileRepository;
import com.zajel.app.data.SupabaseRoomRepository;
import com.zajel.app.data.AuthRepository;
import com.zajel.app.data.ProfileRepository;

public final class AppContainer {
    public final AuthRepository auth;
    public final ProfileRepository profiles;
    public final ChatRepository chats;
    public final GroupRepository groups;
    public final RoomRepository rooms;

    public AppContainer(Context context) {
        Context app = context.getApplicationContext();
        SecureSessionStore sessions = new SecureSessionStore(app);
        SupabaseClient api = new SupabaseClient();
        auth = new SupabaseAuthRepository(api, sessions);
        profiles = new SupabaseProfileRepository(api, sessions);
        chats = new SupabaseChatRepository(app, api, sessions);
        groups = new SupabaseGroupRepository(api, sessions);
        rooms = new SupabaseRoomRepository(api, sessions);
    }
}
