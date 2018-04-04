package com.group42.server.controller;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Server extends Thread {
    private int serverPort = 3000;
    private LinkedList<ServerWorker> workerList = new LinkedList<>();
    private ArrayList<String> onlineUsers = new ArrayList<>();
    private File file = new File("General chat.txt");

    List<ServerWorker> getWorkerList() {
        return workerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(serverPort);
            UsersDAOimpl.getInstance().connect();
            ChatRoomsDAOimpl.getInstance().connect();
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
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }

    public ArrayList<String> getOnlineUsers() {
        return onlineUsers;
    }

    public File getFile() {
        return file;
    }
}

