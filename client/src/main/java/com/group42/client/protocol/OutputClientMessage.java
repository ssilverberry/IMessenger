package com.group42.client.protocol;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Modified by Yura 04.04.18
 */
public class OutputClientMessage {

    private String groupName;
    private Integer chatId;
    private LocalDateTime date;
    private Integer actionId;
    private String firstName, lastName, phoneNumber, email, login, password;
    private LocalDate dateOfBirth;
    private String fromUser, toUser, msgBody;
    private List<String> members;


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

     *************************************************************************/

    /**
     * Authorisation request constructor
     *
     * @param actionId - id operation for authorisation, use <tt>1</tt>.
     * @param login - user name to sign in
     * @param password - password to sign in
     */
    public OutputClientMessage(Integer actionId, String login, String password) {
        this.actionId = actionId;
        this.login = login;
        this.password = password;
    }

    /**
     * Registration request constructor
     *
     * @param actionId - id operation for registration, use <tt>2</tt>.
     * @param email - email to sign up
     * @param login - user name to sign up
     * @param password - password to sign up
     */
    public OutputClientMessage(Integer actionId, String firstName, String lastName, String phoneNumber,
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
     * Request from main chat window to get all online users
     *
     * @param actionId - id operation for get online user request, use <tt>31</tt>.
     */
    public OutputClientMessage(Integer actionId) {
        this.actionId = actionId;
    }

    /**
     * Request from main chat window to create private chat
     *
     * @param fromUser - user name of writer
     * @param toUser - user name of consumer
     * @param actionId - id operation to  create private chat request, use <tt>34</tt>.
     */
    public OutputClientMessage(String fromUser, String toUser, Integer actionId) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.actionId = actionId;
    }

    /**
     * Request from main chat window to send message from current user to general chat (WORKING!)
     *
     * @param fromUser - user name of writer
     * @param actionId - id operation for send message to general chat request, use <tt>32</tt>.
     * @param msgBody - content of message
     */
    public OutputClientMessage(Integer chatId, String fromUser, Integer actionId, String msgBody) {
        this.chatId = chatId;
        this.fromUser = fromUser;
        this.actionId = actionId;
        this.msgBody = msgBody;
    }

    /**
     * Request from main chat window to get user info, use <tt>38</tt>
     * @param login
     * @param actionId
     */
    public OutputClientMessage(String login, Integer actionId) {
        this.login = login;
        this.actionId = actionId;
    }

    /**
     * Request from main window to get chat history, use <tt>37</tt>
     * @param actionId
     * @param groupName
     * @param members
     */
    public OutputClientMessage(Integer actionId, String groupName, List<String> members) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.members = members;
    }

    /**
     * Request from main window to leave group, uses <tt>40</tt>
     * @param chatId
     * @param actionId
     * @param login
     */
    public OutputClientMessage(Integer chatId, String login, Integer actionId) {
        this.chatId = chatId;
        this.actionId = actionId;
        this.login = login;
    }

    /**
     * Request for get chat history, uses <tt>36</tt>
     * And add member to group <tt>42</tt>.
     * @param chatId
     * @param actionId
     * @param fromUser
     */
    public OutputClientMessage(Integer chatId, Integer actionId, String fromUser){
        this.chatId = chatId;
        this.actionId = actionId;
        this.fromUser = fromUser;
    }

    /**
     * Request from main chat window to log out or get chatList) uses<tt>35 or 43</tt>
     *
     * @param actionId - id operation to  log out request, use <tt>35</tt>.
     * @param fromUser - user name which wanna log out.
     */
    public OutputClientMessage(Integer actionId, String fromUser) {
        this.actionId = actionId;
        this.fromUser = fromUser;
    }
}
