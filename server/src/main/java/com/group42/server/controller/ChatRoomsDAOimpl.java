package com.group42.server.controller;

import com.group42.server.model.Chat;
import com.group42.server.model.ChatRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * The class is used for interacting with chats' data which we get from database.
 * We used single tone pattern for the class.
 */
public class ChatRoomsDAOimpl {
    private static final String getChatRooms = "SELECT * FROM CHATS WHERE CHAT_NAME='";
    private static final String insertIntoChats = "INSERT INTO CHATS (CHAT_NAME, ISPRIVATE) VALUES (?, ?)";
    private static final String deleteUsrfromChat = "DELETE FROM CHATS WHERE CHAT_NAME='";
    private static final String delUsrFromChatSec = "' AND CHAT_USER_NAME = '";
    private static final String resetQuery = "UPDATE USERS SET USER_STATUS =";
    private static final String insertIntoChatUsersQuery = "INSERT INTO CHAT_USERS (CHAT_ID, USER_ID) VALUES (?, ?)";
    private static final String getChatIdByNameQuery = "SELECT CHAT_ID FROM CHATS WHERE CHAT_NAME='";
    private static final String isPrivateChatByIdQuery = "SELECT ISPRIVATE FROM CHATS WHERE CHAT_ID='";
    private static final String getChatListForUserQuery = "SELECT CHATS.CHAT_ID, CHATS.CHAT_NAME, CHATS.ISPRIVATE " +
            "from CHATS, CHAT_USERS WHERE CHATS.CHAT_ID = CHAT_USERS.CHAT_ID and CHAT_USERS.USER_ID ='" ;
    private static final String getDataForNewChatQuery = "SELECT * FROM CHATS WHERE " +
            "CHAT_ID=(SELECT MAX(CHAT_ID) FROM CHATS)";
    private static final String deletePrivateChatFromChatUsersQuery = "DELETE FROM CHAT_USERS WHERE CHAT_ID='";
    private static final String deleteUserFromChatQuery = "DELETE FROM CHAT_USERS WHERE CHAT_ID='";
    private static final String deleteChatQuery = "DELETE FROM CHATS WHERE CHAT_ID='";
    private static final String getChatNameByIdQuery = "SELECT CHAT_NAME FROM CHATS WHERE CHAT_ID='";

    private static final Logger logger = LogManager.getLogger(UsersDAOimpl.class);
    private static final ChatRoomsDAOimpl instance = new ChatRoomsDAOimpl();
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private Connection connection = DAOHandler.getInstance().getConnection();

    public static ChatRoomsDAOimpl getInstance() {
        if (instance != null)
            return instance;
        else
            return new ChatRoomsDAOimpl();
    }

    private ChatRoomsDAOimpl() {

    }

    /**
     * Here we parse all data which got from database
     *
     * @return users list
     */
    public List<ChatRoom> getChatRooms(String str) {
        List<ChatRoom> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement( getChatRooms + str + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseChatRoom(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Problem with inserting data to group chats table - > str: " + str);
        }
        return list;
    }

    /**
     * Here we insert data to database (into chats db table).
     */
    public void insertIntoChats(String chatRoomName, Integer isprivate) {
        try {
            preparedStatement = connection.prepareStatement(insertIntoChats);
            preparedStatement.setString(1, chatRoomName);
            preparedStatement.setInt(2, isprivate);
            resultSet = preparedStatement.executeQuery();
            getDataForNewChat();
        } catch (SQLException e) {
            logger.info("Inserting to db process failed chatRoom: " + chatRoomName + "; isPrivate: " + isprivate);
        }
    }

    /**
     * The method is used for creating an user instance
     * for future interactions.
     *
     * @param resultSet your result set from db.
     * @return new user with params such as id, email, password, login and status (online / offline)
     */
    private ChatRoom parseChatRoom(ResultSet resultSet) {
        ChatRoom room = null;
        try {
            int id = resultSet.getInt("chat_id");
            String fromuser = resultSet.getString("chat_name");
            Integer isPrivate = resultSet.getInt("isprivate");
            room = new ChatRoom(id, fromuser, isPrivate);
        } catch (SQLException e) {
            logger.info("parseChatRoom process failed");
        }
        return room;
    }

    /**
     * The method is used for updating user status (online / offline)
     * in database according to changes.
     *
     * @param chatname user id (int number) whose status you want to change
     * @param username int number 1 (online) or 0 (offline)
     */
    public void deleteUsrFromChat(String chatname, String username) {
        try {
            preparedStatement = connection.prepareStatement(deleteUsrfromChat + chatname
                    + delUsrFromChatSec + username + "'");
            resultSet = preparedStatement.executeQuery( );
        } catch (SQLException e) {
            logger.error("Deleting user from chat failed");
        }
    }

    /**
     * Here we insert all users (user id) who joined to chat into database according to chat id.
     *
     * @param chat_id chat identificator
     * @param user_id user identificator
     */
    public void insertIntoChatUsers(int chat_id, int user_id) {
        try {
            preparedStatement = connection.prepareStatement(insertIntoChatUsersQuery);
            preparedStatement.setInt(1, chat_id);
            preparedStatement.setInt(2, user_id);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Inserting to db CHATS_USERS failed");
        }
    }

    /**
     * Thanks to the method you are able to get chat id by its name.
     *
     * @param str chat name
     * @return chat id (Integer)
     */
    public int getChatIdByName(String str) {
        int id = 0;
        try {
            preparedStatement = connection.prepareStatement( getChatIdByNameQuery+
                    str + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                id = resultSet.getInt("chat_id");
        } catch (SQLException e) {
            logger.error("Process failed in getChatIdByName chatName: " + str);
        }
        return id;
    }

    /**
     * The method is used for checking if it is a private or not.
     *
     * @param chatId chat id which you want to check.
     * @return true or false
     */
    public boolean isPrivateChatById(Integer chatId) {
        int priv = 0;
        boolean ispriv;
        try {
            preparedStatement = connection.prepareStatement(isPrivateChatByIdQuery + chatId + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                priv = resultSet.getInt("isprivate");
        } catch (SQLException e) {
            logger.error("Process failed in method isPrivateChatById, chatId: " + chatId);
        }
        ispriv = priv == 1;
        return ispriv;
    }

    /**
     * Here we get all chats for current user.
     *
     * @param username username
     * @return object(chat) array
     */
    public Chat[] getChatListForUser(String username) {
        List<Chat> chatList = new ArrayList<>();
        int userId = UsersDAOimpl.getInstance().getUserIdByName(username);

        try {
            preparedStatement = connection.prepareStatement(getChatListForUserQuery + userId + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Integer chatId = resultSet.getInt("chat_id");
                String chatName = resultSet.getString("chat_name");
                String chatType = resultSet.getString("isprivate");
                chatList.add(new Chat(chatId, chatName, chatType));
            }
        } catch (SQLException e) {
            logger.debug("Process failed: getChatListForUser()");
        }
        Chat[] chatArray = new Chat[chatList.size()];
        for (int i = 0; i < chatArray.length; i++) {
            chatArray[i] = chatList.get(i);
        }
        return chatArray;
    }

    /**
     * The method is used for getting data about just created chat from database.
     *
     * @return chat object
     */
    private void getDataForNewChat() {
        Chat chat = null;
        try {
            preparedStatement = connection.prepareStatement(getDataForNewChatQuery);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                chat = new Chat(resultSet.getInt("chat_id"),
                        resultSet.getString("chat_name"),
                        resultSet.getString("isprivate"));
            }
        } catch (SQLException e) {
            logger.error("Problem with getting data for new chat from CHATS table");
        }
    }

    /**
     * Method for deleting data about chat users in ChatUsers database table.
     * In this case we if someone left private chat we delete all data about it.
     *
     * @param chatId chat id
     */
    public void deletePrivateChatFromChatUsers(Integer chatId) {
        String query = chatId + "'";
        try {
            preparedStatement = connection.prepareStatement(deletePrivateChatFromChatUsersQuery + query);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Failed to delete private chat - > Chat with chatID " + chatId + " was not deleted !", e);
        }
    }

    /**
     * The method is used for deleting user from group chat.
     * We delete only user dependency with chat he/she left in ChatUsers database table.
     *
     * @param usersId
     * @param chatId
     */
    public void deleteUserFromChat(Integer usersId, Integer chatId) {
        String secondQuery =  chatId + "' AND USER_ID='" + usersId + "'";
        try {
            preparedStatement = connection.prepareStatement(deleteUserFromChatQuery + secondQuery);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Failed to delete user from chat - > Chat with chatID " + chatId + " was not deleted !", e);
        }
    }

    /**
     * Here we delete private chat from database after one of interlocutor left chat.
     *
     * @param chatId
     */
    public void deleteChat(Integer chatId) {
        String query = chatId + "'";
        try {
            preparedStatement = connection.prepareStatement(deleteChatQuery + query);
            resultSet = preparedStatement.executeQuery();
            logger.info("Chat with chatID " + chatId + " was deleted !");
        } catch (SQLException e) {
            logger.error("Failed to delete Chat with chatID " + chatId + " was not deleted !", e);
        }
    }

    /**
     * Method is used for getting chat name according to its name.
     *
     * @param chatId
     * @return string chat name
     */
    public String getChatNameById(Integer chatId) {
        String query = chatId + "'";
        String chatName = "";
        try {
            preparedStatement = connection.prepareStatement(getChatNameByIdQuery + query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                chatName = resultSet.getString("chat_name");
        } catch (SQLException e) {
            logger.error("Process getChatNameById failed -> Chat with chatID " + chatId + " was not deleted !", e);
        }
        return chatName;
    }
}
