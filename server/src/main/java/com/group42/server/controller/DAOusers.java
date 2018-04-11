package com.group42.server.controller;

import com.group42.server.model.User;

import java.util.List;

public interface DAOusers {
    void init();
    boolean connect();
    void disconnect();
    List<User> getUsers();
}
