package com.group42.server.controller;


import com.group42.server.protocol.StringCrypter;
import com.group42.server.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The class is used for reading data
 * from db and inserting new data there.
 *
 * @author Paul Horiachyi
 * */
public class UsersDAOimpl implements DAOHandler {

    private static final UsersDAOimpl instance = new UsersDAOimpl();
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private StringCrypter crypter = new StringCrypter(new byte[]{1, 4, 5, 6, 8, 9, 7, 8});
    private static final Logger logger = LogManager.getLogger(UsersDAOimpl.class);

    public static UsersDAOimpl getInstance() {
        return instance;
    }

    private Properties properties = new Properties();

    private UsersDAOimpl() {
        super();
    }

    private String usrname;
    private String pswrd;
    private String url;
    private boolean dbAccess;

    /*
    * The method is used for configuring oracle database account.
    * Here we read from properties file, where you are able to
    * write any credits which are needed for connecting to your
    * database.
    * */
    @Override
    public void init() {
        try {
            String path = "db.properties";
            String pathToFile = new File(path).getAbsolutePath();
            properties.load(new FileInputStream(pathToFile));
            usrname = properties.getProperty("user");
            pswrd = properties.getProperty("password");
            url = properties.getProperty("dburl");
        } catch (IOException e) {
            logger.error("Reading from properties file failed");
        }
    }
    /**
     * This method is used for connection to your database.
     *
     * Here we invoke 'init' method for getting database config
     * then we get oracle db driver and instantiate connection between
     * app and database.
     * */
    @Override
    public boolean connect() {
        try {
            init();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, usrname, pswrd);
            if (!connection.isClosed()) {
                System.out.println("Connected to << Users >> table!");
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
    @Override
    public void disconnect() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
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
    public List<User> getUsers() {
        List<User> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM USERS");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE USER_STATUS='1'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("user_login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE USER_STATUS='0'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("user_login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (user_login, user_password, USER_EMAIL, USER_STATUS," +
                            " USER_SECNAME, USER_BIRTH, USER_PHONENUMBER, USER_FIRSTNAME) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, crypter.encrypt(pswrd));
            preparedStatement.setString(3, email);
            preparedStatement.setInt(4, 0);
            preparedStatement.setString(5, secName);
            preparedStatement.setDate(6, birthday);
            preparedStatement.setString(7, phone);
            preparedStatement.setString(8, firstName);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
           logger.error("Something wrong with DB", e);
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
            e.printStackTrace();
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
            preparedStatement = connection.prepareStatement("UPDATE USERS SET USER_STATUS =" + status + " WHERE USER_ID = " + user_id);
            resultSet = preparedStatement.executeQuery();
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
            preparedStatement = connection.prepareStatement("UPDATE USERS SET USER_STATUS =" + 0);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.info("Reset failed");
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
            preparedStatement = connection.prepareStatement("SELECT USER_ID FROM USERS WHERE USER_LOGIN='" +
                    str +"'");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                id = resultSet.getInt("user_id");
        } catch (SQLException e) {
            logger.error("Error with getting user id", e);
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
        String query = "SELECT USERS.USER_LOGIN from USERS, CHAT_USERS WHERE USERS.USER_ID = CHAT_USERS.USER_ID and CHAT_USERS.CHAT_ID ='" + chatId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                users.add(resultSet.getString("user_login"));
            }
        } catch (SQLException e) {
            logger.debug("EXCEPTION IN getChatListForUser()", e);
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
        String query = "SELECT USERS.USER_STATUS from USERS WHERE USERS.USER_LOGIN = '" + login + "'";
        int status = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                status = resultSet.getInt("user_status");
        } catch (SQLException e) {
            logger.debug("EXCEPTION IN getChatListForUser()", e);
        }
        return status;
    }
}
