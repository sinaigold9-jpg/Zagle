package com.zajel.app.data;

import com.zajel.app.domain.*;
import java.util.List;

public interface ChannelRepository {
    void publicChannels(Callback<List<Channel>> cb);
    void myChannels(Callback<List<Channel>> cb);
    void create(String title, String description, boolean isPrivate, Callback<Channel> cb);
    void follow(String channelId, Callback<Void> cb);
    void posts(String channelId, Callback<List<ChannelPost>> cb);
    void publish(String channelId, String body, Callback<ChannelPost> cb);
    void createInvite(String channelId, Callback<String> cb);
    void joinByInvite(String token, Callback<Channel> cb);
    interface Callback<T> { void success(T value); void error(Exception error); }
}
