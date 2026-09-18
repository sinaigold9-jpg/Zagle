package com.zajel.app.data;

import com.zajel.app.domain.*;
import java.util.List;

public interface CommunityRepository {
    void myCommunities(Callback<List<Community>> cb);
    void create(String title, String description, Callback<Community> cb);
    void addGroup(String communityId, String groupId, Callback<Void> cb);
    void addChannel(String communityId, String channelId, Callback<Void> cb);
    void linkedGroups(String communityId, Callback<List<String>> cb);
    void linkedChannels(String communityId, Callback<List<String>> cb);
    interface Callback<T> { void success(T value); void error(Exception error); }
}
