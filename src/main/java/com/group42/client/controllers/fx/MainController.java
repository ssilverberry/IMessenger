package com.group42.client.controllers.fx;

import com.group42.client.controllers.ChatIO;
import com.group42.client.model.*;
import com.group42.client.model.factory.*;
import com.group42.client.network.protocol.IncomingServerMessage;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.group42.client.controllers.RequestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController extends Controller {


    /**
     * instance of model
     */
    private Model model;

    /**
     * Logging for exception trace
     */
    private final Logger logger = LogManager.getLogger(MainController.class);

    /**
     * bindings to fxml elements
     */
    @FXML
    private ImageView menuButton;
    @FXML
    private Label chatName;
    @FXML
    private HBox writeBox;
    @FXML
    private TextArea messageField;
    @FXML
    private ImageView sendButton;
    @FXML
    private VBox sendBox;
    @FXML
    private AnchorPane centerPane;
    @FXML
    private ScrollPane scrollPane;

    /**
     * container for chat list
     */
    @FXML
    public ListView<Chat> chatListView;

    /**
     * list of chats, which contains in container
     */
    private ObservableList<Chat> chatList;

    /**
     * Container for chat history
     */
    @FXML
    private TextFlow chatHistoryView;

    /**
     * container for list of online chat users
     */
    @FXML
    private ListView<String> generalUserListView;

    /**
     * container for list of online chat users and choice list.
     */
    @FXML
    private VBox onlineListBox;

    /**
     * Addition button process request to add member to group
     */
    @FXML
    private JFXButton addBtn;

    /**
     * list of
     */
    @FXML
    private ListView<String> choiceList;

    public MainController() {
    }

    /**
     * Process response from server.
     */
    @Override
    public void processResponse(IncomingServerMessage message) {
        Platform.runLater(() -> {
            switch (message.getActionId()){
                case 311:
                    receiveOnlineUsers(message);
                    break;
                case 312:
                    model.setOnlineUserList(null);
                    break;
                case 32:
                    receiveMsgToGroupChat("General chat", message);
                    break;
                case 33:
                    receiveMsgToPrivate(message);
                    break;
                case 34:
                    createPrivateRoom(message.getToUser());
                    break;
                case 36:
                    updateGroupChatHistory(message.getGroupName(), message.getGeneralChatHistory(), message.getMembers());
                    break;
                case 37:
                    createGroupRoom(message.getGroupName(), message.getMembers());
                    break;
                case 38:
                    new UserInfoAlert(Alert.AlertType.INFORMATION, message);
                    break;
                case 39:
                    receiveMsgToGroupChat(message.getGroupName(), message);
                    break;
                case 40:
                    updateGroupListOfMembers(message.getGroupName(), message.getMembers());
                    break;
                case 42:
                    createGroupRoom(message.getGroupName(), message.getMembers());
                    break;
            }
        });
    }

    private void updateGroupListOfMembers(String groupName, List<String> members) {
        Chat chat = getChatByName(groupName);
        if (chat != null) {
            ObservableList<String> tempList = FXCollections.observableArrayList(members);
            model.getChatUsersMap().get(chat).setAll(tempList);
            if (chatListView.getSelectionModel().getSelectedItem().equals(chat)) {
                generalUserListView.setItems(model.getChatUsersMap().get(chat));
            } chat.setHistoryFlag(false);
        }
    }

    /**
     * receive online users list.
     * @param message
     */
    private void receiveOnlineUsers(IncomingServerMessage message) {
        Chat chat = getChatByName("General chat");
        ObservableList<String> onlineList = FXCollections.observableArrayList(message.getOnlineUsers());
        model.getChatUsersMap().put(chat, onlineList);
        model.setOnlineUserList(onlineList);
        if (chatListView.getSelectionModel().getSelectedItem() != null) {
            if (chatListView.getSelectionModel().getSelectedItem().equals(chat)) {
                generalUserListView.setItems(model.getChatUsersMap().get(chat));
            }
        }
    }

    /**
     * init function load after scene setup.
     */
    @FXML
    void initialize() {
        model = Model.getInstance();
        setupChatList();
        chatList = FXCollections.observableArrayList();
        chatList.setAll(model.getChatHistoryMap().keySet());
        initializeFactories();
        setOnlineListListener();
        generalUserListView.setVisible(false);
        centerPane.setVisible(false);
        menuButton.setOnMouseClicked(new MenuListener(menuButton));
        chatListView.setOnContextMenuRequested(this::leaveGroupListener);
        setListenerOnAddBtn();
        setIlluminateListener();
        RequestController.getInstance().getOnlineUsersRequest();
        setLogOutListener();
    }

    /**
     * method to read chatList from file and setup to view list.
     */
    private void setupChatList(){
        Set<Chat> chatSet = new HashSet<>(ChatIO.getInstance().readChatsFromFile());
        model.setChatHistoryMap(FXCollections.observableHashMap());
        model.setChatUsersMap(FXCollections.observableHashMap());
        for (Chat chat : chatSet) {
            model.getChatHistoryMap().put(chat, FXCollections.observableArrayList());
            model.getChatUsersMap().put(chat, FXCollections.observableArrayList());
        }
    }

    /**
     * Cell factories add listeners to lists of data
     */
    private void initializeFactories() {
        chatListView.setCellFactory(new ChatListCellFactory());
        //generalUserListView.setCellFactory(new UserListCellFactory());
        chatListView.setItems(chatList);
    }

    /**
     * set listener on button which responsible for add member to group chat.
     */
    private void setListenerOnAddBtn(){
        addBtn.setOnAction(event -> {
            Chat group =chatListView.getSelectionModel().getSelectedItem();
            if(group != null){
                if (choiceList.getPrefHeight() == 0) {
                    addBtn.setText("ONLINE USERS");
                    choiceList.setPrefHeight(300);
                    choiceList.setItems(model.getOnlineUserList());
                    choiceList.setOnMouseClicked(event1 -> {
                        String choice = choiceList.getSelectionModel().getSelectedItem();
                        if (choice != null){
                            for (String user: model.getChatUsersMap().get(group)) {
                                if (choice.equals(user)){
                                    Alert alert = new Alert((Alert.AlertType.ERROR));
                                    alert.setHeaderText("User is already here!");
                                    alert.showAndWait();
                                    return;
                                }
                            } RequestController.getInstance().joinGroupRequest(group.getChatName(), choice, 0);
                            group.setHistoryFlag(false);
                            choiceList.setPrefHeight(0);
                        }
                    });
                } else {
                    addBtn.setText("ADD MEMBER");
                    choiceList.setPrefHeight(0.0);
                }
            }
        });
    }

    /**
     * Process context menu request by right click of mouse. Context menu contains
     * possibility to create private with one of the online users and shows user info.
     */
    private void setOnlineListListener(){
        generalUserListView.setOnContextMenuRequested(event -> {
            String toUser = generalUserListView.getSelectionModel().getSelectedItem();
            if (toUser != null && !toUser.equals(model.getUser().getLogin())) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem newPrivate = new MenuItem("New Private");
                MenuItem userInfo = new MenuItem("User Info");
                contextMenu.getItems().addAll(newPrivate, userInfo);
                newPrivate.setOnAction(newPrivateEvent -> {
                    createPrivateRoom(toUser);
                    RequestController.getInstance().createPrivateRequest(model.getUser().getLogin(), toUser);
                });
                userInfo.setOnAction(infoEvent -> RequestController.getInstance().getUserInfoRequest(toUser));
                contextMenu.show(menuButton, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Creates new group room with list of members, <tt>members</tt>
     * @param groupName
     * @param members
     */
    private void createGroupRoom(String groupName, List<String> members){
        ObservableList<String> tempList = FXCollections.observableArrayList(members);
        Chat chat = new Chat(groupName, "group");
        chatList.add(chat);
        model.getChatHistoryMap().put(chat, FXCollections.observableArrayList());
        model.getChatUsersMap().put(chat, tempList);
        if (groupName.equals(chatListView.getSelectionModel().getSelectedItem().getChatName())){
            generalUserListView.setItems(tempList);
        } ChatIO.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
        logger.info("2. GROUP CREATED!");
    }

    /**
     * Creates new private room with chosen user, <tt>toUser</tt>
     * @param userName is username of another user.
     */
    private void createPrivateRoom(String userName){
        Chat chat = new Chat(userName, "private");
        chatList.add(chat);
        model.getChatHistoryMap().put(chat, FXCollections.observableArrayList());
        ChatIO.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
        ChatIO.getInstance().createFileForPrivateHistory(userName);
    }

    /**
     * receive message in chat history for chat in <tt>chatName</tt>
     */
    private void receiveMsgToGroupChat (String chatName, IncomingServerMessage message) {
        Chat chat = getChatByName(chatName);
        if (chat != null) {
            model.getChatHistoryMap().get(chat).add(parseMessage(message, chat.getChatName()));
            logger.info("receive message to chat list" + message.getFromUser() + " " + message.getMsgBody());
        }
    }

    /**
     * receive message to private chat.
     */
    private void receiveMsgToPrivate(IncomingServerMessage message) {
        String fromUser = message.getFromUser();
        String toUser = message.getToUser();
        for (ObservableMap.Entry<Chat, ObservableList<Text>> chatList : model.getChatHistoryMap().entrySet()) {
            Chat chat = chatList.getKey();
            if (fromUser.contains(chat.getChatName()) | toUser.contains(chat.getChatName())) {
                model.getChatHistoryMap().get(chat).add(parseMessage(message, chat.getChatName()));
                ChatIO.getInstance().writePrivateHistoryToFile(chat.getChatName(), model.getChatHistoryMap().get(chat));
            }
        }
    }

    /**
     * Get chat by name.
     * @param chatName
     * @return
     */
    private Chat getChatByName(String chatName){
        Chat chat;
        for (ObservableMap.Entry<Chat, ObservableList<Text>> chatList : model.getChatHistoryMap().entrySet()) {
            chat = chatList.getKey();
            if (chatName.contains(chat.getChatName())) {
                return chat;
            }
        } return null;
    }

    /**
     * parse message to show it in chat
     */
    private Text parseMessage(IncomingServerMessage message, String chatName) {
        Text text = new Text(message.getFromUser() + " > " + message.getMsgBody() + "\n");
        text.setFont(new Font(14));
        if (message.getFromUser().contains(model.getUser().getLogin())){
            text.setFill(Color.valueOf("#4357a3"));
        }
        if (chatName.equals(chatListView.getSelectionModel().getSelectedItem().getChatName())){
            chatHistoryView.getChildren().add(text);
        } scrollPane.setVvalue(1.0);
        return text;
    }

    /**
     * Update general chat history after sign in.
     * @param historyList
     */
    private void updateGroupChatHistory(String groupName, List<String> historyList, List<String> members){
        logger.info("SIZE MEMBERS LIST: " + members.size());
        Chat chat = getChatByName(groupName);
        if (chat != null) {
            List<Text> msgText = new ArrayList<>();
            for (String msgString : historyList) {
                Text text = new Text(msgString + "\n");
                text.setFont(Font.font(14));
                if (msgString.contains(model.getUser().getLogin())) {
                    text.setFill(Color.valueOf("#4357a3"));
                } msgText.add(text);
            }
            model.getChatHistoryMap().get(chat).setAll(msgText);
            ObservableList<String> tempList = FXCollections.observableArrayList(members);
            model.getChatUsersMap().get(chat).setAll(tempList);
            generalUserListView.setItems(tempList);
            chatHistoryView.getChildren().setAll(model.getChatHistoryMap().get(chat));
        }
    }

    /**
     * leave group listener.
     * @param event
     */
    private void leaveGroupListener(ContextMenuEvent event){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem leaveGroup = new MenuItem("Leave group");
        contextMenu.getItems().add(leaveGroup);
        contextMenu.show(chatListView, event.getScreenX(), event.getScreenY());
        leaveGroup.setOnAction(leaveEvent -> {
            String currUser = Model.getInstance().getUser().getLogin();
            Chat chat = chatListView.getSelectionModel().getSelectedItem();
            if (chat != null) {
                if (!chat.getChatType().equals("general")) {
                    RequestController.getInstance().leaveGroupRequest(chat.getChatName(), currUser);
                    chatList.remove(chat);
                    model.getChatHistoryMap().remove(chat);
                    model.getChatUsersMap().remove(chat);
                    ChatIO.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
                }
            }
        });
    }

    /**
     * listen to get username from user list and insert into message field.
     */
    @FXML
    private void generalListListener() {
        String user = generalUserListView.getSelectionModel().getSelectedItem();
        if (user != null) {
            if (!messageField.getText().contains(" " + user + ", ")) {
                messageField.setText(messageField.getText() + " " + user + ", ");
                messageField.positionCaret(messageField.getText().length());
            }
        }
    }

    /**
     * listen to select chat and set chat history for current chat
     */
    @FXML
    private void chatListListener() {
        if (chatListView.getSelectionModel().getSelectedItem() != null) {
            Chat chat = chatListView.getSelectionModel().getSelectedItem();
            chatName.setText(chat.getChatName() + "(" + model.getUser().getLogin() + ")");
            if (chat.getChatType().equals("group") | chat.getChatType().equals("general")){
                setGroupChatView(chat);
            } else if (chat.getChatType().equals("private")){
                setPrivateChatView(chat);
            }
            chatHistoryView.getChildren().setAll(model.getChatHistoryMap().get(chat));
            addSendingMessages(chat);
        }
        chatName.setVisible(true);
        generalUserListView.setVisible(true);
        centerPane.setVisible(true);
    }

    /**
     * enable sending message listener for each chat.
     * @param chat is current chat
     */
    private void addSendingMessages(Chat chat) {
        sendButton.setOnMouseClicked(event -> {
            sendMsgToChat(chat);
        });
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                messageField.setCursor(Cursor.DEFAULT);
                sendMsgToChat(chat);
            }
        });
    }

    /**
     * change center pane of main scene to group chat.
     * @param chat
     */
    private void setGroupChatView(Chat chat) {
        generalUserListView.setItems(model.getChatUsersMap().get(chat));
        onlineListBox.setPrefWidth(175.0);
        onlineListBox.setVisible(true);
        chat.setHistoryFlag(false);
        if (!chat.getHistoryFlag()) {
            RequestController.getInstance().getGroupChatHistoryRequest(chat.getChatName());
            chat.setHistoryFlag(true);
        }
        if (!chat.getChatType().equals("private")){
            if (chat.getChatType().equals("general")){
                choiceList.setPrefHeight(0.0);
                addBtn.setOnAction(null);
                addBtn.setText("ONLINE USERS");
            } else {
                addBtn.setText("ADD MEMBER");
                setListenerOnAddBtn();
            }

        } else {
            addBtn.setMinHeight(0.0);
            addBtn.setPrefHeight(0.0);
        }
    }

    /**
     * change center pane of main scene to private chat.
     * @param chat
     */
    private void setPrivateChatView(Chat chat) {
        List<Text> historyList = ChatIO.getInstance().readChatHistoryFromFile(chat.getChatName());
        model.getChatHistoryMap().get(chat).setAll(historyList);
        onlineListBox.setPrefWidth(0.0);
        onlineListBox.setVisible(false);
    }

    /**
     * send request with message to specific chat.
     */
    private void sendMsgToChat(Chat chat) {
        String message = messageField.getText();
        String chatName = chat.getChatName();
        if (!message.isEmpty()){
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + " ";
            String currUser = model.getUser().getLogin();
            switch (chat.getChatType()) {
                case "general":
                    RequestController.getInstance().messageToGeneralChat(time + currUser, message);
                    break;
                case "private":
                    RequestController.getInstance().messageToPrivateChat(time + currUser, chatName, message);
                    break;
                default:
                    RequestController.getInstance().messageToGroupRequest(chatName, time + currUser, message);
                    break;
            }
            reduceToDefaultSize();
        }
    }

    /**
     * listening to resize message field.
     */
    @FXML
    private void messageFieldListener() {
        if (messageField.getText().contains("\n") && messageField.getText().length() == 1)
            messageField.clear();
        if (messageField.getScrollTop() > 0.0) {
            if (writeBox.getMinHeight() + 10 < writeBox.getMaxHeight()) {
                messageField.setScrollTop(0.0);
                increaseMessageField();
            }
        }
    }

    /**
     * Automatically increases  message field, if it necessary.
     */
    private void increaseMessageField(){
        writeBox.setMinHeight(writeBox.getMinHeight() + 20);
        messageField.setMinHeight(messageField.getMinHeight() + 20);
        sendBox.setPadding(new Insets(sendBox.getPadding().getTop() + 20, sendBox.getPadding().getRight(),
                sendBox.getPadding().getBottom(), sendBox.getPadding().getLeft()));
    }

    /**
     * Reduced message field to default size
     */
    private void reduceToDefaultSize(){
        messageField.clear();
        writeBox.setMinHeight(40);
        messageField.setMinHeight(30);
        sendBox.setPadding(new Insets(0, sendBox.getPadding().getRight(),
                sendBox.getPadding().getBottom(), sendBox.getPadding().getLeft()));
    }

    /**
     * illuminate and unlit next buttons.
     */
    private void setIlluminateListener(){
        menuButton.setOnMouseEntered(event -> menuButton.setOpacity(1.0));
        menuButton.setOnMouseExited(event -> menuButton.setOpacity(0.8));
        sendButton.setOnMouseEntered(event -> sendButton.setOpacity(1.0));
        sendButton.setOnMouseExited(event -> sendButton.setOpacity(0.7));
    }

    /**
     * log out request.
     */
    private void setLogOutListener() {
        SceneManager.getInstance().getPrimaryStage().setOnCloseRequest(event -> {
            RequestController.getInstance().logOutRequest(model.getUser().getLogin());
        });
    }
}
