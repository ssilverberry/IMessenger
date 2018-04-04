package com.group42.server.model;

public class ChatRoom {
    private int room_id;
    private String fromUser;
    private String toUser;
    private String room_name;

    public ChatRoom(int id, String room_name) {
        room_id = id;
        this.room_name = room_name;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
}
