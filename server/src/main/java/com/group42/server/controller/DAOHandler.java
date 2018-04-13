package com.group42.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DAOHandler implements DAO {

    private static final Logger LOG = LogManager.getLogger(DAOHandler.class);
    private String name;
    private String password;
    private String url;
    private Connection connection;
    private static final DAOHandler instance = new DAOHandler();

    private DAOHandler() {
    }
    /*
     * The method is used for configuring oracle database account.
     * Here we read from properties file, where you are able to
     * write any credits which are needed for connecting to your
     * database.
     */
    @Override
    public void init() {
        Properties props = new Properties();

        try  {
            String path = "src/main/resources/db.properties";
            props.load(new FileInputStream(path));
            name = props.getProperty("user");
            password = props.getProperty("password");
            url = props.getProperty("dburl");
        } catch (IOException e) {
            LOG.error("Reading from properties file failed");
        }
    }
    /**
     * This method is used for connection to your database.
     *
     * Here we invoke 'init' method for getting database config
     * then we get oracle db driver and instantiate connection between
     * app and database.
     */
    @Override
    public void connect() {
        try {
            init();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, name, password);
            if (!connection.isClosed()) {
                System.out.println("Connected to tables !");
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error("Connection failed");
        }
    }
    /**
     * The method is used for disconnecting from database
     * after server was turned off.
     * <p>
     * We close all statements, result sets & connections.
     */
    @Override
    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }


    public static DAOHandler getInstance() {
        if (instance != null)
            return instance;
        else
            return new DAOHandler();
    }
}
