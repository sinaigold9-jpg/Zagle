package com.zajel.app.data;

import com.zajel.app.core.AppExecutors;
import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.Community;
import com.zajel.app.domain.UserSession;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseCommunityRepository
        implements CommunityRepository {

    private final SupabaseClient api;
    private final SecureSessionStore sessions;

    public SupabaseCommunityRepository(
            SupabaseClient api,
            SecureSessionStore sessions) {
        this.api = api;
        this.sessions = sessions;
    }

    @Override
    public void myCommunities(Callback<List<Community>> callback) {
        UserSession user = required();

        String path = "/rest/v1/community_members?user_id=eq."
                + user.userId
                + "&select=communities(id,title,description,owner_id)";

        load(path, callback);
    }

    private void load(
            String path,
            Callback<List<Community>> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONArray rows = api.request(
                        "GET",
                        path,
                        user.accessToken,
                        null
                ).optJSONArray("rows");

                List<Community> communities = new ArrayList<>();

                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);

                        JSONObject community = row == null
                                ? null
                                : row.optJSONObject("communities");

                        if (community != null) {
                            communities.add(toCommunity(community));
                        }
                    }
                }

                callback.success(communities);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void create(
            String title,
            String description,
            Callback<Community> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONObject body = new JSONObject();
                body.put("title", title);
                body.put("description", description);
                body.put("owner_id", user.userId);

                JSONObject result = first(
                        api.request(
                                "POST",
                                "/rest/v1/communities",
                                user.accessToken,
                                body
                        )
                );

                if (result == null) {
                    throw new IllegalStateException(
                            "Community creation failed"
                    );
                }

                JSONObject membership = new JSONObject();
                membership.put(
                        "community_id",
                        result.optString("id")
                );
                membership.put("user_id", user.userId);
                membership.put("role", "owner");

                api.request(
                        "POST",
                        "/rest/v1/community_members",
                        user.accessToken,
                        membership
                );

                callback.success(toCommunity(result));
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void addGroup(
            String communityId,
            String groupId,
            Callback<Void> callback) {

        link(
                "community_groups",
                "community_id",
                communityId,
                "group_id",
                groupId,
                callback
        );
    }

    @Override
    public void addChannel(
            String communityId,
            String channelId,
            Callback<Void> callback) {

        link(
                "community_channels",
                "community_id",
                communityId,
                "channel_id",
                channelId,
                callback
        );
    }

    private void link(
            String table,
            String firstKey,
            String firstValue,
            String secondKey,
            String secondValue,
            Callback<Void> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                // Build the JSON inside try so JSONException is handled.
                JSONObject body = new JSONObject();
                body.put(firstKey, firstValue);
                body.put(secondKey, secondValue);

                api.request(
                        "POST",
                        "/rest/v1/" + table,
                        user.accessToken,
                        body
                );

                callback.success(null);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    @Override
    public void linkedGroups(
            String communityId,
            Callback<List<String>> callback) {

        ids(
                "community_groups?community_id=eq."
                        + communityId
                        + "&select=group_id",
                "group_id",
                callback
        );
    }

    @Override
    public void linkedChannels(
            String communityId,
            Callback<List<String>> callback) {

        ids(
                "community_channels?community_id=eq."
                        + communityId
                        + "&select=channel_id",
                "channel_id",
                callback
        );
    }

    private void ids(
            String path,
            String key,
            Callback<List<String>> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONArray rows = api.request(
                        "GET",
                        "/rest/v1/" + path,
                        user.accessToken,
                        null
                ).optJSONArray("rows");

                List<String> ids = new ArrayList<>();

                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);

                        if (row != null) {
                            ids.add(row.optString(key));
                        }
                    }
                }

                callback.success(ids);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    private UserSession required() {
        UserSession user = sessions.read();

        if (user == null) {
            throw new IllegalStateException("Not signed in");
        }

        return user;
    }

    private JSONObject first(JSONObject response) {
        JSONArray rows = response.optJSONArray("rows");

        if (rows == null || rows.length() == 0) {
            return null;
        }

        return rows.optJSONObject(0);
    }

    private Community toCommunity(JSONObject json) {
        return new Community(
                json.optString("id"),
                json.optString("title"),
                json.optString("description"),
                json.optString("owner_id")
        );
    }
                 }
