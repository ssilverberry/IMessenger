package com.group42.client.controllers.fx;

import com.group42.client.controllers.ChatIOController;
import com.group42.client.model.*;
import com.group42.client.model.factory.*;
import com.group42.client.protocol.IncomingServerMessage;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.group42.client.controllers.RequestController;
import org.controlsfx.control.Notifications;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This is the class controller for the main chat window. It is necessary for such
 * purposes: selecting a chat from the chat list, writing messages, viewing online/offline
 * users, creating private chats, adding users to the group, viewing user information,
 * logging out of the group, and also transferring control to additional controllers.
 */
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
     * containers for online/offline user lists.
     */
    @FXML
    private ListView<String> onlineUserList;
    @FXML
    private ListView<String> offlineUserList;

    /**
     * this two tabs of tabPane which consists of users list. Uses for
     * view online/offline users and also can view list of members for
     * group chat and general list(online with offline users list) to
     * add member.
     */
    @FXML
    private Tab firstTab;
    @FXML
    private Tab secondTab;

    /**
     * container for list of online chat users and choice list.
     */
    @FXML
    private VBox onlineListBox;

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
                    receiveOnlineOfflineUsers(message);
                    break;
                case 32:
                    receiveMsgToChat(message.getChatId(), message);
                    break;
                case 34:
                    createPrivateRoom(message.getChatId(), message.getToUser());
                    break;
                case 36:
                    updateGroupChatHistory(message.getChatId(), message.getGeneralChatHistory(), message.getMembers());
                    break;
                case 37:
                    createGroupRoom(message.getChatId(), message.getGroupName(), message.getMembers());
                    break;
                case 38:
                    new UserInfoAlert(Alert.AlertType.INFORMATION, message);
                    break;
                case 40:
                    updateGroupListOfMembers(message.getChatId(), message.getMembers());
                    break;
                case 41:
                    leavePrivateChat(message.getChatId());
                    break;
                case 42:
                    createGroupRoom(message.getChatId(), message.getGroupName(), message.getMembers());
                    break;
                case 43:
                    updateChatList(message.getLocalUsrChatList());
                    break;
            }
        });
    }

    /**
     * remove private chat, when one of two left it.
     *
     * @param chatId
     */
    private void leavePrivateChat(Integer chatId) {
        Chat chat = getChatById(chatId);
        if (chat != null){
            chatList.remove(chat);
            model.getChatHistoryMap().remove(chat);
            ChatIOController.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
            chatName.setVisible(false);
            centerPane.setVisible(false);
        }
    }

    /**
     * receive msg to chat by id.
     * @param chatId
     * @param message
     */
    private void receiveMsgToChat(Integer chatId, IncomingServerMessage message) {
        Chat chat = getChatById(chatId);
        if (chat != null) {
            model.getChatHistoryMap().get(chat).add(parseMessage(message, chat.getChatName()));
            if (chatListView.getSelectionModel().getSelectedItem().equals(chat)) {
                chatHistoryView.getChildren().setAll(model.getChatHistoryMap().get(chat));
            } else pushNotification(chat, parseMessage(message, chat.getChatName()).getText());
            logger.info("receive message to chat" + message.getFromUser() + " " + message.getMsgBody());
            if (chat.getIsPrivate().equals("private")){
                ChatIOController.getInstance().writePrivateHistoryToFile(chat.getChatName(), model.getChatHistoryMap().get(chat));
            }
        }
    }

    /**
     * show popup window to notify user about message in another chat.
     * @param chat
     * @param contentText
     */
    private void pushNotification(Chat chat, String contentText){
        Notifications notificationBuilder = Notifications.create()
                .title(chat.getChatName())
                .text(contentText)
                .hideAfter(Duration.seconds(5.0))
                .position(Pos.BOTTOM_RIGHT)
                .onAction(event -> chatListView.getSelectionModel().select(chat));
        notificationBuilder.showConfirm();
    }

    /**
     * update chat list after initialize main scene.
     * @param usrChatList
     */
    private void updateChatList(Chat[] usrChatList) {
        chatList.setAll(usrChatList);
        for (Chat chat : usrChatList) {
            model.getChatHistoryMap().put(chat, FXCollections.observableArrayList());
        }
        ChatIOController.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
    }

    /**
     * Updates list of group members, when someone was added or left group.
     *
     * @param chatId
     * @param members
     */
    private void updateGroupListOfMembers(Integer chatId, List<String> members) {
        Chat chat = getChatById(chatId);
        if (chat != null) {
            ObservableList<String> tempList = FXCollections.observableArrayList(members);
            model.getChatUsersMap().get(chat).setAll(tempList);
            if (chatListView.getSelectionModel().getSelectedItem().equals(chat)) {
                onlineUserList.setItems(model.getChatUsersMap().get(chat));
            }
        }
    }

    /**
     * receive list of online/offline users.
     * @param message
     */
    private void receiveOnlineOfflineUsers(IncomingServerMessage message) {
        Chat chat = getChatByName("General chat");
        model.setOnlineUsers(message.getOnlineUsers());
        model.setOfflineUsers(message.getOfflineUsers());
        if (chatListView.getSelectionModel().getSelectedItem() != null) {
            if (chatListView.getSelectionModel().getSelectedItem().equals(chat)) {
                onlineUserList.setItems(FXCollections.observableArrayList(model.getOnlineUsers()));
                offlineUserList.setItems(FXCollections.observableArrayList(model.getOfflineUsers()));
            }
        }
    }

    /**
     * init function load after scene setup.
     */
    @FXML
    void initialize() {
        model = Model.getInstance();
        chatList = FXCollections.observableArrayList();
        setListenerOnUserList(onlineUserList);
        setListenerOnUserList(offlineUserList);
        centerPane.setVisible(false);
        menuButton.setOnMouseClicked(new MenuListener(menuButton));
        chatListView.setOnContextMenuRequested(this::leaveGroupListener);
        setIlluminateListener();
        RequestController.getInstance().getChatListRequest();
        RequestController.getInstance().getOnlineUsersRequest();
        initializeFactories();
        setLogOutListener();
    }

    /**
     * Cell factories add listeners to lists of data
     */
    private void initializeFactories() {
        chatListView.setCellFactory(new ChatListCellFactory());
        chatListView.setItems(chatList);
        onlineUserList.setItems(FXCollections.observableArrayList());
        offlineUserList.setItems(FXCollections.observableArrayList());
    }

    /**
     * Process context menu request by right click of mouse. Context menu contains
     * possibility to create private with one of the online users and shows user info.
     */
    private void setListenerOnUserList(ListView<String> userList){
        userList.setOnContextMenuRequested(event -> {
            String toUser = userList.getSelectionModel().getSelectedItem();
            if (toUser != null && !toUser.equals(model.getUser().getLogin())) {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem newPrivate = new MenuItem("New Private");
                MenuItem userInfo = new MenuItem("User Info");
                contextMenu.getItems().addAll(newPrivate, userInfo);
                newPrivate.setOnAction(newPrivateEvent -> {
                    RequestController.getInstance().createPrivateRequest(model.getUser().getLogin(), toUser);
                });
                userInfo.setOnAction(infoEvent -> RequestController.getInstance().getUserInfoRequest(toUser));
                contextMenu.show(menuButton, event.getScreenX(), event.getScreenY());
            }
        });
        userList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY){
                String user = userList.getSelectionModel().getSelectedItem();
                if (user != null) {
                    if (!messageField.getText().contains(" " + user + ", ")) {
                        messageField.setText(messageField.getText() + " " + user + ", ");
                        messageField.positionCaret(messageField.getText().length());
                    }
                }
            }
        });
    }

    /**
     * Creates new group room with list of <tt>members</tt>
     * @param groupName
     * @param members
     */
    private void createGroupRoom(Integer chatId, String groupName, List<String> members){
        ObservableList<String> membersList = FXCollections.observableArrayList(members);
        Chat chat = new Chat(chatId, groupName, "0");
        chatList.add(chat);
        chatListView.setItems(chatList);
        model.getChatHistoryMap().put(chat, FXCollections.observableArrayList());
        model.getChatUsersMap().put(chat, membersList);
        ChatIOController.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
        pushNotification(chat, "You have been invited to a new group!");
        logger.info("2. GROUP CREATED!");
    }

    /**
     * Creates new private room with chosen user, <tt>toUser</tt>
     * @param userName is username of another user.
     */
    private void createPrivateRoom(Integer chatId, String userName){
        Chat chat = new Chat(chatId, userName, "1");
        chatList.add(chat);
        model.getChatHistoryMap().put(chat, FXCollections.observableArrayList());
        ChatIOController.getInstance().writeChatsToFile(model.getChatHistoryMap().keySet());
        ChatIOController.getInstance().createFileForPrivateHistory(userName);
    }

    /**
     * Get chat by name.
     * @param chatId
     * @return
     */
    private Chat getChatById(Integer chatId){
        Chat chat;
        for (ObservableMap.Entry<Chat, ObservableList<Text>> chatList : model.getChatHistoryMap().entrySet()) {
            chat = chatList.getKey();
            if (chatId == chat.getChatId()) {
                return chat;
            }
        } return null;
    }

    /**
     * Return chat by id
     * @param chatName
     * @return
     */
    private Chat getChatByName(String chatName){
        Chat chat;
        for (ObservableMap.Entry<Chat, ObservableList<Text>> chatList : model.getChatHistoryMap().entrySet()) {
            chat = chatList.getKey();
            if (chatName.equals(chat.getChatName())) {
                return chat;
            }
        } return null;
    }

    /**
     * parse message from String to Text object to show it in message container.
     */
    private Text parseMessage(IncomingServerMessage message, String chatName) {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")) + " ";
        Text text = new Text( dateTime + message.getFromUser() + " > " + message.getMsgBody() + "\n");
        text.setFont(new Font(14));
        if (message.getFromUser().equals(model.getUser().getLogin())){
            text.setFill(Color.valueOf("#4357a3"));
        }
        if (chatName.equals(chatListView.getSelectionModel().getSelectedItem().getChatName())){
            chatHistoryView.getChildren().add(text);
        } scrollPane.setVvalue(1.0);
        return text;
    }

    /**
     * parse history from string values to <tt>Text</tt> objects
     * for display to user.
     *
     * @param chatHistory
     * @return
     */
    private List<Text> parseChatHistoryToDisplay(List<String> chatHistory) {
        List<Text> msgText = new ArrayList<>();
        for (String msgString : chatHistory) {
            Text text = new Text(msgString + "\n");
            text.setFont(Font.font(14));
            if (msgString.contains(model.getUser().getLogin())) {
                text.setFill(Color.valueOf("#4357a3"));
            } msgText.add(text);
        } return msgText;
    }

    /**
     * Update group chat history and members
     * @param historyList
     */
    private void updateGroupChatHistory(Integer chatId, List<String> historyList, List<String> members){
        Chat chat = getChatById(chatId);
        if (chat != null) {
            model.getChatHistoryMap().get(chat).setAll(parseChatHistoryToDisplay(historyList));
            ObservableList<String> tempList = FXCollections.observableArrayList(members);
            model.getChatUsersMap().put(chat, tempList);
            if (chat.getChatName().equals("General chat")) {
                onlineUserList.setItems(FXCollections.observableArrayList(model.getOnlineUsers()));
                offlineUserList.setItems(FXCollections.observableArrayList(model.getOfflineUsers()));
            } else if (chat.getIsPrivate().equals("0") & !chat.getChatName().equals("General chat")){
                onlineUserList.setItems(FXCollections.observableArrayList(model.getChatUsersMap().get(chat)));
            }
            chatHistoryView.getChildren().setAll(model.getChatHistoryMap().get(chat));
        }
    }

    /**
     * listen to call context menu on chat list and process
     * request to leave group.
     * @param event
     */
    private void leaveGroupListener(ContextMenuEvent event){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem leaveGroup = new MenuItem("Leave chat");
        contextMenu.getItems().add(leaveGroup);
        if (!contextMenu.isShowing()) {
            contextMenu.show(chatListView, event.getScreenX(), event.getScreenY());
            leaveGroup.setOnAction(leaveEvent -> {
                String currUser = Model.getInstance().getUser().getLogin();
                Chat chat = chatListView.getSelectionModel().getSelectedItem();
                if (chat != null) {
                    RequestController.getInstance().leaveGroupRequest(chat.getChatId(), currUser);
                    chatList.remove(chat);
                    model.getChatHistoryMap().remove(chat);
                    model.getChatUsersMap().remove(chat);
                    chatName.setVisible(false);
                    centerPane.setVisible(false);
                    chatListView.setFocusModel(null);
                }
            });
        }
    }

    /**
     * listen to select chat and set chat view for different type of chats.
     */
    @FXML
    private void chatListListener() {
        if (chatListView.getSelectionModel().getSelectedItem() != null) {
            Chat chat = chatListView.getSelectionModel().getSelectedItem();
            chatName.setText(chat.getChatName() + " (" + model.getUser().getLogin() + ")");
            if (chat.getIsPrivate().equals("0")){
                setGroupChatView(chat);
            } else {
                setPrivateChatView(chat);
            }
            chatHistoryView.getChildren().setAll(model.getChatHistoryMap().get(chat));
            addSendingMessages(chat);
            RequestController.getInstance().getGroupChatHistoryRequest(chat.getChatId());
            chatName.setVisible(true);
            centerPane.setVisible(true);
        }
    }

    /**
     * enable sending message listener for parametr <tt>chat</tt>.
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
     * change center pane of main scene to set group chat view.
     * @param chat
     */
    private void setGroupChatView(Chat chat) {
        onlineListBox.setPrefWidth(175.0);
        onlineListBox.setVisible(true);
        if (chat.getChatName().equals("General chat")) {
            firstTab.setText("online users");
            secondTab.setText("offline users");
            onlineUserList.setItems(FXCollections.observableArrayList(model.getOnlineUsers()));
            offlineUserList.setItems(FXCollections.observableArrayList(model.getOfflineUsers()));
            offlineUserList.setOnContextMenuRequested(null);
        } else {
            firstTab.setText("members");
            secondTab.setText("add member");
            onlineUserList.setItems(model.getChatUsersMap().get(chat));
            ObservableList<String> tempList = FXCollections.observableArrayList(model.getOnlineUsers());
            tempList.addAll(model.getOfflineUsers());
            offlineUserList.getItems().setAll(tempList);
            listenForAddMemberReq();
        }
    }

    /**
     * listen when user want to add member to group.
     */
    private void listenForAddMemberReq(){
        offlineUserList.setOnContextMenuRequested(event -> {
            Chat group = chatListView.getSelectionModel().getSelectedItem();
            if(group != null){
                String choice = offlineUserList.getSelectionModel().getSelectedItem();
                if (choice != null) {
                    for (String user : model.getChatUsersMap().get(group)) {
                        if (choice.equals(user)) {
                            Alert alert = new Alert((Alert.AlertType.ERROR));
                            alert.setHeaderText("User is already here!");
                            alert.showAndWait();
                            return;
                        }
                    } RequestController.getInstance().joinGroupRequest(group.getChatId(), choice);
                }
            }
        });
    }

    /**
     * change center pane of main scene to set private chat view.
     * @param chat
     */
    private void setPrivateChatView(Chat chat) {
        onlineListBox.setPrefWidth(0.0);
        onlineListBox.setVisible(false);
        List<Text> historyList = ChatIOController.getInstance().readChatHistoryFromFile(chat.getChatName());
        model.getChatHistoryMap().get(chat).setAll(historyList);

    }

    /**
     * send request with message to parameter <tt>chat</tt>.
     */
    private void sendMsgToChat(Chat chat) {
        String message = messageField.getText();
        Integer chatId = chat.getChatId();
        if (!message.isEmpty()){
            String currUser = model.getUser().getLogin();
            RequestController.getInstance().sendMsgToChatRequest(chatId, currUser, message);
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
