package com.group42.server.protocol;

import com.group42.server.model.Chat;
import com.group42.server.model.ChatMessages;

import java.time.LocalDate;
import java.util.List;

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
    private String email, login, password, firstName, lastName, phoneNumber;
    private String fromUser, toUser, msgBody;
    private List<String> onlineUsers, offlineUsers;
    private List<String> generalChatHistory, usrChatsList;
    private Chat[] localUsrChatList;
    private ChatMessages chatHistory;
    private String chat;
    private LocalDate birthday;
    private Integer chatId;

    /**
     * Constructor for sending only one action id.
     *
     * @param actionId
     */
    public OutMessage(Integer actionId) {
        this.actionId = actionId;
    }

    /**
     * Constructor for response if private chat was removed.
     *
     * @param actionId
     */
    public OutMessage(Integer chatId, Integer actionId) {
        this.chatId = chatId;
        this.actionId = actionId;
    }

    /**
     * @param actionId
     */
    public OutMessage(Integer actionId, Integer chatId, List<String> members) {
        this.chatId = chatId;
        this.actionId = actionId;
        this.members = members;
    }

    /**
     * The method is used In case of authorization.
     *
     * @param actionId
     * @param login
     */
    public OutMessage(Integer actionId, String login) {
        this.actionId = actionId;
        this.login = login;
    }

    /**
     * Create private chat
     *
     * @param toUser   - username with whom private chat will be created
     * @param actionId - id operation for this <tt>34</tt>
     */
    public OutMessage(String toUser, Integer actionId, Integer chatId) {
        this.actionId = actionId;
        this.toUser = toUser;
        this.chatId = chatId;
    }

    /**
     * This constructor is used in case of requesting online and offline user lists.
     * @param actionId
     * @param onlineUsers
     * @param offlineUsers
     */
    public OutMessage(Integer actionId, List<String> onlineUsers, List<String> offlineUsers) {
        this.actionId = actionId;
        this.onlineUsers = onlineUsers;
        this.offlineUsers = offlineUsers;
    }

    /**
     * Constructor is used in case of sending message,-s in private chat
     *
     * @param fromUser
     * @param toUser
     * @param msgBody
     * @param actionId
     */
    public OutMessage(String fromUser, String toUser, String msgBody, Integer actionId) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.msgBody = msgBody;
        this.actionId = actionId;
    }

    /**
     * The method is used in case of registration.
     *
     * @param actionId
     * @param email
     * @param firstName
     * @param secondName
     * @param birthday
     * @param phoneNumber
     */
    public OutMessage(Integer actionId, String email, String firstName,
                      String secondName, LocalDate birthday, String phoneNumber) {
        this.actionId = actionId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = secondName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    /**
     * The constructor is used in case of creating group.
     *
     * @param actionId
     * @param groupName
     * @param members
     * @param chatId
     */
    public OutMessage(Integer actionId, String groupName, List<String> members, Integer chatId) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.members = members;
        this.chatId = chatId;
    }

    /**
     * The constructor is used in case of sending message to some group.
     *
     * @param groupName
     * @param actionId
     * @param fromUser
     * @param msgBody
     */
    public OutMessage(String groupName, Integer actionId, String fromUser, String msgBody) {
        this.groupName = groupName;
        this.actionId = actionId;
        this.fromUser = fromUser;
        this.msgBody = msgBody;
    }

    /**
     * Send chat history from database for selected chat.
     *
     * @param historyList
     * @param actionId
     * @param chatId
     * @param members
     */
    public OutMessage(List<String> historyList, Integer actionId, Integer chatId, List<String> members) {
        this.generalChatHistory = historyList;
        this.actionId = actionId;
        this.chatId = chatId;
        this.members = members;
    }

    /**
     * send updated chat list, uses <tt>43</tt>
     *
     * @param actionId
     * @param usrChatsList
     */
    public OutMessage(Integer actionId, Chat[] usrChatsList) {
        this.actionId = actionId;
        localUsrChatList = usrChatsList;
    }

    /**
     * Constructor for preparing data for sending message, its author to client.
     *
     * @param chatId   chat id
     * @param fromUser message author
     * @param actionId action id
     * @param msgBody  message
     */
    public OutMessage(Integer chatId, String fromUser, Integer actionId, String msgBody) {
        this.chatId = chatId;
        this.fromUser = fromUser;
        this.actionId = actionId;
        this.msgBody = msgBody;
    }
}

