package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.UserSession;
import org.json.JSONObject;
import java.util.concurrent.Executors;

public final class SupabaseAuthRepository implements AuthRepository {
 private final SupabaseClient api; private final SecureSessionStore store; public SupabaseAuthRepository(SupabaseClient a,SecureSessionStore s){api=a;store=s;}
 public UserSession current(){return store.read();}
 public void signIn(String email,String password,Callback<UserSession> cb){auth(email,password,cb);}
 public void signUp(String email,String password,Callback<UserSession> cb){auth(email,password,cb);}
 private void auth(String e,String p,Callback<UserSession> cb){Executors.newSingleThreadExecutor().execute(()->{try{JSONObject b=new JSONObject().put("email",e).put("password",p); JSONObject r=api.request("POST","/auth/v1/token?grant_type=password",null,b); UserSession s=new UserSession(r.optString("access_token"),r.getJSONObject("user").getString("id"),e);store.save(s);cb.success(s);}catch(Exception x){cb.error(x);}});}
 public void signOut(){store.clear();}
}
