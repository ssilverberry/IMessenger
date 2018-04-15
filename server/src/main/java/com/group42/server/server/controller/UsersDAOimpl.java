package com.group42.server.server.controller;


import com.group42.server.controller.DAOHandler;
import com.group42.server.model.User;
import com.group42.server.protocol.StringCrypter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The class is used for reading data
 * from db and inserting new data there.
 *
 * @author Paul Horiachyi
 * */
public class UsersDAOimpl {
    private static final String getUsersQuery = "SELECT * FROM USERS";
    private static final String getOnlineUsersQuery = "SELECT * FROM USERS WHERE USER_STATUS='1'";
    private static final String getOfflineUsersQuery = "SELECT * FROM USERS WHERE USER_STATUS='0'";
    private static final String insesrtIntoTableQuery = "INSERT INTO users " +
            "(user_login, user_password, USER_EMAIL, USER_STATUS," +
            " USER_SECNAME, USER_BIRTH, USER_PHONENUMBER, USER_FIRSTNAME) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String updateUsrStatusQuery = "UPDATE USERS SET USER_STATUS =";
    private static final String updateUsrStatusSecQuery = " WHERE USER_ID = ";
    private static final String resetAllQuery = "UPDATE USERS SET USER_STATUS ='0'";
    private static final String getUserIdByNameQuery = "SELECT USER_ID FROM USERS WHERE USER_LOGIN='";
    private static final String getUsersForGroupQuery = "SELECT USERS.USER_LOGIN from USERS, CHAT_USERS " +
            "WHERE USERS.USER_ID = CHAT_USERS.USER_ID and CHAT_USERS.CHAT_ID ='";
    private static final String getUserStatusQuery = "SELECT USERS.USER_STATUS from USERS WHERE USERS.USER_LOGIN = '";
    private static final com.group42.server.controller.UsersDAOimpl instance = new com.group42.server.controller.UsersDAOimpl();

    private Connection connection = DAOHandler.getInstance().getConnection();
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private StringCrypter crypter = new StringCrypter(new byte[]{1, 4, 5, 6, 8, 9, 7, 8});
    private static final Logger logger = LogManager.getLogger(com.group42.server.controller.UsersDAOimpl.class);

    public static com.group42.server.controller.UsersDAOimpl getInstance() {
        if (instance != null)
            return instance;
        else
            return new com.group42.server.controller.UsersDAOimpl();
    }

    private UsersDAOimpl() {
    }

    /**
     * Here we parse all data which got from database
     *
     * @return users list
     * */
    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(getUsersQuery);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Getting all users failed");
        }
        return list;
    }

    /**
     * The method is used for getting online users.
     * @return online users list.
     */
    public List<String> getOnlineUsers() {
        List<String> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(getOnlineUsersQuery);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("user_login"));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("Getting online Users failed");
        }
        return list;
    }

    /**
     * Here we get offline users.
     * @return offline users list.
     */
    public List<String> getOfflineUsers() {
        List<String> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(getOfflineUsersQuery);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("user_login"));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("Getting offline users failed");
        }
        return list;
    }
    /**
     * Here we insert data to database.
     * All passwords are encrypted.
     *
     * @param login user login (nickname or name)
     * @param email user email
     * @param pswrd user password which was encrypted
     * */
    public void insertInto(String login, String pswrd, String email, String secName,
                           Date birthday, String phone, String firstName) {
        try {
            preparedStatement = connection.prepareStatement(insesrtIntoTableQuery);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, crypter.encrypt(pswrd));
            preparedStatement.setString(3, email);
            preparedStatement.setInt(4, 0);
            preparedStatement.setString(5, secName);
            preparedStatement.setDate(6, birthday);
            preparedStatement.setString(7, phone);
            preparedStatement.setString(8, firstName);
            resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException e) {
           logger.error("insertInto proces failed; Login: " + login);
        }
    }

    /**
     * The method is used for creating an user instance
     * for future interactions.
     *
     * @param resultSet your result set from db.
     * @return new user with params such as id, email, password, login and status (online / offline)
     */
    private User parseUser(ResultSet resultSet) {
        User user = null;
        try {
            Integer id = resultSet.getInt("user_id");
            String login = resultSet.getString("user_login");
            String pswrd = resultSet.getString("user_password");
            String email = resultSet.getString("user_email");
            Integer usr_status = resultSet.getInt("user_status");
            String secondName = resultSet.getString("user_secname");
            Date birth = resultSet.getDate("user_birth");
            String phoneNumber = resultSet.getString("user_phonenumber");
            String firstName = resultSet.getString("user_firstname");

            String password = crypter.decrypt(pswrd);
            user = new User(login, secondName, birth, phoneNumber, password, email, id, usr_status, firstName);
        } catch (SQLException e) {
            logger.error("parseUser process failed");
        }
        return user;
    }
    /**
     * The method is used for updating user status (online / offline)
     * in database according to changes.
     *
     * @param user_id user id (int number) whose status you want to change
     * @param status int number 1 (online) or 0 (offline)
     * */
    public void updateUsrStatus(Integer user_id, Integer status) {
        try {
            preparedStatement = connection.prepareStatement( updateUsrStatusQuery + status +
                    updateUsrStatusSecQuery + user_id);
            resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("updateUsrStatus process failed with user_id: " + user_id + "; status: " + status);
        }
    }
    /**
     * Here we reset user status every time when server starts again.
     * It was made because of
     */
    public void resetAll() {
        try {
            preparedStatement = connection.prepareStatement(resetAllQuery);
            resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.info("ResetAll process failed with query: " + resetAllQuery);
        }
    }

    /**
     * The method allows to get user's id by its name.
     *
     * @param str username
     * @return id (Integer)
     */
    public int getUserIdByName(String str) {
        int id = 0;
        try {
            preparedStatement = connection.prepareStatement( getUserIdByNameQuery +
                    str +"'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                id = resultSet.getInt("user_id");
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("getUserIdByName process failed with string: " + str);
        }
        return id;
    }

    /**
     * Thanks to the method we are able to get any user list for any chat.
     *
     * @param chatId chat id
     * @return user list
     */
    public List<String> getUsersForGroup(Integer chatId){
        List<String> users = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(getUsersForGroupQuery + chatId + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                users.add(resultSet.getString("user_login"));
            }
        } catch (SQLException e) {
            logger.debug("getUsersForGroup process failed with chatID " + chatId);
        }
        return users;
    }

    /**
     * Method for getting user status (online / offline).
     *
     * @param login
     * @return status (Integer)
     */
    public int getUserStatus(String login) {
        int status = 0;
        try {
            preparedStatement = connection.prepareStatement(getUserStatusQuery + login + "'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                status = resultSet.getInt("user_status");
            preparedStatement.close();
        } catch (SQLException e) {
            logger.debug("getUserStatus process failed; Login: " + login);
        }
        return status;
    }
}
