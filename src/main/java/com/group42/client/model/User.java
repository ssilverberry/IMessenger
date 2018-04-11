package com.group42.client.model;

/**
 * Class for storage some user.
 */

public class User {

    private String login;

    /**
     * Constructs some user.
     * @param login
     */
    User(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
