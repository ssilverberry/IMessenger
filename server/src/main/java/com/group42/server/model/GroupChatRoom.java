package com.group42.server.model;

public class GroupChatRoom {
    private String groupName;
    private String username;

    public GroupChatRoom (String groupName, String username) {
        this.groupName = groupName;
        this.username = username;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
