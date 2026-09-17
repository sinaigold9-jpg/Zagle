package com.zajel.app.core;
import android.content.Context;import com.zajel.app.data.*;
public final class AppContainer { public final AuthRepository auth; public final ProfileRepository profiles; public final ChatRepository chats; public AppContainer(Context c){SecureSessionStore s=new SecureSessionStore(c.getApplicationContext());SupabaseClient a=new SupabaseClient();auth=new SupabaseAuthRepository(a,s);profiles=new SupabaseProfileRepository(a,s);chats=new SupabaseChatRepository(a,s);} }
