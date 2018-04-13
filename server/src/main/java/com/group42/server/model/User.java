package com.group42.server.model;


import java.sql.Date;

/**
 * This Class is using for creating new accounts
 *
 * Here we use different constructors because of
 * different action steps between client and server
 */
public class User {
    private String login;
    private String firstName;
    private String secondName;
    private Date birth;
    private String phoneNumber;
    private String password;
    private String email;
    private Integer id;
    private Integer user_status;

    public User (String user_name, String pswrd, String email) {
        login = user_name;
        password = pswrd;
        this.email = email;
    }

    public User(String name, String secondName, Date birth, String phoneNumber,
                String password, String email, Integer id, Integer user_status, String firstName) {
        this.login = name;
        this.secondName = secondName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.id = id;
        this.user_status = user_status;
        this.firstName = firstName;
    }

    public User (String user_name) {
        login = user_name;
    }
    public String getLogin() {
        return login;
    }

    public void setName(String name) {
        this.login = name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId (Integer id) { this.id = id; };

    public Integer getId () { return id; }

    public Integer getUser_status() {
        return user_status;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

}
