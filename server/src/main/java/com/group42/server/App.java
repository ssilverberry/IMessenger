package com.group42.server;

import com.group42.server.controller.Server;

public class App 
{
    public static void main( String[] args ) {
        Server server = new Server();
        server.start();
    }
}
