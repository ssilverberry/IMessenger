package com.group42.server.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group42.server.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <i><b>ServerWorker</b> class.</i>
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
    private static String chatString;
    private static Gson gson = new Gson();
    private static ArrayList<String> onlineUsers = new ArrayList<>();
    private static List<String> generalChatHistory = new ArrayList<>();
    private static List<String> groupChatUsers = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger();

    private ChatMessages chatHistory;
    private LocalDate localDate = LocalDate.now();

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
            // Here we get action from Handler
            String incom = Handler.getResponse();
            LOGGER.info(incom);
            // We split Handler response line into array for getting right action in the first array item
            String[] tokens = StringUtils.split(incom);
            switch (tokens[0]) {
                case "access":
                    currentThread().setName(mesage.getLogin());
                    LOGGER.info("Thread Name as username" + currentThread().getName());
                    authorization();
                    //readChatHstrFromFile();
                    LOGGER.info("<< Access step >>");
                    break;
                case "noRegUser":
                    OutMessage noRegUser = new OutMessage(12);
                    line = transformOut(noRegUser);
                    output.println(line);
                    LOGGER.info("<< User is not registered >>");
                    break;
                case "regUser":
                    OutMessage regUser = new OutMessage(21, tokens[1]);
                    line = transformOut(regUser);
                    output.println(line);
                    LOGGER.info("<< User is registered >> " + mesage.getLogin());
                    break;
                case "messageAll":
                    writeChatHistoryToFile();
                    sendMsgToAll();
                    break;
                case "messagePrivate":
                    sendMessageToPrivateRoom(mesage.getFromUser(), mesage.getToUser(), mesage.getMsgBody());
                    LOGGER.info("<< Message Private step >>" +
                            mesage.getFromUser() + " " + mesage.getToUser() + " " + mesage.getMsgBody());
                    break;
                case "activeList":
                    ArrayList<String> firstLocalList = getOnlineList();
                    sendList(firstLocalList);
                    //sendChatHistory();
                    LOGGER.info("<< Online users list was sent >>");
                    break;
                case "createPrivateChatRoom":
                    OutMessage msg = new OutMessage(mesage.getFromUser(), 34);
                    String newLine = transformOut(msg);
                    List<ServerWorker> serverWorkers = server.getWorkerList();

                    for (ServerWorker i : serverWorkers) {
                        if (mesage.getToUser().equals(i.getName())) { i.send(newLine); }
                    }
                    //addMembersToGroupChat(mesage.getChatName(), mesage.getLogin());
                    LOGGER.info("<< Create private chat room step. Chat created. >>");
                    break;
                case "logOut":
                    handleLogOff();
                    break;
                case "chatHistory":
                    sendChatHistory();
                    break;
                case "userInfo":
                    sendUserInfo(mesage.getLogin());
                    break;
                case "createGroupChat":
                    createGroupChat();
                    break;
                case "messageToGroup":
                    messageToGroupChat();
                    break;
                case "leaveGroup":
                    deleteUserFromGroup();
                    break;
                case "leavePrivateChat":
                    leavePrivateChat();
                    break;
                case "addMemberToGroup":
                    addMembersToGroupChat(mesage.getChatName(), mesage.getLogin());
                    break;
            }
        }
        socket.close();
    }

    private void leavePrivateChat() {
        List<ServerWorker> localList = server.getWorkerList();
        OutMessage outMessage = new OutMessage(41, mesage.getFromUser());
        String line = transformOut(outMessage);
        for (ServerWorker worker: localList) {
            if (mesage.getToUser().contains(worker.getName())) {
                worker.send(line);
            }
        }
    }

    private void deleteUserFromGroup() {
        ChatRoomsDAOimpl.getInstance().deleteUsrFromChat(mesage.getChatName(), mesage.getLogin());
        List<ServerWorker> serverWorkers = server.getWorkerList();
        List<GroupChatRoom> localChatlist = ChatRoomsDAOimpl.getInstance().getChatRooms(mesage.getChatName());
        List<String> updatedChatUsersList = new ArrayList<>();
        for (GroupChatRoom x: localChatlist) {
            updatedChatUsersList.add(x.getUsername());
        }

        OutMessage outMessage = new OutMessage(mesage.getChatName(), 40, updatedChatUsersList);
        String line = transformOut(outMessage);
        for (ServerWorker worker: serverWorkers) {
            worker.send(line);
        }
    }

    /**
     * The method is used for sending messages only to group members.
     */
    private void messageToGroupChat() {
        List<ServerWorker> workerList = server.getWorkerList();
        List<GroupChatRoom> chatUsersList = ChatRoomsDAOimpl.getInstance().getChatRooms(mesage.getChatName());
        OutMessage outMessage = new OutMessage(mesage.getChatName(), 39,mesage.getFromUser(),
                mesage.getMsgBody());
        String line = transformOut(outMessage);
        for (GroupChatRoom user: chatUsersList) {
            for (ServerWorker worker: workerList) {
                if (worker.getName().equals(user.getUsername()) &
                        user.getGroupName().equals(mesage.getChatName())) {
                    worker.send(line);
                }
            }
        }
        writeChatHistoryToFile();
    }
    /**
     * The method is used for creating group chat room.
     * When room was created the members list would be send
     * to everyone member of that group.
     */
    private void createGroupChat() {
        File file = new File ("./chats/"+mesage.getChatName() + ".txt");
        if (!file.exists() & !file.isDirectory()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<ServerWorker> workerList = server.getWorkerList();
        String line = transformOut(new OutMessage(37, mesage.getChatName(),mesage.getUserChatList()));
        LOGGER.info(line);
        for (String str: mesage.getUserChatList()) {
            ChatRoomsDAOimpl.getInstance().insertInto(mesage.getChatName(), str);
            for (ServerWorker worker: workerList) {
                if (worker.getName().equals(str))
                    worker.send(line);
            }
        }
    }
    /**
     * Here we parse all users from database.
     * And get all additional user info then send it to client.
     */
    private void sendUserInfo(String login) {
        List<User> usersList = UsersDAOimpl.getInstance().getUsers();
        for (User user: usersList) {
            if (login.equals(user.getLogin())) {
                OutMessage outMessage = new OutMessage(38, user.getEmail(),  user.getFirstName(),
                        user.getSecondName(), user.getBirth(), user.getPhoneNumber());
                String line = transformOut(outMessage);
                output.println(line);
            }
        }
    }
    /**
     *
     */
    private void addMembersToGroupChat(String chatName, String login) {
        ChatRoomsDAOimpl.getInstance().insertInto(chatName, login);
        List<GroupChatRoom> localChatlist = ChatRoomsDAOimpl.getInstance().getChatRooms(mesage.getChatName());
        List<String> updatedChatUsersList = new ArrayList<>();
        List<ServerWorker> serverWorkers = server.getWorkerList();
        for (GroupChatRoom x: localChatlist) {
            updatedChatUsersList.add(x.getUsername());
        }
        OutMessage outMessage = new OutMessage(40, chatName, updatedChatUsersList);
        String str = transformOut(outMessage);
        for (ServerWorker serverWorker: serverWorkers) {
            if (serverWorker.getName().equals(login)) {
                serverWorker.send(str);
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
        ArrayList<String> localList = getOnlineList();
        String removeUser = " ";
        List<User> usrList = UsersDAOimpl.getInstance().getUsers();
        if (usrList.size() != 0) {
            for (String index : localList) {
                System.out.println(index);
                for (User user : usrList) {
                    LOGGER.info(user.getLogin() + " user.getName " + mesage.getLogin() + " nameFromUser");
                    if (user.getLogin().equals(mesage.getFromUser())) {
                        removeUser = user.getLogin();
                        UsersDAOimpl.getInstance().updateUsrStatus(user.getId(), 0);
                    }
                }
            }
        }
        onlineUsers.remove(removeUser);
        LOGGER.info(removeUser + ": removed user");
        server.removeWorker(this);
        sendList(localList);
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
     *
     * @param arrayList active users list.
     */
    private void sendList(ArrayList<String> arrayList) {
        String line;
        OutMessage message = new OutMessage(311, arrayList);
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
    private void sendChatHistory() {
        String line;
        readChatHstrFromFile();
        OutMessage message = null;
        List<GroupChatRoom> localChatlist = ChatRoomsDAOimpl.getInstance().getChatRooms(mesage.getChatName());
        List<String> updatedChatUsersList = new ArrayList<>();
        for (GroupChatRoom x: localChatlist) {
            updatedChatUsersList.add(x.getUsername());
        }
        if (mesage.getChatName().contains("General chat"))
            message = new OutMessage(generalChatHistory, 36, mesage.getChatName(), getOnlineList());
        else
            message = new OutMessage(generalChatHistory, 36, mesage.getChatName(), updatedChatUsersList);
        line = transformOut(message);
        output.println(line);
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
     * The method send input client message to Handler class.
     * Handler class checks what action came and send in callback
     * a status so server could understand what client needs.
     *
     * @param line where from you read data from inputstream
     */
    private void preAccess (String line) {
        mesage = transform(line);
        LOGGER.info("Login" + "  " + mesage.getLogin() + " " + mesage.getMsgBody());
        Handler.start(mesage);
    }
    /**
     * We use the method for reading general message chat history.
     * After we read it from json file we send it to every existing user
     * which logged in after some time.
     */
    private void readChatHstrFromFile() {
        generalChatHistory.clear();
        File file = new File("./chats/" + mesage.getChatName() + ".txt");
        String line;
        try (BufferedReader reader1 = new BufferedReader(new FileReader(file))) {
            while ((line = reader1.readLine()) != null) {
                setChatString(line);
                generalChatHistory.add(line);
            }
            //output.println(getChatString());
        } catch (IOException ex) {
            LOGGER.error("Chat file" + mesage.getChatName() + ".txt was not found");
        }

        /*ChatMessages[] object = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("General chat.txt"))){
            object = gson.fromJson(reader, ChatMessages[].class);
        } catch (IOException ex) {
            System.out.println("exception");
        }
        for (int i = 0; i < object.length; i++) {
            System.out.println(object[i]);
        }*/
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
                    sendList(onlineUsers);
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
     */
    private void writeChatHistoryToFile () {
        File file = new File ("./chats/" + mesage.getChatName() + ".txt");
        ChatMessages chatMsg = new ChatMessages(localDate, mesage.getFromUser(), mesage.getMsgBody());
        String line;
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            line = chatMsg.toString();
            fileWriter.write(line);
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.error ("GeneralChat txt file could not be found or written");
        }
    }
    /**
     * The method for sending data to all threads (GeneralChat).
     */
    private void sendMsgToAll () {
        //writeChatHistoryToFile();
        List<ServerWorker> mylist = server.getWorkerList();
        OutMessage messageAll = new OutMessage(mesage.getFromUser(), 32, mesage.getMsgBody());
        String str = transformOut(messageAll);
        for (ServerWorker i : mylist) {
            i.send(str);
        }
        LOGGER.info("<< Message to All step >> " + mesage.getMsgBody());
    }

    public static String getChatString() {
        return chatString;
    }

    public static void setChatString(String chatString) {
        ServerWorker.chatString = chatString;
    }
}
