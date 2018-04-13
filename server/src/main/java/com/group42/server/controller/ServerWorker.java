package com.group42.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group42.server.model.*;
import com.group42.server.protocol.InputMessage;
import com.group42.server.protocol.OutMessage;
import com.group42.server.protocol.StringCrypter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ServerWorker class.
 * The class is used for creating a new thread for every new connected user.
 * Every new connection is a new thread.
 *
 * @author Paul Horiachyi.
 */
public class ServerWorker extends Thread {

    private final Server server;
    private final Socket socket;

    private static StringCrypter crypter = new StringCrypter(new byte[]{1, 4, 5, 6, 8, 9, 7, 8});
    private OutputStream outputStream;
    private PrintWriter output;

    private static InputMessage mesage;
    private static Gson gson = new Gson();
    private static ArrayList<String> onlineUsers = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * It is a default constructor.
     *
     * @param server server class instance
     * @param socket socket which was created in server class instance.
     */
    public ServerWorker(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    /**
     * Overridden 'run' method where we invoke the main method 'handleClientSocket' in the class.
     */
    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            LOGGER.error("Connection was closed unexpectedly !");
        }
    }

    /**
     * It is a protocol side method.
     * The method is used for transforming object to string.
     * Method send encrypted string in Base64;
     *
     * @param OutMessage existing object what you want to convert to string.
     * @return encrypted string.
     */
    private static String transformOut(Object OutMessage) {
        Gson out = new GsonBuilder().create();
        return crypter.encrypt(out.toJson(OutMessage));
    }

    /**
     * It is a protocol side method.
     * The method is used for converting input encrypted json string to object.
     *
     * @param response input encrypted json string.
     * @return decrypted string and convert it to object (InputMessage)
     */
    private static InputMessage transform(String response) {
        return gson.fromJson(crypter.decrypt(response), InputMessage.class);
    }

    /**
     * HandleClientSocket method.
     *
     * It is a main controller method which interacts with clients.
     * Method send all needed data to client according to client requests.
     */
    private void handleClientSocket() throws IOException {
        outputStream = socket.getOutputStream();
        output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            preAccess(line);
            // Here we get action from ServerHandler
            String incom = ServerHandler.getResponse();
            LOGGER.info(incom);
            // We split ServerHandler response line into array for getting right action in the first array item
            String[] tokens = StringUtils.split(incom);
            switch (tokens[0]) {
                case "access":
                    currentThread().setName(mesage.getLogin());
                    LOGGER.info("Thread Name as username" + currentThread().getName());
                    authorization();
                    LOGGER.info("<< Access step >>");
                    break;
                case "accessDenied":
                    OutMessage deny = new OutMessage(13);
                    line = transformOut(deny);
                    output.println(line);
                    LOGGER.info("<< User is not registered >>");
                    break;
                case "noRegUser":
                    OutMessage noRegUser = new OutMessage(12);
                    line = transformOut(noRegUser);
                    output.println(line);
                    LOGGER.info("<< User is not registered >>");
                    break;
                case "regFailed":
                    OutMessage regFailed = new OutMessage(22);
                    line = transformOut(regFailed);
                    output.println(line);
                    LOGGER.info("<< User already exists >>");
                    break;
                case "regUser":
                    OutMessage regUser = new OutMessage(21, tokens[1]);
                    line = transformOut(regUser);
                    output.println(line);
                    LOGGER.info("<< User is registered >> " + mesage.getLogin());
                    break;
                case "messageAll":
                    sendMsgToAll(mesage.getChatId(), mesage.getFromUser(), mesage.getActionId(), mesage.getMsgBody());
                    break;
                case "messagePrivate":
                    sendMessageToPrivateRoom(mesage.getFromUser(), mesage.getToUser(), mesage.getMsgBody());
                    LOGGER.info("<< Message Private step >>" +
                            mesage.getFromUser() + " " + mesage.getToUser() + " " + mesage.getMsgBody());
                    break;
                case "activeList":
                    sendList();
                    LOGGER.info("<< Online users list was sent >>");
                    break;
                case "createPrivateChatRoom":
                    createPrivateChat();
                    LOGGER.info("<< Create private chat room step. Chat created. >>");
                    break;
                case "logOut":
                    handleLogOff();
                    break;
                case "chatHistory":
                    sendChatHistory(mesage.getChatId(), mesage.getFromUser());
                    break;
                case "userInfo":
                    sendUserInfo(mesage.getLogin());
                    break;
                case "createGroupChat":
                    createGroupChat(mesage.getChatName());
                    break;
                case "messageToGroup":
                    messageToGroupChat();
                    break;
                case "leaveChat":
                    deleteUserFromGroup();
                    break;
                case "addMemberToGroup":
                    addMembersToGroupChat();
                    break;
                default:
                    sendUserChatsList();
                    break;
            }
        }
        socket.close();
    }

    /**
     * Method for creating private chats.
     * It is a working method.
     */
    private void createPrivateChat() {
        String chatName = mesage.getFromUser() + " " + mesage.getToUser();
        ChatRoomsDAOimpl.getInstance().insertIntoChats(chatName, 1);

        int chat_id = ChatRoomsDAOimpl.getInstance().getChatIdByName(chatName);
        int first_user_id = UsersDAOimpl.getInstance().getUserIdByName(mesage.getFromUser());
        int second_user_id = UsersDAOimpl.getInstance().getUserIdByName(mesage.getToUser());
        ChatRoomsDAOimpl.getInstance().insertIntoChatUsers(chat_id, first_user_id);
        ChatRoomsDAOimpl.getInstance().insertIntoChatUsers(chat_id, second_user_id);

        List<ServerWorker> serverWorkers = server.getWorkerList();

        OutMessage msg = new OutMessage(chatName, 34, chat_id);
        String newLine = transformOut(msg);
        System.out.println("message to client" + newLine);
        for (ServerWorker i : serverWorkers) {
            if (mesage.getToUser().equals(i.getName()))
                i.send(newLine);
            if (mesage.getFromUser().equals(i.getName()))
                i.send(newLine);
        }
    }

    /**
     * Here we handle if user wants to leave group chat or private chat and send info
     * about it to his / her interlocutors in that group/private chat.
     * If it is a private chat so then we delete it from db.
     */
    private void deleteUserFromGroup() {
        if (ChatRoomsDAOimpl.getInstance().isPrivateChatById(mesage.getChatId())) {
            LOGGER.info("User: " + mesage.getLogin() + " leaves private chat");
            OutMessage outMessage = new OutMessage(mesage.getChatId(), 41);
            String response = transformOut(outMessage);

            for (ServerWorker worker : server.getWorkerList()) {
                for (String login : UsersDAOimpl.getInstance().getUsersForGroup(mesage.getChatId())) {
                    if (worker.getName().equals(login))
                        worker.send(response);
                }
            }

            ChatRoomsDAOimpl.getInstance().deletePrivateChatFromChatUsers(mesage.getChatId());
            ChatMessagesDAOimpl.getInstance().deleteChatHistoryByChatId(mesage.getChatId());
            ChatRoomsDAOimpl.getInstance().deleteChat(mesage.getChatId());
        } else {
            ChatRoomsDAOimpl.getInstance().deleteUserFromChat(
                    UsersDAOimpl.getInstance().getUserIdByName(mesage.getLogin()),
                    mesage.getChatId()
            );
            OutMessage outMessage = new OutMessage(
                    40,
                    mesage.getChatId(),
                    UsersDAOimpl.getInstance().getUsersForGroup(mesage.getChatId())
            );
            String response = transformOut(outMessage);
            for (ServerWorker worker : server.getWorkerList()) {
                for (String login : UsersDAOimpl.getInstance().getUsersForGroup(mesage.getChatId())) {
                    if (worker.getName().equals(login))
                        worker.send(response);
                }
            }
        }
    }

    /**
     * The method is used for sending messages only to group members.
     */
    private void messageToGroupChat() {
        List<ServerWorker> workerList = server.getWorkerList();
        List<ChatRoom> chatUsersList = ChatRoomsDAOimpl.getInstance().getChatRooms(mesage.getChatName());
        OutMessage outMessage = new OutMessage(mesage.getChatName(), 39, mesage.getFromUser(),
                mesage.getMsgBody());
        String line = transformOut(outMessage);
        for (ChatRoom user : chatUsersList) {
            for (ServerWorker worker : workerList) {
                if (worker.getName().equals(user.getUsername()) &
                        user.getGroupName().equals(mesage.getChatName())) {
                    worker.send(line);
                }
            }
        }
        //writeMsgToDB(mesage.getChatName());
    }

    /**
     * The method is used for creating group chat room.
     * When room was created the members list would be send
     * to everyone member of that group.
     *
     * It is a working method.
     */
    private void createGroupChat(String chatName) {
        ChatRoomsDAOimpl.getInstance().insertIntoChats(chatName, 0);

        int chat_id = ChatRoomsDAOimpl.getInstance().getChatIdByName(chatName);
        if (mesage.getUserChatList() != null) {
            for (String userName : mesage.getUserChatList()) {
                int userId = UsersDAOimpl.getInstance().getUserIdByName(userName);
                ChatRoomsDAOimpl.getInstance().insertIntoChatUsers(chat_id, userId);
            }
        }

        List<ServerWorker> workerList = server.getWorkerList();
        String line = transformOut(new OutMessage(37, mesage.getChatName(), mesage.getUserChatList(), chat_id));
        for (String str : mesage.getUserChatList()) {
            for (ServerWorker worker : workerList) {
                if (worker.getName().equals(str))
                    worker.send(line);
            }
        }
    }

    /**
     * Here we parse all users from database.
     * And get all additional user info then send it to client.
     *
     * @param login user login
     */
    private void sendUserInfo(String login) {
        List<User> usersList = UsersDAOimpl.getInstance().getUsers();
        for (User user : usersList) {
            if (login.equals(user.getLogin())) {
                OutMessage outMessage = new OutMessage(38, user.getEmail(), user.getFirstName(),
                        user.getSecondName(),
                        user.getBirth().toLocalDate(), user.getPhoneNumber());
                String line = transformOut(outMessage);
                output.println(line);
            }
        }
    }

    /**
     * The method is used for adding user to existing group or chat.
     */
    private void addMembersToGroupChat() {
        ChatRoomsDAOimpl.getInstance().insertIntoChatUsers(mesage.getChatId(),
                UsersDAOimpl.getInstance().getUserIdByName(mesage.getFromUser()));
        String fromUser = mesage.getFromUser();
        String chatName = ChatRoomsDAOimpl.getInstance().getChatNameById(mesage.getChatId());
        OutMessage outMessage = new OutMessage(
                37,
                chatName,
                UsersDAOimpl.getInstance().getUsersForGroup(mesage.getChatId()),
                mesage.getChatId()
        );
        String str = transformOut(outMessage);
        for (ServerWorker worker : server.getWorkerList()) {
            if (worker.getName().equals(fromUser)) {
                worker.send(str);
                break;
            }
        }
        OutMessage anotherOutMessage = new OutMessage(
                40,
                mesage.getChatId(),
                UsersDAOimpl.getInstance().getUsersForGroup(mesage.getChatId())
        );
        String strlocal = transformOut(anotherOutMessage);
        for (ServerWorker serverWorker : server.getWorkerList()) {
            for (String usrName : UsersDAOimpl.getInstance().getUsersForGroup(mesage.getChatId()))
                if (!serverWorker.getName().equals(fromUser) & serverWorker.getName().equals(usrName)) {
                    serverWorker.send(strlocal);
                }
        }
    }

    /**
     * The method is used for sending messages between users in private chat room.
     */
    private void sendMessageToPrivateRoom(String fromUser, String toUser, String msgBody) {
        List<ServerWorker> newServerWorkerList = server.getWorkerList();
        OutMessage out = new OutMessage(fromUser, toUser, msgBody, 33);
        String line = transformOut(out);
        for (ServerWorker serverWorker : newServerWorkerList) {
            if (fromUser.contains(serverWorker.getName()) | toUser.contains(serverWorker.getName())) {
                serverWorker.send(line);
            }
        }
        //writeMsgToDB(mesage.getFromUser() + " " + mesage.getToUser());
    }

    /**
     * This method is used for handling user logging out from chat.
     * Here we get users' list from database and check if that user
     * exists in case of positive answer we update his / her status
     * from 1 to 0 and delete the client thread.
     *
     * After it we send to other users online users' list.
     */
    private void handleLogOff() {
        List<String> localList = getOnlineList();
        String removeUser = " ";
        List<User> usrList = UsersDAOimpl.getInstance().getUsers();
        if (usrList.size() != 0) {
            for (String index : localList) {
                System.out.println(index);
                for (User user : usrList) {
                    LOGGER.info(user.getLogin() + " user.getName " + mesage.getLogin() + " nameFromUser");
                    if (user.getLogin().equals(mesage.getFromUser())) {
                        removeUser = user.getLogin();
                        UsersDAOimpl.getInstance().updateUsrStatus(
                                UsersDAOimpl.getInstance().getUserIdByName(user.getLogin()), 0);
                    }
                }
            }
        }
        onlineUsers.remove(removeUser);
        LOGGER.info(removeUser + ": removed user");
        server.removeWorker(this);
        sendList();
    }

    /**
     * Basic method for sending info to output stream.
     *
     * @param message Json string.
     */
    private void send(String message) {
        output.println(message);
    }

    /**
     * The method is used for sending active users list to client.
     */
    private void sendList() {
        String line;
        OutMessage message = new OutMessage(311, UsersDAOimpl.getInstance().getOnlineUsers(),
                UsersDAOimpl.getInstance().getOfflineUsers());
        line = transformOut(message);
        List<ServerWorker> mylist3 = server.getWorkerList();
        for (ServerWorker i : mylist3) {
            i.send(line);
        }
    }

    /**
     * Here we send general chat history to every client if they
     * were offline while other fill it.
     */
    private void sendChatHistory(Integer chatId, String fromUser) {
        List<String> chatHistory = ChatMessagesDAOimpl.getInstance().getChatHistoryForChat(chatId);
        List<String> chatUsers = UsersDAOimpl.getInstance().getUsersForGroup(chatId);
        OutMessage message = new OutMessage(chatHistory, 36, chatId, chatUsers);
        String str = transformOut(message);
        for (ServerWorker i : server.getWorkerList()) {
            if (i.getName().equals(fromUser))
                i.send(str);
        }
    }

    /**
     * It is a simple getter for online users list.
     */
    private ArrayList<String> getOnlineList() {
        return onlineUsers;
    }

    /**
     * preAccess method.
     *
     * The method send input client message to ServerHandler class.
     * ServerHandler class checks what action came and send in callback
     * a status so server could understand what client needs.
     *
     * @param line where from you read data from inputstream
     */
    private void preAccess(String line) {
        mesage = transform(line);
        LOGGER.info("Login" + "  " + mesage.getLogin() + " " + mesage.getMsgBody());
        ServerHandler.start(mesage);
    }
    /**
     * Authorization method.
     *
     * Here we compare data which retrieve from client
     * with data in database.
     */
    private void authorization() {
        OutMessage access;
        String line;
        List<User> userList = UsersDAOimpl.getInstance().getUsers();

        if (userList.size() != 0) {
            for (User index : userList) {
                if (index.getLogin().equals(mesage.getLogin()) & index.getUser_status() == 0) {
                    UsersDAOimpl.getInstance().updateUsrStatus(index.getId(), 1);
                    onlineUsers.add(mesage.getLogin());

                    LOGGER.info("threadName : " + this.getName() + "clientName : " + mesage.getLogin());
                    sendList();
                    access = new OutMessage(11, index.getLogin());
                    line = transformOut(access);
                    output.println(line);
                } else if (index.getLogin().equals(mesage.getLogin()) & index.getUser_status() == 1) {
                    access = new OutMessage(12);
                    line = transformOut(access);
                    output.println(line);
                }
            }
        }
    }

    /**
     * Method is used for storing general chat message history in json file.
     *
     * This method was updated. Local time set by default as server default local time.
     */
    private void writeMsgToDB(Integer chatId) {
        ChatMessagesDAOimpl.getInstance().insertIntoChatMessages(chatId,
                mesage.getFromUser(), mesage.getMsgBody());
    }

    /**
     * The method for sending data to all threads (GeneralChat).
     *
     * Method was updated and thanks to it we are able to use it for any chat.
     */
    private void sendMsgToAll(Integer chatId, String fromUser, Integer actionId, String msgBody) {
        OutMessage messageAll = new OutMessage(chatId, fromUser, actionId, msgBody);
        String str = transformOut(messageAll);
        for (ServerWorker i : server.getWorkerList()) {
            for (String userLogin : UsersDAOimpl.getInstance().getUsersForGroup(chatId)) {
                if (i.getName().equals(userLogin))
                    i.send(str);
            }

        }
        writeMsgToDB(mesage.getChatId());
        LOGGER.info("<< Message to chat step >> " + mesage.getMsgBody() + "; ChatId: " + mesage.getChatId());
    }
    /**
     * Method for sending user chat list.
     */
    private void sendUserChatsList() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Chat.class, new ChatConverter());
        Gson gsonForChats = builder.setPrettyPrinting().create();
        Chat[] chatarr = ChatRoomsDAOimpl.getInstance().getChatListForUser(mesage.getFromUser());

        OutMessage outMessage = new OutMessage(43, chatarr);
        String str = gsonForChats.toJson(outMessage);
        output.println(crypter.encrypt(str));
    }
}
