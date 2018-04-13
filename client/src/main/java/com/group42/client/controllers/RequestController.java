package com.group42.client.controllers;

import com.group42.client.model.Model;
import com.group42.client.protocol.OutputClientMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.List;

/**
 * This class forms a request to the server, and then
 * and then sends it using NetworkController.
 */
public class RequestController {

    /**
     * Instance of RequestController class
     */
    private static final RequestController INSTANCE = new RequestController();

    private final Logger logger = LogManager.getLogger(RequestController.class);

    private RequestController() {
    }

    public static RequestController getInstance() {
        return INSTANCE;
    }

    public void authorizationRequest(String login, String password){
        OutputClientMessage outMsg = new OutputClientMessage(1, login, password);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND AUTHORIZATION REQUEST TO SERVER: " + login + " " + password);
    }

    /**
     *  Send request for registration.
     *
     */
    public void registrationRequest(String firstName, String lastName, String phoneNumber,
                                    LocalDate dateOfBirth, String email, String login, String password) {
        OutputClientMessage outMsg = new OutputClientMessage(2, firstName, lastName, phoneNumber,
                                                                dateOfBirth, email, login, password);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND REGISTRATION REQUEST TO SERVER: ");
    }

    /**
     * send request for get online users.
     */
    public void getOnlineUsersRequest() {
        OutputClientMessage outMsg = new OutputClientMessage(31);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND GET ONLINE USERS REQUEST TO SERVER ");
    }

    /**
     * send request to get group chat history.
     *
     * @param chatId
     */
    public void getGroupChatHistoryRequest(Integer chatId) {
        String currUser = Model.getInstance().getUser().getLogin();
        OutputClientMessage outMsg = new OutputClientMessage(chatId, 36, currUser);
        NetworkController.getInstance().send(outMsg);
        logger.info("GET CHAT HISTORY REQUEST: " + chatId);
    }

    /**
     * send message to chat request.
     * @param chatId
     * @param fromUser
     * @param msgBody
     */
    public void sendMsgToChatRequest(Integer chatId, String fromUser, String msgBody){
        OutputClientMessage outMsg = new OutputClientMessage(chatId , fromUser, 32, msgBody);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND MSG TO GROUP: " + chatId + " " + fromUser + " " + msgBody);
    }

    /**
     *  send create group request
     *
     * @param groupName
     * @param members
     */
    public void createGroupRequest(String groupName, List<String> members){
        OutputClientMessage outMsg = new OutputClientMessage(37, groupName, members);
        NetworkController.getInstance().send(outMsg);
        logger.info("1. CREATE GROUP REQUEST: " + groupName + " " + members.size());
    }

    /**
     * create private chat request.
     * @param fromUser
     * @param toUser
     */
    public void createPrivateRequest(String fromUser, String toUser) {
        OutputClientMessage outMsg = new OutputClientMessage(fromUser, toUser, 34);
        NetworkController.getInstance().send(outMsg);
    }

    /**
     * get user info request.
     * @param login
     */
    public void getUserInfoRequest(String login){
        OutputClientMessage outMsg = new OutputClientMessage(login, 38);
        NetworkController.getInstance().send(outMsg);
        logger.info("USER INFO REQUEST: " + login);
    }

    /**
     * send leave group request.
     * @param chatId
     * @param login
     */
    public void leaveGroupRequest(Integer chatId, String login) {
        OutputClientMessage outMsg = new OutputClientMessage(chatId,  login, 40);
        NetworkController.getInstance().send(outMsg);
        logger.info("LEAVE GROUP INFO");
    }

    /**
     * send join group request.
     * @param chatId
     * @param login
     */
    public void joinGroupRequest(Integer chatId, String login) {
        OutputClientMessage outMsg = new OutputClientMessage(chatId, 42, login);
        NetworkController.getInstance().send(outMsg);
        logger.info("INVITE TO GROUP INFO: " + "group: " + chatId + " user: " + login);
    }

    /**
     * get chat list request.
     */
    public void getChatListRequest(){
        OutputClientMessage outMsg = new OutputClientMessage(43, Model.getInstance().getUser().getLogin());
        NetworkController.getInstance().send(outMsg);
        logger.info("GET CHAT LIST REQUEST");
    }


    /**
     * send log uot request.
     */
    public void logOutRequest(String fromUser) {
        OutputClientMessage outMsg = new OutputClientMessage(35, fromUser);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND LOG OUT REQUEST TO SERVER: " + fromUser);
    }
}
