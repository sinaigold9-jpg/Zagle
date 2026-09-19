package com.zajel.app.core;

import android.content.Context;
import com.zajel.app.data.AndroidContactsRepository;
import com.zajel.app.data.AuthRepository;
import com.zajel.app.data.ChannelRepository;
import com.zajel.app.data.ChatRepository;
import com.zajel.app.data.CommunityRepository;
import com.zajel.app.data.ContactsRepository;
import com.zajel.app.data.ExploreRepository;
import com.zajel.app.data.GroupRepository;
import com.zajel.app.data.MediaStorageRepository;
import com.zajel.app.data.NotificationRepository;
import com.zajel.app.data.ProfileRepository;
import com.zajel.app.data.RoomRepository;
import com.zajel.app.data.SecureSessionStore;
import com.zajel.app.data.SecurityRepository;
import com.zajel.app.data.SupabaseAuthRepository;
import com.zajel.app.data.SupabaseChannelRepository;
import com.zajel.app.data.SupabaseChatRepository;
import com.zajel.app.data.SupabaseCommunityRepository;
import com.zajel.app.data.SupabaseExploreRepository;
import com.zajel.app.data.SupabaseGroupRepository;
import com.zajel.app.data.SupabaseMediaStorageRepository;
import com.zajel.app.data.SupabaseNotificationRepository;
import com.zajel.app.data.SupabaseProfileRepository;
import com.zajel.app.data.SupabaseRoomRepository;
import com.zajel.app.data.SupabaseSecurityRepository;

/** Single composition root for the Native Android application. */
public final class AppContainer {
    public final AuthRepository auth; public final ProfileRepository profiles; public final ChatRepository chats;
    public final GroupRepository groups; public final RoomRepository rooms; public final ExploreRepository explore;
    public final NotificationRepository notifications; public final ContactsRepository contacts;
    public final ChannelRepository channels; public final CommunityRepository communities; public final SecurityRepository security;
    public AppContainer(Context context) {
        Context app = context.getApplicationContext(); SecureSessionStore sessionStore = new SecureSessionStore(app);
        SupabaseClient api = new SupabaseClient(app); MediaStorageRepository mediaStorage = new SupabaseMediaStorageRepository(api);
        auth = new SupabaseAuthRepository(api, sessionStore); profiles = new SupabaseProfileRepository(api, sessionStore);
        chats = new SupabaseChatRepository(app, api, sessionStore, mediaStorage); groups = new SupabaseGroupRepository(api, sessionStore);
        rooms = new SupabaseRoomRepository(api, sessionStore); explore = new SupabaseExploreRepository(api, sessionStore);
        notifications = new SupabaseNotificationRepository(api, sessionStore); contacts = new AndroidContactsRepository(app);
        channels = new SupabaseChannelRepository(api, sessionStore); communities = new SupabaseCommunityRepository(api, sessionStore);
        security = new SupabaseSecurityRepository(api, sessionStore);
    }
}
