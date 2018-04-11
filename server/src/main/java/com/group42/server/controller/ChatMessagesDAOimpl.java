package com.group42.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The class is used for getting and updating data from / to database.
 * We use single tone pattern for the class.
 */
public class ChatMessagesDAOimpl implements DAOHandler {
    private static final ChatMessagesDAOimpl instance = new ChatMessagesDAOimpl();
    private Connection connection;
    private PreparedStatement prepStatement;
    private ResultSet resultSet;
    private static final Logger logger = LogManager.getLogger(UsersDAOimpl.class);
    private String username;
    private String password;
    private String url;

    public static ChatMessagesDAOimpl getInstance() {
        return instance;
    }

    private Properties props = new Properties();

    private ChatMessagesDAOimpl() {
        super();
    }

    /**
     * Here we get data from properties file in init method.
     * And after it we connect to our schema with tables.
     *
     * @return false as default and true if connection was successful
     */
    @Override
    public boolean connect() {
        try {
            init();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, username, password);
            if (!connection.isClosed()) {
                System.out.println("Connected to << Chat Messages >> table !");
                return true;
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Reading from properties file failed");
        }
        return false;
    }

    /**
     * We use the method for closing all statements, connections and result sets.
     */
    @Override
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
     * Here we get data from properties file.
     * In that file you are able to write your credentials
     * for connecting to your own schema and database.
     */
    @Override
    public void init() {
        try {
            String path = "db.properties";
            String localfile = new File(path).getAbsolutePath();
            props.load(new FileInputStream(localfile));
            username = props.getProperty("user");
            password = props.getProperty("password");
            url = props.getProperty("dburl");
        } catch (IOException e) {
            logger.error("Reading from properties file failed");
        }
    }

    /**
     * The method is used for getting data from database and building a response string
     *
     * @param resultSet
     * @return string with formatted date, message author and message.
     */
    private String parseMsg(ResultSet resultSet) {
        StringBuilder builder = new StringBuilder();
        try {
            LocalDateTime dateTime = resultSet.getTimestamp("msg_date").toLocalDateTime();
            String date = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"));
            builder.append(date).append(" ");
            builder.append(resultSet.getString("msg_author")).append(" > ");
            builder.append(resultSet.getString("msg_content"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * The method is used for getting any chat history for anyone who was offline.
     *
     * @param chatId from where you want to get history.
     * @return strings' list with all messages for any chat.
     */
    public List<String> getChatHistoryForChat(Integer chatId) {
        List<String> list = new ArrayList<>();
        try {
            prepStatement = connection.prepareStatement(
                    "SELECT MSG_CONTENT, MSG_AUTHOR, MSG_DATE FROM CHAT_MESSAGES WHERE CHAT_ID='" + chatId + "' ORDER BY MSG_DATE ASC");
            resultSet = prepStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseMsg(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Something wrong with db", e);
        }
        return list;
    }

    /**
     * Here we insert all needed data to database according to chat id.
     * In another words we write chat message history to database.
     *
     * @param chat_id
     * @param msgFrom it is a message author
     * @param msgBody it is a message
     */
    public void insertIntoChatMessages(Integer chat_id, String msgFrom, String msgBody) {
        try {
            prepStatement = connection.prepareStatement(
                    "INSERT INTO CHAT_MESSAGES (CHAT_ID, MSG_AUTHOR, MSG_DATE, MSG_CONTENT) " + "VALUES (?, ?, ?, ?)");
            prepStatement.setInt(1, chat_id);
            prepStatement.setString(2, msgFrom);
            prepStatement.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            prepStatement.setString(4, msgBody);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Inserting to db failed", e);
        }
    }

    /**
     * Here we delete chat message history. The method is used for deleting history only from private chats.
     *
     * @param chatId private chat id.
     */
    public void deleteChatHistoryByChatId(Integer chatId) {
        String query;
        try {
            prepStatement = connection.prepareStatement(
                    "DELETE FROM CHAT_MESSAGES WHERE CHAT_ID='" + chatId + "'");
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Chat with chatID " + chatId + " was not deleted !", e);
        }
    }
}
