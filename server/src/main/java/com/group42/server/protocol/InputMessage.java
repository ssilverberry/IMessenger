package com.group42.server.protocol;

import java.time.LocalDate;
import java.util.List;
/*************************************************************************
 1 - Authorisation
 request: 1,  "login", "password"
 2 - Registration
 request: 2, "email", "login", "password"
 3 - Main chat:
 request: 31                                     - get online users
 request: 32  "fromUser", "message"              - send message to chat.
 request: 34  "fromUser", "toUser"               - create private chat
 request: 35  "fromUser"                         - log out and get chat list.
 request: 36  "groupName"                        - get general chat history
 request: 37 "groupName", List<String> members   - create group room
 request: 38 "login"                             - get user info
 *************************************************************************
 *
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
 **************************************************************************/
public class InputMessage {
    private String groupName;
    private List<String> members;
    private Integer actionId;
    private String login, fromUser, toUser, password, email, msgBody, chatName;
    private String firstName, lastName, phoneNumber;
    private LocalDate dateOfBirth;
    private List<String> onlineUsers, chatList;
    private LocalDate localDate;
    private Integer chatId;
    private Integer isPrivate;
    /**
     * Authorisation request constructor
     *
     * @param actionId - id operation for authorisation, use <tt>1</tt>.
     * @param login - user name to sign in
     * @param password - password to sign in
     */
    public InputMessage(Integer actionId, String login, String password) {
        this.actionId = actionId;
        this.login = login;
        this.password = password;
    }
    /**
     * Request from main chat window to get all online users
     *
     * @param actionId - id operation for get online user request, use <tt>31</tt>.
     */
    public InputMessage(Integer actionId) {
        this.actionId = actionId;
    }

    /**
     * Registration request constructor
     *
     * @param actionId - id operation for registration, use <tt>2</tt>.
     * @param email - email to sign up
     * @param login - user name to sign up
     * @param password - password to sign up
     */
    public InputMessage(Integer actionId, String firstName, String lastName, String phoneNumber,
                        LocalDate dateOfBirth, String email, String login, String password) {
        this.actionId = actionId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.login = login;
        this.password = password;
    }
    /**
     * Request from main chat window to create private chat
     *
     * @param fromUser - user name of writer
     * @param toUser - user name of consumer
     * @param actionId - id operation to  create private chat request, use <tt>34</tt>.
     */
    public InputMessage(String fromUser, String toUser, Integer actionId) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.actionId = actionId;
    }
    /**
     * Request from main chat window to send message from current user to private chat
     *
     * @param fromUser - user name of writer
     * @param msgBody - content of message
     * @param actionId - id operation for send message to private chat request, use <tt>33</tt>.
     */
    public InputMessage(String fromUser, Integer actionId, String msgBody, LocalDate localDate) {
        this.fromUser = fromUser;
        this.actionId = actionId;
        this.msgBody = msgBody;
        this.localDate = localDate;
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

    /**
     * Request from main chat window to get user info, use <tt>38</tt>
     * @param login
     * @param actionId
     */
    public InputMessage (String login, Integer actionId) {
        this.login = login;
        this.actionId = actionId;
    }
    /**
     * Request from main window to create group chat, use <tt>37</tt>
     * @param actionId
     * @param groupName
     * @param members
     */
    public InputMessage(Integer actionId, String groupName, List<String> members) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.members = members;
    }

    /**
     * For deleting current user from group.
     * @param chatId It is a String, group name from where you want delete a user;
     * @param actionId
     * @param login
     */
    public InputMessage(Integer chatId, String login, Integer actionId) {
        this.chatId = chatId;
        this.actionId = actionId;
        this.login = login;
    }

    /**
     * get chat history request.
     * @param chatId
     * @param actionId
     * @param fromUser
     */
    public InputMessage(Integer chatId, Integer actionId, String fromUser){
        this.chatId = chatId;
        this.actionId = actionId;
        this.fromUser = fromUser;
    }
    public InputMessage (String login, Integer actionId, Integer chatId) {
        this.login = login;
        this.actionId = actionId;
        this.chatId = chatId;
    }
    /////////////////////////////

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

    public LocalDate getBirthday() {
        return dateOfBirth;
    }

    public void setBirthday(LocalDate birthday) {
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

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Integer getChatId() {
        return chatId;
    }
}


