package com.group42.server.controller;

import com.group42.server.model.Chat;
import com.group42.server.model.ChatRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * The class is used for interacting with chats' data which we get from database.
 * We used single tone pattern for the class.
 */
public class ChatRoomsDAOimpl implements DAOHandler {

    private static final ChatRoomsDAOimpl instance = new ChatRoomsDAOimpl();
    private Connection connection;
    private Statement prepStatement;
    private ResultSet resultSet;
    private static final Logger logger = LogManager.getLogger(UsersDAOimpl.class);

    public static ChatRoomsDAOimpl getInstance() {
        return instance;
    }

    private Properties prps = new Properties();

    private ChatRoomsDAOimpl() {
        super();
    }

    private String name;
    private String passwrd;
    private String url;

    /*
     * The method is used for configuring oracle database account.
     * Here we read from properties file, where you are able to
     * write any credits which are needed for connecting to your
     * database.
     */
    public void init() {
        try {
            String path = "db.properties";
            String file = new File(path).getAbsolutePath();
            prps.load(new FileInputStream(file));
            name = prps.getProperty("user");
            passwrd = prps.getProperty("password");
            url = prps.getProperty("dburl");
        } catch (IOException e) {
            logger.error("Reading from properties file failed");
        }
    }

    /**
     * This method is used for connection to your database.
     * <p>
     * Here we invoke 'init' method for getting database config
     * then we get oracle db driver and instantiate connection between
     * app and database.
     */
    public boolean connect() {
        try {
            init();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, name, passwrd);
            if (!connection.isClosed()) {
                System.out.println("Connected to << Chats >> table!");
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The method is used for disconnecting from database
     * after server was turned off.
     * <p>
     * We close all statements, result sets & connections.
     */
    public void disconnect() {
        try {
            if (prepStatement != null) {
                prepStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here we parse all data which got from database
     *
     * @return users list
     */
    public List<ChatRoom> getChatRooms(String str) {
        List<ChatRoom> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM CHATS WHERE CHAT_NAME='" + str + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseChatRoom(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Problem with inserting to group chats table");
        }
        return list;
    }

    /**
     * Here we insert data to database (into chats db table).
     */
    public void insertIntoChats(String chatRoomName, Integer isprivate) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO CHATS (CHAT_NAME, ISPRIVATE) " + "VALUES (?, ?)");
            prepStatement.setString(1, chatRoomName);
            prepStatement.setInt(2, isprivate);
            resultSet = prepStatement.executeQuery();
            getDataForNewChat();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("Inserting to db failed");
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
            e.printStackTrace();
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
            prepStatement = connection.createStatement();
            resultSet = prepStatement.executeQuery("DELETE FROM CHATS WHERE CHAT_NAME='" + chatname
                    + "' AND CHAT_USER_NAME = '" + username + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here we reset user status every time when server starts again.
     * It was made because of
     */
    public void resetAll() {
        try {
            prepStatement = connection.createStatement();
            resultSet = prepStatement.executeQuery("UPDATE USERS SET USER_STATUS =" + 0);
        } catch (SQLException e) {
            logger.info("Reset failed");
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
            PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO CHAT_USERS (CHAT_ID, USER_ID) " + "VALUES (?, ?)");
            prepStatement.setInt(1, chat_id);
            prepStatement.setInt(2, user_id);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Inserting to db failed", e);
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT CHAT_ID FROM CHATS WHERE CHAT_NAME='" +
                    str + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                id = resultSet.getInt("chat_id");
        } catch (SQLException e) {
            logger.error("Error with getting chat id", e);
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
        String query = "SELECT ISPRIVATE FROM CHATS WHERE CHAT_ID='" + chatId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                priv = resultSet.getInt("isprivate");
        } catch (SQLException e) {
            logger.error("Error with getting chat id", e);
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
        String query = "SELECT CHATS.CHAT_ID, CHATS.CHAT_NAME, CHATS.ISPRIVATE from CHATS, CHAT_USERS WHERE CHATS.CHAT_ID = CHAT_USERS.CHAT_ID and CHAT_USERS.USER_ID ='" + userId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Integer chatId = resultSet.getInt("chat_id");
                String chatName = resultSet.getString("chat_name");
                String chatType = resultSet.getString("isprivate");
                chatList.add(new Chat(chatId, chatName, chatType));
            }
        } catch (SQLException e) {
            logger.debug("EXCEPTION IN getChatListForUser()", e);
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
    private Chat getDataForNewChat() {
        Chat chat = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM CHATS WHERE CHAT_ID=(SELECT MAX(CHAT_ID) FROM CHATS)");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                chat = new Chat(resultSet.getInt("chat_id"),
                        resultSet.getString("chat_name"),
                        resultSet.getString("isprivate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Problem with getting data from chats");
        }
        return chat;
    }

    /**
     * Method for deleting data about chat users in ChatUsers database table.
     * In this case we if someone left private chat we delete all data about it.
     *
     * @param chatId chat id
     */
    public void deletePrivateChatFromChatUsers(Integer chatId) {
        String query = "DELETE FROM CHAT_USERS WHERE CHAT_ID='" + chatId + "'";
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Chat with chatID " + chatId + " was not deleted !", e);
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
        String query = "DELETE FROM CHAT_USERS WHERE CHAT_ID='" + chatId + "' AND USER_ID='" + usersId + "'";
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Chat with chatID " + chatId + " was not deleted !", e);
        }
    }

    /**
     * Here we delete private chat from database after one of interlocutor left chat.
     *
     * @param chatId
     */
    public void deleteChat(Integer chatId) {
        String query = "DELETE FROM CHATS WHERE CHAT_ID='" + chatId + "'";
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            resultSet = prepStatement.executeQuery();
            logger.info("Chat with chatID " + chatId + " was deleted !");
        } catch (SQLException e) {
            logger.error("Chat with chatID " + chatId + " was not deleted !", e);
        }
    }

    /**
     * Method is used for getting chat name according to its name.
     *
     * @param chatId
     * @return string chat name
     */
    public String getChatNameById(Integer chatId) {
        String query = "SELECT CHAT_NAME FROM CHATS WHERE CHAT_ID='" + chatId + "'";
        String chatName = "";
        try {
            PreparedStatement prepStatement = connection.prepareStatement(query);
            resultSet = prepStatement.executeQuery();
            while (resultSet.next())
                chatName = resultSet.getString("chat_name");
        } catch (SQLException e) {
            logger.error("Chat with chatID " + chatId + " was not deleted !", e);
        }
        return chatName;
    }
}
