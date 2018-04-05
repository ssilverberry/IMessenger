package com.group42.client.controllers;

/**
 * Class for send request to server.
 */

import com.group42.client.network.NetworkController;
import com.group42.client.network.protocol.OutputClientMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

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
                                    String dateOfBirth, String email, String login, String password) {
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
     * @param groupName
     */
    public void getGroupChatHistoryRequest(String groupName) {
        OutputClientMessage outMsg = new OutputClientMessage(36, groupName, new ArrayList<>());
        NetworkController.getInstance().send(outMsg);
        logger.info("GET CHAT HISTORY REQUEST: " + groupName);
    }

    /**
     * send message to group chat request.
     */
    public void messageToGroupRequest(String groupName, String fromUser, String msgBody) {
        OutputClientMessage outMsg = new OutputClientMessage(39, groupName, fromUser, msgBody);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND MSG TO GROUP: " + groupName + " " + fromUser + " " + msgBody);
    }

    /**
     * send message to general chat request.
     */
    public void messageToGeneralChat(String fromUser, String message){
        OutputClientMessage outMsg = new OutputClientMessage("General chat", fromUser, 32, message);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND MESSAGE TO GENERAL CHAT REQUEST TO SERVER: " + fromUser + " " + message);
    }

    /**
     * send message to private chat request.
     * @param fromUser
     * @param toUser
     * @param message
     */
    public void messageToPrivateChat(String fromUser, String toUser, String message){
        OutputClientMessage outMsg = new OutputClientMessage(fromUser, toUser, message, 33);
        NetworkController.getInstance().send(outMsg);
        logger.info("SEND MSG TO PRIVATE CHAT: " + fromUser + " " + toUser + " " + message);
    }

    /**
     *  send create group request
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
     * Get user info request.
     * @param login
     */
    public void getUserInfoRequest(String login){
        OutputClientMessage outMsg = new OutputClientMessage(login, 38);
        NetworkController.getInstance().send(outMsg);
        logger.info("USER INFO REQUEST: " + login);
    }

    /**
     * send leave group request.
     * @param groupName
     * @param login
     */
    public void leaveGroupRequest(String groupName, String login) {
        OutputClientMessage outMsg = new OutputClientMessage(groupName, 40, login, 40);
        NetworkController.getInstance().send(outMsg);
        logger.info("LEAVE GROUP INFO");
    }

    /**
     * send join group request.
     * @param groupName
     * @param login
     */
    public void joinGroupRequest(String groupName, String login, Integer temp) {
        OutputClientMessage outMsg = new OutputClientMessage(groupName, 42, login, 40);
        NetworkController.getInstance().send(outMsg);
        logger.info("INVITE TO GROUP INFO: " + "group: " + groupName + " user: " + login);
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
