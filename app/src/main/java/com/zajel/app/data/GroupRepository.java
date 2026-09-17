package com.zajel.app.data;

import com.zajel.app.domain.Group;
import com.zajel.app.domain.GroupMember;
import java.util.List;

public interface GroupRepository {
    void myGroups(Callback<List<Group>> cb);
    void createGroup(String title, String description, String avatarUrl, boolean isPrivate, Callback<Group> cb);
    void members(String groupId, Callback<List<GroupMember>> cb);
    void addMember(String groupId, String userId, String role, Callback<GroupMember> cb);

    interface Callback<T> {
        void success(T value);
        void error(Exception error);
    }
}
