package com.group42.server.model;

import java.util.List;

/******************************************************************************************
 +      Authorisation
 +            response: 11, "login"                       - authorisation successful
 +            response: 12                                - authorisation failed
 +      Registration
 +            response: 21                                - registration successful
 +            response: 22                                - registration failed
 +      Main chat:
 +            response: 311  List<String> onlineUsers     - returns list of users
 +            response: 312                               - no online users
 +            response: 32  "fromUser", "message"         - message to all
 +            response: 33  "fromUser", "message"         - message to private
 +            response: 34  "toUser"                      - create private chat
 +
 *******************************************************************************************/

/**
 * New Version Protocol.
 *
 * This class is using for getting
 * incoming messages from client
 *
 * It is full of overloaded constructors
 * for different interactions between server
 * and client.
 *
 * Input message for server is an output message
 * for client.
 **/
public class InputMessage {
    private String groupName;
    private List<String> members;
    private Integer actionId;
    private String login, fromUser, toUser, password, email, msgBody, chatName;
    private String firstName, lastName, dateOfBirth, phoneNumber;
    private List<String> onlineUsers, chatList;

    public InputMessage(Integer actionId, String login, String password) {
        this.actionId = actionId;
        this.login = login;
        this.password = password;
    }

    public InputMessage(Integer actionId) {
        this.actionId = actionId;
    }

    public InputMessage(Integer actionId, String firstName, String lastName, String phoneNumber,
                               String dateOfBirth, String email, String login, String password) {
        this.actionId = actionId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public InputMessage(String fromUser, String toUser, Integer actionId) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.actionId = actionId;
    }

    public InputMessage(String fromUser, Integer actionId, String msgBody) {
        this.fromUser = fromUser;
        this.actionId = actionId;
        this.msgBody = msgBody;
    }

    public InputMessage(String fromUser, String toUser, String msgBody, Integer actionId) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.msgBody = msgBody;
        this.actionId = actionId;
    }

    public InputMessage(Integer actionId, String fromUser) {
        this.actionId = actionId;
        this.fromUser = fromUser;
    }

    public InputMessage (String login, Integer actionId) {
        this.login = login;
        this.actionId = actionId;
    }

    public InputMessage(Integer actionId, String groupName, List<String> members) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.members = members;
    }

    /**
     * For deleting current user from group.
     * @param groupName
     * @param actionId
     * @param login
     * @param action
     */
    public InputMessage(String groupName, Integer actionId, String login, Integer action) {
        this.groupName = groupName;
        this.actionId = actionId;
        this.login = login;
    }
    public Integer getActionId() {
        return actionId;
    }

    public String getLogin() {
        return login;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public List<String> getOnlineUsers() {
        return onlineUsers;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getChatName () {
        return groupName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return lastName;
    }

    public void setSecondName(String secondName) {
        this.lastName = secondName;
    }

    public String getBirthday() {
        return dateOfBirth;
    }

    public void setBirthday(String birthday) {
        this.dateOfBirth = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getUserChatList() {
        return members;
    }
}


