package com.group42.server.controller;

import com.group42.server.model.GroupChatRoom;
import com.group42.server.model.StringCrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ChatRoomsDAOimpl {

    private static final ChatRoomsDAOimpl instance = new ChatRoomsDAOimpl();
    private Connection connection;
    private Statement prepStatement;
    private ResultSet resultSet;
    private StringCrypter crypter = new StringCrypter(new byte[]{1, 4, 5, 6, 8, 9, 7, 8});
    private static final Logger logger = LogManager.getLogger(UsersDAOimpl.class);

    public static ChatRoomsDAOimpl getInstance() {
        return instance;
    }

    private Properties props = new Properties();

    private ChatRoomsDAOimpl() {
        super();
    }

    private String username;
    private String password;
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
            props.load(new FileInputStream(file));
            username = props.getProperty("user");
            password = props.getProperty("password");
            url = props.getProperty("dburl");
            System.out.println(props.getProperty("user") + props.getProperty("password") + props.getProperty("dburl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method is used for connection to your database.
     *
     * Here we invoke 'init' method for getting database config
     * then we get oracle db driver and instantiate connection between
     * app and database.
     */
    public boolean connect() {
        try {
            init();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, username, password);
            if (!connection.isClosed()) {
                System.out.println("Connection successful!");
                resetAll();
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
     *
     * We close all statements, result sets & connections.
     * */
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
     * */
    public List<GroupChatRoom> getChatRooms(String str) {
        List<GroupChatRoom> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM GROUP_CHATS WHERE CHAT_NAME='" + str + "'");
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
     * Here we insert data to database.
     * All passwords are encrypted.
     */
    public void insertInto(String chatRoomName, String username) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO GROUP_CHATS (CHAT_NAME, CHAT_USER_NAME) " + "VALUES (?, ?)");
            prepStatement.setString(1, chatRoomName);
            prepStatement.setString(2, username);
            resultSet = prepStatement.executeQuery();
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
     * */
    private GroupChatRoom parseChatRoom(ResultSet resultSet) {
        GroupChatRoom room = null;
        try {
            String id = resultSet.getString("chat_name");
            String fromuser = resultSet.getString("chat_user_name");
            room = new GroupChatRoom(id, fromuser);
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
            resultSet = prepStatement.executeQuery("DELETE FROM GROUP_CHATS WHERE CHAT_NAME='" + chatname
                    + "' AND CHAT_USER_NAME = '" + username +"'");
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

    public void createTable () {
        try {
            prepStatement = connection.createStatement();
            resultSet = prepStatement.executeQuery("create table group_chats (chat_name VARCHAR2(100) not null, chat_user_name VARCHAR2(100) not null)");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.info("Table was not created");
        }
    }
}
