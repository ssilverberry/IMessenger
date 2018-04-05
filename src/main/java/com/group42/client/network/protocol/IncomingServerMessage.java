package com.group42.client.network.protocol;

import com.group42.client.model.ChatMessages;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified by Yura on 22.03.2018.
 */
public class IncomingServerMessage {

   private Integer roomId;
   private Integer actionId;
   private String login, fromUser, toUser, msgBody;
   private String groupName;
   private List<String> members;
   private List<String> onlineUsers;
   private List<String> generalChatHistory;
   private String email, firstName, lastName, birthday, phoneNumber;

   /******************************************************************************************
      Authorisation
            response: 11, "login"                           - authorisation successful
            response: 12                                    - authorisation failed

      Registration
            response: 21                                    - registration successful
            response: 22                                    - registration failed

      Main chat:

            response: 311  List<String> onlineUsers,
                           List<String> chatHistory       - returns list of online users and general chat history

            response: 312                                   - no online users
            response: 32  "fromUser", "message"             - message to general chat

            response: 33  "fromUser", "message"             - message to private
            response: 34  "toUser"                          - create private chat
            response: 36   List<String> generalChatHistory  - get general history
            response: 37  "groupName", List<String> members - create group chat

            response: 38   String email, String firstName,
                           String secondName, String birthday,
                           String phoneNumber               - get user info
            response: 39   "groupName", "fromUser", "msgBody" - message to group chat
    ******************************************************************************************/

   public IncomingServerMessage() {
   }

   /**
    * Successful registration response
    */
   public IncomingServerMessage(Integer actionId, String login) {
      this.actionId = actionId;
      this.login = login;
   }

   /**
    * Receive next responses: id = 12, id = 21, id = 22, id = 312.
    *
    * @param actionId - id operation
    */
   public IncomingServerMessage(Integer actionId) {
      this.actionId = actionId;
   }

   /**
    * Receive list of online users
    *
    * @param actionId - 311
    * @param onlineUsers - list if user names
    */
   public IncomingServerMessage(Integer actionId, List<String> onlineUsers) {
      this.actionId = actionId;
      this.onlineUsers = FXCollections.observableArrayList();
      this.onlineUsers = onlineUsers;
      this.generalChatHistory = generalChatHistory;
   }

   /**
    * Receive group history list, uses <tt>36</tt>
    * @param historyList
    * @param actionId
    */
   public IncomingServerMessage(List<String> historyList, Integer actionId, String groupName, List<String> members){
      this.generalChatHistory = historyList;
      this.actionId = actionId;
      this.groupName = groupName;
      this.members = members;
   }

   /**
    * Receive message from general chat
    *
    * @param actionId - 32
    * @param fromUser - user name of writer
    * @param msgBody - content of message
    */
   public IncomingServerMessage(Integer actionId, String fromUser, String msgBody){
      this.actionId = actionId;
      this.fromUser = fromUser;
      this.msgBody = msgBody;
   }

   /**
    * Receive message from private chat
    *
    * @param fromUser - user name of writer
    * @param msgBody - content of message
    * @param actionId - 33
    */
   public IncomingServerMessage(String fromUser, String toUser, String msgBody, Integer actionId){
      this.fromUser = fromUser;
      this.toUser = toUser;
      this.msgBody = msgBody;
      this.actionId = actionId;
   }

   /**
    * Create private chat
    *
    * @param toUser - username with whom private chat will be created
    * @param actionId - id operation for this <tt>34</tt>
    */
   public IncomingServerMessage(String toUser, Integer actionId){
       this.toUser = toUser;
       this.actionId = actionId;
   }

   /**
    * Receive user info, uses id <tt>38</tt>
    * @param actionId
    * @param email
    * @param firstName
    * @param secondName
    * @param birthday
    * @param phoneNumber
    */
   public IncomingServerMessage(Integer actionId, String email, String firstName,
                                String secondName, String birthday, String phoneNumber){
      this.actionId = actionId;
      this.email = email;
      this.firstName = firstName;
      this.lastName = secondName;
      this.birthday = birthday;
      this.phoneNumber = phoneNumber;
   }

   /**
    * Create group room, uses id <tt>37</tt>
    * @param actionId
    * @param groupName
    * @param members
    */
   public IncomingServerMessage(Integer actionId, String groupName, List<String> members) {
      this.actionId = actionId;
      this.groupName = groupName;
      this.members = members;
   }

   /**
    * Receive msg to group chat, uses<tt>39</tt>
    * @param groupName
    * @param actionId
    * @param fromUser
    * @param msgBody
    */
   public IncomingServerMessage(String groupName, Integer actionId, String fromUser, String msgBody){
      this.groupName = groupName;
      this.actionId = actionId;
      this.fromUser = fromUser;
      this.msgBody = msgBody;
   }

   /**
    * Update list of members in group chat after someone left group
    * or join in group, uses<tt>40</tt>
    * @param groupName
    * @param members
    */
   public IncomingServerMessage(List<String> members, String groupName, Integer actionId) {
      this.actionId = actionId;
      this.groupName = groupName;
      this.members = members;
   }

   public String getGroupName() {
      return groupName;
   }

   public List<String> getMembers() {
      return members;
   }

   public List<String> getGeneralChatHistory() {
      return generalChatHistory;
   }

   public String getLogin() {
      return login;
   }

   public String getFromUser() {
      return fromUser;
   }

   public String getMsgBody() {
      return msgBody;
   }

   public List<String> getOnlineUsers() {
      return onlineUsers;
   }

   public Integer getActionId() {
      return actionId;
   }

    public String getToUser() {
        return toUser;
    }

   public Integer getRoomId() {
      return roomId;
   }

   public String getEmail() {
      return email;
   }

   public String getFirstName() {
      return firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public String getBirthday() {
      return birthday;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }
}
