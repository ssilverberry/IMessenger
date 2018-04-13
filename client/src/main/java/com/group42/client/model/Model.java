package com.group42.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.text.Text;
import java.util.List;

/**
 * This class stores folders with chats, their stories
 * and members, and also creates the current user
 */
public class Model {

    /**
     * instance of current user.
     */
    private User user;

    /**
     * Map with key by chat and values by list of text messages
     */
    private ObservableMap<Chat, ObservableList<Text>> chatHistoryMap;

    /**
     * Map with key by chat and values by list of chat users
     */
    private ObservableMap<Chat, ObservableList<String>> chatUsersMap;

    /**
     * list of online and offline chat users
     */
    private List<String> onlineUsers;
    private List<String> offlineUsers;

    private static final Model instance = new Model();

    public static Model getInstance() {
        return instance;
    }

    /**
     * Constructs object with online/offline users list and
     * maps for storage chats and it's histories and members
     */
    private Model(){
        onlineUsers = FXCollections.observableArrayList();
        offlineUsers = FXCollections.observableArrayList();
        chatHistoryMap = FXCollections.observableHashMap();
        chatUsersMap = FXCollections.observableHashMap();
    }

    /**
     * setters and getters
     * @param login
     */
    public void setUser(String login){
        user = new User(login);
    }

    public User getUser() {
        return user;
    }

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public List<String> getOfflineUsers() {
        return offlineUsers;
    }

    public void setOfflineUsers(List<String> offlineUsers) {
        this.offlineUsers = offlineUsers;
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
