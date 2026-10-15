package com.zajel.app.data;

import com.zajel.app.core.AppExecutors;
import com.zajel.app.core.SupabaseClient;
import com.zajel.app.domain.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public final class SupabaseChannelRepository implements ChannelRepository {

    private final SupabaseClient api;
    private final SecureSessionStore sessions;

    public SupabaseChannelRepository(
            SupabaseClient api,
            SecureSessionStore sessions) {
        this.api = api;
        this.sessions = sessions;
    }

    public void publicChannels(Callback<List<Channel>> callback) {
        get(
                "/rest/v1/channels?is_private=eq.false"
                        + "&select=id,title,description,owner_id,"
                        + "is_private,invite_token"
                        + "&order=created_at.desc",
                callback
        );
    }

    public void myChannels(Callback<List<Channel>> callback) {
        UserSession user = required();

        get(
                "/rest/v1/channel_members?user_id=eq."
                        + user.userId
                        + "&select=channels(id,title,description,"
                        + "owner_id,is_private,invite_token)",
                callback
        );
    }

    private void get(
            String path,
            Callback<List<Channel>> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONArray rows = api.request(
                        "GET",
                        path,
                        user.accessToken,
                        null
                ).optJSONArray("rows");

                List<Channel> channels = new ArrayList<>();

                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);

                        JSONObject channel = row == null
                                ? null
                                : (row.optJSONObject("channels") != null
                                    ? row.optJSONObject("channels")
                                    : row);

                        if (channel != null) {
                            channels.add(channel(channel));
                        }
                    }
                }

                callback.success(channels);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    public void create(
            String title,
            String description,
            boolean isPrivate,
            Callback<Channel> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONObject body = new JSONObject();
                body.put("title", title);
                body.put("description", description);
                body.put("is_private", isPrivate);
                body.put("owner_id", user.userId);

                JSONObject result = first(
                        api.request(
                                "POST",
                                "/rest/v1/channels",
                                user.accessToken,
                                body
                        )
                );

                if (result == null) {
                    throw new IllegalStateException(
                            "Channel creation failed");
                }

                JSONObject membership = new JSONObject();
                membership.put("channel_id", result.optString("id"));
                membership.put("user_id", user.userId);
                membership.put("role", "owner");

                api.request(
                        "POST",
                        "/rest/v1/channel_members",
                        user.accessToken,
                        membership
                );

                callback.success(channel(result));
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    public void follow(
            String id,
            Callback<Void> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONObject body = new JSONObject();
                body.put("channel_id", id);
                body.put("user_id", user.userId);
                body.put("role", "subscriber");

                api.request(
                        "POST",
                        "/rest/v1/channel_members",
                        user.accessToken,
                        body
                );

                callback.success(null);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    public void posts(
            String id,
            Callback<List<ChannelPost>> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONArray rows = api.request(
                        "GET",
                        "/rest/v1/channel_posts?channel_id=eq."
                                + id
                                + "&select=id,channel_id,author_id,"
                                + "body,created_at"
                                + "&order=created_at.asc",
                        user.accessToken,
                        null
                ).optJSONArray("rows");

                List<ChannelPost> posts = new ArrayList<>();

                if (rows != null) {
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject row = rows.optJSONObject(i);

                        if (row != null) {
                            posts.add(post(row));
                        }
                    }
                }

                callback.success(posts);
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    public void publish(
            String id,
            String body,
            Callback<ChannelPost> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONObject requestBody = new JSONObject();
                requestBody.put("channel_id", id);
                requestBody.put("author_id", user.userId);
                requestBody.put("body", body);

                JSONObject result = first(
                        api.request(
                                "POST",
                                "/rest/v1/channel_posts",
                                user.accessToken,
                                requestBody
                        )
                );

                if (result == null) {
                    throw new IllegalStateException("Post failed");
                }

                callback.success(post(result));
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    public void createInvite(
            String id,
            Callback<String> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONObject body = new JSONObject();
                body.put("channel_id", id);
                body.put("created_by", user.userId);

                JSONObject result = first(
                        api.request(
                                "POST",
                                "/rest/v1/channel_invites",
                                user.accessToken,
                                body
                        )
                );

                if (result == null) {
                    throw new IllegalStateException("Invite failed");
                }

                callback.success(result.optString("token"));
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    public void joinByInvite(
            String token,
            Callback<Channel> callback) {

        AppExecutors.network().execute(() -> {
            try {
                UserSession user = required();

                JSONObject body = new JSONObject();
                body.put("token", token);
                body.put("user_id", user.userId);

                JSONObject result = first(
                        api.request(
                                "POST",
                                "/rest/v1/channel_invites/redeem",
                                user.accessToken,
                                body
                        )
                );

                if (result == null) {
                    throw new IllegalStateException("Invite invalid");
                }

                callback.success(channel(result));
            } catch (Exception error) {
                callback.error(error);
            }
        });
    }

    private void write(
            String path,
            JSONObject body,
            Callback<Void> callback) {

        AppExecutors.network().execute(() -> {
            try {
                api.request(
                        "POST",
                        path,
                        required().accessToken,
                        body
                );

                callback.success(null);
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

        return rows == null || rows.length() == 0
                ? null
                : rows.optJSONObject(0);
    }

    private Channel channel(JSONObject json) {
        return new Channel(
                json.optString("id"),
                json.optString("title"),
                json.optString("description"),
                json.optString("owner_id"),
                json.optBoolean("is_private"),
                json.optString("invite_token")
        );
    }

    private ChannelPost post(JSONObject json) {
        return new ChannelPost(
                json.optString("id"),
                json.optString("channel_id"),
                json.optString("author_id"),
                json.optString("body"),
                json.optString("created_at")
        );
    }
                    }
