package com.zajel.app.core;

import android.content.Context;
import com.zajel.app.data.*;

public final class AppContainer {
    public final AuthRepository auth; public final ProfileRepository profiles; public final ChatRepository chats;
    public final GroupRepository groups; public final RoomRepository rooms; public final ExploreRepository explore;
    public final NotificationRepository notifications; public final ContactsRepository contacts;
    public AppContainer(Context context) { Context app = context.getApplicationContext(); SecureSessionStore s = new SecureSessionStore(app); SupabaseClient api = new SupabaseClient(); auth = new SupabaseAuthRepository(api, s); profiles = new SupabaseProfileRepository(api, s); chats = new SupabaseChatRepository(api, s); groups = new SupabaseGroupRepository(api, s); rooms = new SupabaseRoomRepository(api, s); explore = new SupabaseExploreRepository(api, s); notifications = new SupabaseNotificationRepository(api, s); contacts = new AndroidContactsRepository(app); }
}
