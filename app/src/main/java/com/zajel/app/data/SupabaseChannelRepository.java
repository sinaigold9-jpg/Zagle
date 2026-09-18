package com.zajel.app.data;

import com.zajel.app.core.AppExecutors;
import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.*;
import java.util.*;
import org.json.*;

public final class SupabaseChannelRepository implements ChannelRepository {
    private final SupabaseClient api; private final SecureSessionStore sessions;
    public SupabaseChannelRepository(SupabaseClient a, SecureSessionStore s){api=a;sessions=s;}
    public void publicChannels(Callback<List<Channel>> cb){get("/rest/v1/channels?is_private=eq.false&select=id,title,description,owner_id,is_private,invite_token&order=created_at.desc",cb);}
    public void myChannels(Callback<List<Channel>> cb){ UserSession u=required(); get("/rest/v1/channel_members?user_id=eq."+u.userId+"&select=channels(id,title,description,owner_id,is_private,invite_token)",cb); }
    private void get(String path, Callback<List<Channel>> cb){AppExecutors.network().execute(()->{try{UserSession u=required(); JSONArray a=api.request("GET",path,u.accessToken,null).optJSONArray("rows"); List<Channel> out=new ArrayList<>(); if(a!=null)for(int i=0;i<a.length();i++){JSONObject r=a.optJSONObject(i); JSONObject x=r==null?null:(r.optJSONObject("channels")!=null?r.optJSONObject("channels"):r); if(x!=null)out.add(channel(x));} cb.success(out);}catch(Exception e){cb.error(e);}});}
    public void create(String t,String d,boolean p,Callback<Channel> cb){AppExecutors.network().execute(()->{try{UserSession u=required();JSONObject r=first(api.request("POST","/rest/v1/channels",u.accessToken,new JSONObject().put("title",t).put("description",d).put("is_private",p).put("owner_id",u.userId))); if(r==null)throw new IllegalStateException("Channel creation failed"); api.request("POST","/rest/v1/channel_members",u.accessToken,new JSONObject().put("channel_id",r.optString("id")).put("user_id",u.userId).put("role","owner")); cb.success(channel(r));}catch(Exception e){cb.error(e);}});}
    public void follow(String id,Callback<Void> cb){write("/rest/v1/channel_members",new JSONObject().put("channel_id",id).put("user_id",required().userId).put("role","subscriber"),cb);}
    public void posts(String id,Callback<List<ChannelPost>> cb){AppExecutors.network().execute(()->{try{UserSession u=required();JSONArray a=api.request("GET","/rest/v1/channel_posts?channel_id=eq."+id+"&select=id,channel_id,author_id,body,created_at&order=created_at.asc",u.accessToken,null).optJSONArray("rows");List<ChannelPost> out=new ArrayList<>();if(a!=null)for(int i=0;i<a.length();i++){JSONObject r=a.optJSONObject(i);if(r!=null)out.add(post(r));}cb.success(out);}catch(Exception e){cb.error(e);}});}
    public void publish(String id,String body,Callback<ChannelPost> cb){AppExecutors.network().execute(()->{try{UserSession u=required();JSONObject r=first(api.request("POST","/rest/v1/channel_posts",u.accessToken,new JSONObject().put("channel_id",id).put("author_id",u.userId).put("body",body)));if(r==null)throw new IllegalStateException("Post failed");cb.success(post(r));}catch(Exception e){cb.error(e);}});}
    public void createInvite(String id,Callback<String> cb){AppExecutors.network().execute(()->{try{UserSession u=required();JSONObject r=first(api.request("POST","/rest/v1/channel_invites",u.accessToken,new JSONObject().put("channel_id",id).put("created_by",u.userId)));if(r==null)throw new IllegalStateException("Invite failed");cb.success(r.optString("token"));}catch(Exception e){cb.error(e);}});}
    public void joinByInvite(String token,Callback<Channel> cb){AppExecutors.network().execute(()->{try{UserSession u=required();JSONObject r=first(api.request("POST","/rest/v1/channel_invites/redeem",u.accessToken,new JSONObject().put("token",token).put("user_id",u.userId)));if(r==null)throw new IllegalStateException("Invite invalid");cb.success(channel(r));}catch(Exception e){cb.error(e);}});}
    private void write(String p,JSONObject b,Callback<Void> cb){AppExecutors.network().execute(()->{try{api.request("POST",p,required().accessToken,b);cb.success(null);}catch(Exception e){cb.error(e);}});}
    private UserSession required(){UserSession u=sessions.read();if(u==null)throw new IllegalStateException("Not signed in");return u;}
    private JSONObject first(JSONObject x){JSONArray a=x.optJSONArray("rows");return a==null||a.length()==0?null:a.optJSONObject(0);}
    private Channel channel(JSONObject x){return new Channel(x.optString("id"),x.optString("title"),x.optString("description"),x.optString("owner_id"),x.optBoolean("is_private"),x.optString("invite_token"));}
    private ChannelPost post(JSONObject x){return new ChannelPost(x.optString("id"),x.optString("channel_id"),x.optString("author_id"),x.optString("body"),x.optString("created_at"));}
}
