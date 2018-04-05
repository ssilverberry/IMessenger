package com.group42.client.network.protocol;

import java.util.List;

/**
 * Modified by Yura 23.03.18
 */
public class OutputClientMessage {

    private Integer actionId;
    private String firstName, lastName, phoneNumber, dateOfBirth, email, login, password;
    private String fromUser, toUser, msgBody;
    private String groupName;
    private List<String> members;


    /*************************************************************************

    1 - Authorisation
        request: 1,  "login", "password"

    2 - Registration
        request: 2, "email", "login", "password"

    3 - Main chat:
        request: 31                                     - get online users
        request: 32  "fromUser", "message"              - message to general chat.

        request: 33  "fromUser", "toUser", "message"    - message to private
        request: 34  "fromUser", "toUser"               - create private chat
        request: 35  "fromUser"                         - log out
        request: 36  "groupName"                        - get general chat history
        request: 37 "groupName", List<String> members   - create group room
        request: 38 "login"                             - get user info
        request: 39: "groupName", "fromUser", "message" - message to group

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
     * Request from main chat window to send message from current user to general chat
     *
     * @param fromUser - user name of writer
     * @param actionId - id operation for send message to general chat request, use <tt>32</tt>.
     * @param msgBody - content of message
     */
    public OutputClientMessage(String groupName, String fromUser, Integer actionId, String msgBody) {
        this.groupName = groupName;
        this.fromUser = fromUser;
        this.actionId = actionId;
        this.msgBody = msgBody;
    }

    /**
     * Request from main chat window to send message from current user to private chat
     *
     * @param fromUser - user name of writer
     * @param toUser - user name of consumer
     * @param msgBody - content of message
     * @param actionId - id operation for send message to private chat request, use <tt>33</tt>.
     */
    public OutputClientMessage(String fromUser, String toUser, String msgBody, Integer actionId) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.msgBody = msgBody;
        this.actionId = actionId;
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
     * Request from main window to create group chat, use <tt>37</tt>
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
     * Request from main window to send message to group chat, use <tt>39</tt>
     * @param groupName
     * @param fromUser
     * @param msgBody
     */
    public OutputClientMessage(Integer actionId, String groupName, String fromUser, String msgBody) {
        this.actionId = actionId;
        this.groupName = groupName;
        this.fromUser = fromUser;
        this.msgBody = msgBody;
    }

    /**
     * Request from main window to leave group, uses <tt>40</tt>
     * @param groupName
     * @param actionId
     * @param login
     */
    public OutputClientMessage(String groupName, Integer actionId, String login, Integer action) {
        this.groupName = groupName;
        this.actionId = actionId;
        this.login = login;
    }

    /**
     * Request from main chat window to log out.
     *
     * @param actionId - id operation to  log out request, use <tt>35</tt>.
     * @param fromUser - user name which wanna log out.
     */
    public OutputClientMessage(Integer actionId, String fromUser) {
        this.actionId = actionId;
        this.fromUser = fromUser;
    }
}
