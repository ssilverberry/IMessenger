package com.group42.server.model;

import java.util.ArrayList;
import java.util.List;

/***********************************************************************************
+    private Integer id
+
+    1 - Authorisation
+        request: 1,  "login", "password"
+
+    2 - Registration
+        request: 2, "email", "login", "password"
+
+    3 - Main chat:
+        request: 31                                     - get online users
+        request: 32  "fromUser", "message"              - message to all
+        request: 33  "fromUser", "toUser", "message"    - message to private
+        request: 34  "fromUser", "toUser"               - create private chat
+        request: 35  "fromUser"                         - log out
+
+     *******************************************************************************/
/**
 * New Version Protocol.
 *
 * Here is class witch is used for creating
 * output message for client.
 *
 * In other words it is a "server response" for client
 **/
public class OutMessage {
    private List<String> members;
    private String groupName;
    private Integer actionId;
    private String email, login, password, firstName, lastName, birthday, phoneNumber;
    private String fromUser, toUser, msgBody;
    private ArrayList<String> onlineUsers;
    private List<String> generalChatHistory;
    private ChatMessages chatHistory;
    private String chat;

    public OutMessage(Integer actionId) {
        this.actionId = actionId;
    }

    public OutMessage(Integer actionId, String login) {
        this.actionId = actionId;
        this.login = login;
    }
    public OutMessage(String toUser, Integer actionId) {
        this.actionId = actionId;
        this.toUser = toUser;
    }
    public OutMessage (Integer actionId, ArrayList<String> onlineUsers) {
        this.actionId = actionId;
        this.onlineUsers = onlineUsers;
        this.chat = chat;
        //generalChatHistory = history;
    }
    public OutMessage (Integer actionId, ChatMessages chatHistory) {
        this.actionId = actionId;
        this.chatHistory = chatHistory;
    }
    public OutMessage(Integer actionId, String fromUser, String msgBody) {
        this.actionId = actionId;
        this.fromUser = fromUser;
        this.msgBody = msgBody;
    }

    public OutMessage(String fromUser, String toUser, String msgBody, Integer actionId){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.msgBody = msgBody;
        this.actionId = actionId;
    }
    public OutMessage(String fromUser, Integer actionId, String msgBody){
        this.fromUser = fromUser;
        this.msgBody = msgBody;
        this.actionId = actionId;
    }

    public OutMessage(Integer actionId, String email, String firstName,
                      String secondName, String birthday, String phoneNumber){
        this.actionId = actionId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = secondName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }
    public OutMessage (Integer actionId, String groupName, List<String> members) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.members = members;
    }
    public OutMessage ( List<String> historyList, Integer actionId, String groupName, List<String> members) {
        this.actionId = actionId;
        this.generalChatHistory = historyList;
        this.groupName = groupName;
        this.members = members;
    }
    public OutMessage (String groupName, Integer actionId, String fromUser, String msgBody){
        this.groupName = groupName;
        this.actionId = actionId;
        this.fromUser = fromUser;
        this.msgBody = msgBody;
    }
    public OutMessage (String groupName, Integer actionId, List<String> members) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.members = members;
    }
}

