package com.zajel.app.domain;

public final class GroupMember {
    public final String id;
    public final String groupId;
    public final String userId;
    public final String role;

    public GroupMember(String id, String groupId, String userId, String role) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
    }
}
