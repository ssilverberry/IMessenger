package com.group42.client.protocol;

import com.group42.client.model.Chat;
import java.time.LocalDate;
import java.util.List;

/**
 * Modified by Yura on 04.04.2018.
 */
public class IncomingServerMessage {

   private List<String> offlineUsers;
   private Chat[] localUsrChatList;
   private Integer chatId;
   private Integer actionId;
   private String login, fromUser, toUser, msgBody;
   private String groupName;
   private List<String> members;
   private List<String> onlineUsers;
   private List<String> generalChatHistory;
   private String email, firstName, lastName, phoneNumber;
   private LocalDate birthday;

   /******************************************************************************************
      Authorisation
            response: 11, "login"                           - authorisation successful
            response: 12                                    - authorisation failed

      Registration
            response: 21                                    - registration successful
            response: 22                                    - registration failed

      Main chat:

            response: 311  List<String> onlineUsers,
                           List<String> offlineUsers            - returns list of online/offline users

            response: 32  "fromUser", "message"                 - message to chat
            response: 34  "toUser"                              - create private chat
            response: 36   List<String> historyList, members    - get chat history and chat users.
            response: 37  "groupName", List<String> members     - create group chat

            response: 38   String email, String firstName,
                           String secondName, String birthday,
                           String phoneNumber                   - get user info

            response: 43   Chat[] localUsrChatList              - get chat list
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
    * Receive list of online/offline users
    *
    * @param actionId - 311
    * @param onlineUsers - list if user names
    */
   public IncomingServerMessage(Integer actionId, List<String> onlineUsers, List<String> offlineUsers) {
      this.actionId = actionId;
      this.onlineUsers = onlineUsers;
      this.offlineUsers = offlineUsers;
   }

   /**
    * Receive group history list, uses <tt>36</tt>
    * @param historyList
    * @param actionId
    */
   public IncomingServerMessage(List<String> historyList, Integer actionId, Integer chatId, List<String> members){
      this.generalChatHistory = historyList;
      this.actionId = actionId;
      this.chatId = chatId;
      this.members = members;
   }

   /**
    * Receive message from chat
    *
    * @param actionId - 32
    * @param fromUser - who write msg
    * @param msgBody - what write in msg
    */
   public IncomingServerMessage(Integer actionId, Integer chatId, String fromUser, String msgBody){
      this.actionId = actionId;
      this.chatId = chatId;
      this.fromUser = fromUser;
      this.msgBody = msgBody;
   }

   /**
    * Create private chat
    *
    * @param toUser - username with whom private chat will be created
    * @param actionId - id operation for this <tt>34</tt>
    */
   public IncomingServerMessage(String toUser, Integer actionId, Integer chatId){
       this.toUser = toUser;
       this.actionId = actionId;
       this.chatId = chatId;
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
                                String secondName, LocalDate birthday, String phoneNumber){
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
   public IncomingServerMessage(Integer actionId, String groupName, Integer chatId, List<String> members) {
      this.actionId = actionId;
      this.groupName = groupName;
      this.chatId = chatId;
      this.members = members;
   }

   /**
    * Update list of members in group chat after someone left group
    * or join in group, uses<tt>40</tt>
    * @param chatId
    * @param members
    */
   public IncomingServerMessage(Integer chatId, Integer actionId, List<String> members) {
      this.actionId = actionId;
      this.chatId = chatId;
      this.members = members;
   }

   /**
    * TEST LEAVE PRIVATE. uses <tt>41</tt>
    * @param actionId
    * @param chatId
    */
   public IncomingServerMessage(Integer actionId, Integer chatId) {
      this.actionId = actionId;
      this.chatId = chatId;
   }

   /**
    * receive update chat list, uses <tt>43</tt>
    * @param actionId
    * @param localUsrChatList
    */
   public IncomingServerMessage (Integer actionId, Chat[] localUsrChatList) {
      this.actionId = actionId;
      this.localUsrChatList = localUsrChatList;
   }

   public Chat[] getLocalUsrChatList() {
      return localUsrChatList;
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

   public Integer getChatId() {
      return chatId;
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

   public LocalDate getBirthday() {
      return birthday;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public List<String> getOfflineUsers() {
      return offlineUsers;
   }
}
