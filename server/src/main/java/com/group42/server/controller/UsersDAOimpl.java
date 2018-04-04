package com.group42.server.controller;


import com.group42.server.model.StringCrypter;
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
public class UsersDAOimpl implements DAOusers {

    private static final UsersDAOimpl instance = new UsersDAOimpl();
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private StringCrypter crypter = new StringCrypter(new byte[]{1, 4, 5, 6, 8, 9, 7, 8});
    private static final Logger logger = LogManager.getLogger(UsersDAOimpl.class);

    public static UsersDAOimpl getInstance() {
        return instance;
    }

    private Properties props = new Properties();

    private UsersDAOimpl() {
        super();
    }

    private String username;
    private String password;
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
     * */
    @Override
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
    @Override
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
        //disconnect();
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
                           String birthday, String phone, String firstName) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO users (user_login, user_password, USER_EMAIL, USER_STATUS," +
                            " USER_SECNAME, USER_BIRTH, USER_PHONENUMBER, USER_FIRSTNAME) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, crypter.encrypt(pswrd));
            preparedStatement.setString(3, email);
            preparedStatement.setInt(4, 0);
            preparedStatement.setString(5, secName);
            preparedStatement.setString(6, birthday);
            preparedStatement.setString(7, phone);
            preparedStatement.setString(8, firstName);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
           logger.error("Something wrong with DB");
        }
    }

    /**
     * The method is used for creating an user instance
     * for future interactions.
     *
     * @param resultSet your result set from db.
     * @return new user with params such as id, email, password, login and status (online / offline)
     * */
    private User parseUser(ResultSet resultSet) {
        User user = null;
        try {
            int id = resultSet.getInt("user_id");
            String login = resultSet.getString("user_login");
            String pswrd = resultSet.getString("user_password");
            String email = resultSet.getString("user_email");
            Integer usr_status = resultSet.getInt("user_status");
            String secondName = resultSet.getString("user_secname");
            String birth = resultSet.getString("user_birth");
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
}
