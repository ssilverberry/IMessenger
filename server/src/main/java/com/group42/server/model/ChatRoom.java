package com.group42.server.model;

public class ChatRoom {
    private String groupName;
    private String username;
    private Integer isPrivate;
    private Integer id;

    public ChatRoom(String groupName, String username, Integer isPrivate) {
        this.groupName = groupName;
        this.username = username;
        this.isPrivate = isPrivate;
    }
    public ChatRoom(Integer id, String username, Integer isPrivate) {
        this.id = id;
        this.username = username;
        this.isPrivate = isPrivate;
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

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
