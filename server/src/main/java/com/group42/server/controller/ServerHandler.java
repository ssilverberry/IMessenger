package com.group42.server.controller;

import com.group42.server.protocol.InputMessage;
import com.group42.server.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

/**
 * ServerHandler class.
 *
 * The class is used for processing all input messages from client
 * and handle all events. Then it passes all control actions to
 * client threads.
 * */
public class ServerHandler {
    private static String status;
    private static String userId;
    private static HashSet<User> userList = new HashSet<>();
    private static List<User> usrList;
    private static final Logger lOGGER = LogManager.getLogger(ServerHandler.class);

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String line) {
        status = line;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getResponse() {
        return status;
    }

    /**
     * The main method where we handle all input cases which
     * client send to server.
     *
     * @param object input message
     * */
    public static void start(InputMessage object) {
        if (object != null) {
            switch (object.getActionId()) {
                case 1:
                    lOGGER.info("case 1");
                    if(!authorized(object))
                        registered(object);
                    break;
                case 2:
                    lOGGER.info("case 2");
                    registered(object);
                    break;
                case 32:
                    lOGGER.info("case 32");
                    setStatus("messageAll");
                    break;
                case 34:
                    lOGGER.info("case 34");
                    setStatus("createPrivateChatRoom");
                    break;
                case 35:
                    lOGGER.info("case 35");
                    setStatus("logOut");
                    break;
                case 31:
                    lOGGER.info("case 31");
                    setStatus("activeList");
                    break;
                case 36:
                    lOGGER.info("case 36");
                    setStatus("chatHistory");
                    break;
                case 37:
                    lOGGER.info("case 37");
                    setStatus("createGroupChat");
                    break;
                case 38:
                    lOGGER.info("case 38");
                    setStatus("userInfo");
                    break;
                case 39:
                    lOGGER.info("case 39");
                    setStatus("messageToGroup");
                    break;
                case 40:
                    lOGGER.info("case 40");
                    setStatus("leaveChat");
                    break;
                case 42:
                    lOGGER.info("case 42");
                    setStatus("addMemberToGroup");
                    break;
                case 43:
                    lOGGER.info("case 43");
                    setStatus("sendUserChatsList");
                    break;
            }
        }
    }
    /**
     * This method is used for creating a new user credentials in database.
     *
     * @param object input data which client send in case of registration process
     *               on client side.
     */
    private static void registration(InputMessage object) {
        lOGGER.debug(object.getLogin()+ " "  +object.getPassword()+ " "  +
                object.getEmail()+ " "  +object.getSecondName()+ " "  +
                object.getBirthday()+ " "  + object.getPhoneNumber() + " "  +
                object.getFirstName());
        if (UsersDAOimpl.getInstance().getUsers().size() != 0) {
            for (User user: UsersDAOimpl.getInstance().getUsers()) {
                if (user.getLogin().equals(object.getLogin())) {
                    setStatus("regFailed");
                    return;
                }
            }
            UsersDAOimpl.getInstance().insertInto(object.getLogin(), object.getPassword(),
                    object.getEmail(), object.getSecondName(), java.sql.Date.valueOf(object.getBirthday()),
                    object.getPhoneNumber(), object.getFirstName());
            setStatus("regUser " + object.getLogin());
        } else {
            UsersDAOimpl.getInstance().insertInto(object.getLogin(), object.getPassword(),
                    object.getEmail(), object.getSecondName(), java.sql.Date.valueOf(object.getBirthday()),
                    object.getPhoneNumber(), object.getFirstName());
            setStatus("regUser " + object.getLogin());
        }
        int chat_id = ChatRoomsDAOimpl.getInstance().getChatIdByName("General chat");
        int user_id = UsersDAOimpl.getInstance().getUserIdByName(object.getLogin());
        ChatRoomsDAOimpl.getInstance().insertIntoChatUsers(chat_id, user_id);
    }
    /**
     * The method is used for authorizing user in case of starting client application.
     * Also the method check user credentials (login and password) if they equal to
     * that one what stores in database then user will be logged in system and he will
     * have access to his / her chats.
     * 13
     * @param msgUsr input information what
     */
    private static boolean authorized(InputMessage msgUsr) {
        if (msgUsr.getActionId() == 1) {
            usrList = UsersDAOimpl.getInstance().getUsers();
            if (usrList.size() != 0) {
                for (User user : usrList) {
                    lOGGER.debug("password from database: " +
                            user.getPassword() + " " + "password from client " + msgUsr.getPassword());
                    if (user.getLogin().equals(msgUsr.getLogin()) & user.getPassword().equals(msgUsr.getPassword())) {
                        if (UsersDAOimpl.getInstance().getUserStatus(user.getLogin()) == 0) {
                            setStatus("access " + user.getLogin());
                            return true;
                        } else {
                            setStatus("accessDenied");
                            return false;
                        }

                    } else {
                        setStatus("noRegUser");
                    }
                }
            } else {
                setStatus("noRegUser");
            }
        }
        return false;
    }
    /**
     * The method check if user was already registered in system.
     * In case of 'Not registered' system will prompt message to client about
     * registration process for current client.
     *
     * @param usr input client data
     * @return 'false' by default and 'true' in case of user is already exist.
     */
    private static boolean registered(InputMessage usr) {
        if (usr.getActionId() == 2) {
            registration(usr);
            return true;
        }
        return false;
    }
}
