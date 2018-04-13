package com.group42.server.controller;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Server class.
 *
 * Here we init server port and start infinite loop for maintaining
 * connections to our initialized server port.
 * Every new connection is a new thread.
 */
public class Server extends Thread {
    private int serverPort = 3000;
    private LinkedList<ServerWorker> workerList = new LinkedList<>();
    private ArrayList<String> onlineUsers = new ArrayList<>();
    List<ServerWorker> getWorkerList() {
        return workerList;
    }

    /**
     * Here we connect to our schema with required tables.
     */
    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(serverPort);

            DAOHandler.getInstance().connect();

            while (true) {
                System.out.println("Wait for client");
                Socket socket = listener.accept();
                System.out.println("Connection is established ! " + socket);
                ServerWorker worker = new ServerWorker(this, socket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            System.err.println("Connection is failed !");
            DAOHandler.getInstance().disconnect();
        }
        DAOHandler.getInstance().disconnect();
    }

    /**
     * We delete every thread which was logged out.
     * @param serverWorker
     */
    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }

    /**
     * It is getter for online users list.
     * @return
     */
    public ArrayList<String> getOnlineUsers() {
        return onlineUsers;
    }
}

