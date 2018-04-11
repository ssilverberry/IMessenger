package com.group42.server.controller;

public interface DAOHandler {
    void init();
    boolean connect();
    void disconnect();
}
