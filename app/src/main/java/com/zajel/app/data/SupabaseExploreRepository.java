package com.zajel.app.data;

import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseExploreRepository implements ExploreRepository {
    private final SupabaseClient api; private final SecureSessionStore sessions;
    public SupabaseExploreRepository(SupabaseClient api, SecureSessionStore sessions) { this.api=api; this.sessions=sessions; }
    public void search(String query, Callback<List<ExploreItem>> cb) {
        Executors.newSingleThreadExecutor().execute(() -> { try {
            UserSession u=required(); String q=URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.name());
            List<ExploreItem> out=new ArrayList<>();
            addGroups(out, api.request("GET", "/rest/v1/groups?is_private=eq.false&or=(title.ilike.*"+q+"*,description.ilike.*"+q+"*)&select=id,title,description,owner_id&limit=25", u.accessToken, null));
            addRooms(out, api.request("GET", "/rest/v1/rooms?is_private=eq.false&or=(title.ilike.*"+q+"*,description.ilike.*"+q+"*)&select=id,title,description,owner_id&limit=25", u.accessToken, null));
            addProfiles(out, api.request("GET", "/rest/v1/profiles?or=(username.ilike.*"+q+"*,display_name.ilike.*"+q+"*)&select=id,username,display_name&limit=25", u.accessToken, null));
            cb.success(out);
        } catch(Exception e) { cb.error(e); } });
    }
    public void suggestions(Callback<List<PublicSuggestion>> cb) { queryPublic("/rest/v1/explore_suggestions?select=id,type,title,reason&order=rank.asc&limit=20", cb, false); }
    public void publicAds(Callback<List<PublicAd>> cb) { queryPublic("/rest/v1/public_ads?enabled=eq.true&select=id,title,body,target_url&order=priority.desc&limit=5", cb, true); }
    private void queryPublic(String path, ObjectCallback cb, boolean ads) { Executors.newSingleThreadExecutor().execute(() -> { try { UserSession u=required(); JSONObject r=api.request("GET",path,u.accessToken,null); JSONArray a=r.optJSONArray("rows"); if(ads){List<PublicAd> out=new ArrayList<>();if(a!=null)for(int i=0;i<a.length();i++){JSONObject x=a.optJSONObject(i);if(x!=null)out.add(new PublicAd(x.optString("id"),x.optString("title"),x.optString("body"),x.optString("target_url")));}((Callback<List<PublicAd>>)cb).success(out);}else{List<PublicSuggestion> out=new ArrayList<>();if(a!=null)for(int i=0;i<a.length();i++){JSONObject x=a.optJSONObject(i);if(x!=null)out.add(new PublicSuggestion(x.optString("id"),x.optString("type"),x.optString("title"),x.optString("reason")));}((Callback<List<PublicSuggestion>>)cb).success(out);}}catch(Exception e){cb.error(e);}}); }
    private interface ObjectCallback { void error(Exception e); }
    private void addGroups(List<ExploreItem> o, JSONObject r){JSONArray a=r.optJSONArray("rows");if(a!=null)for(int i=0;i<a.length();i++){JSONObject x=a.optJSONObject(i);if(x!=null)o.add(new ExploreItem(x.optString("id"),"group",x.optString("title"),x.optString("description"),x.optString("owner_id")));}}
    private void addRooms(List<ExploreItem> o, JSONObject r){JSONArray a=r.optJSONArray("rows");if(a!=null)for(int i=0;i<a.length();i++){JSONObject x=a.optJSONObject(i);if(x!=null)o.add(new ExploreItem(x.optString("id"),"room",x.optString("title"),x.optString("description"),x.optString("owner_id")));}}
    private void addProfiles(List<ExploreItem> o, JSONObject r){JSONArray a=r.optJSONArray("rows");if(a!=null)for(int i=0;i<a.length();i++){JSONObject x=a.optJSONObject(i);if(x!=null)o.add(new ExploreItem(x.optString("id"),"user",x.optString("display_name"),x.optString("username"),x.optString("id")));}}
    private UserSession required(){UserSession u=sessions.read();if(u==null)throw new IllegalStateException("Not signed in");return u;}
}
