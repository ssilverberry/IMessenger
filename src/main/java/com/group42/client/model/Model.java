package com.group42.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.text.Text;

public class Model {

    /**
     * instance of current user.
     */
    private User user;

    /**
     * list of online chat users.
     */
    private ObservableList<String> onlineUserList;

    /**
     * Map with key by chat and values by list of text messages
     */
    private ObservableMap<Chat, ObservableList<Text>> chatHistoryMap;

    /**
     *
     */
    private ObservableMap<Chat, ObservableList<String>> chatUsersMap;

    private static final Model instance = new Model();

    public static Model getInstance() {
        return instance;
    }

    private Model(){
        onlineUserList = FXCollections.observableArrayList();
        chatHistoryMap = FXCollections.observableHashMap();
        chatUsersMap = FXCollections.observableHashMap();
    }

    public void setUser(String login){
        user = new User(login);
    }

    public User getUser() {
        return user;
    }

    public ObservableList<String> getOnlineUserList() {
        return onlineUserList;
    }

    public void setOnlineUserList(ObservableList<String> onlineUserList) {
        this.onlineUserList = onlineUserList;
    }

    public ObservableMap<Chat, ObservableList<Text>> getChatHistoryMap() {
        return chatHistoryMap;
    }

    public ObservableMap<Chat, ObservableList<String>> getChatUsersMap() {
        return chatUsersMap;
    }

    public void setChatHistoryMap(ObservableMap<Chat, ObservableList<Text>> chatHistoryMap) {
        this.chatHistoryMap = chatHistoryMap;
    }

    public void setChatUsersMap(ObservableMap<Chat, ObservableList<String>> chatUsersMap) {
        this.chatUsersMap = chatUsersMap;
    }
}
