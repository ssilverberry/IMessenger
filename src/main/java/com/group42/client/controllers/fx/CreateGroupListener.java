package com.group42.client.controllers.fx;

/*
 * Class controller for load <tt>CreateGroupView</tt> form
 * and creates group room.
 */

import com.group42.client.controllers.RequestController;
import com.group42.client.model.Model;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupListener {


    /**
     * Logging for exception trace
     */
    private final Logger logger = LogManager.getLogger(CreateGroupListener.class);

    /**
     * binding with fields in view
     */
    @FXML
    private JFXTextField groupNameField;
    @FXML
    private ListView<String> chatUsersListView;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton createButton;

    /**
     * Default constructor is needed to be correct for scene loading.
     */
    CreateGroupListener() {
    }

    /**
     * set scene for create group request.
     */
    public void setCreateGroupScene(){
        Stage createGroupStage = new Stage();
        try {
            String addMembersView = "view/CreateGroupView.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(addMembersView));
            createGroupStage.setScene(new Scene(fxmlLoader.load()));
            createGroupStage.setResizable(false);
            createGroupStage.show();
        } catch (IOException e) {
            logger.error("Create group stage don't initialized!", e);
        }
    }

    /**
     * init function listeners and fill containers when scene loaded.
     */
    @FXML
    void initialize() {
        createButton.setOnMouseClicked(event -> process());
        ObservableList<String> userList = FXCollections.observableArrayList();
        userList.addAll(Model.getInstance().getOnlineUsers());
        userList.addAll(Model.getInstance().getOfflineUsers());
        String currUser = null;
        for (String user: userList) {
            if (user.equals(Model.getInstance().getUser().getLogin())){
                currUser = user;
                break;
            }
        } userList.remove(currUser);
        chatUsersListView.setItems(userList);
        chatUsersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * process request for create group room.
     */
    private void process() {
        List<String> members = new ArrayList<>(chatUsersListView.getSelectionModel().getSelectedItems());
        members.add(Model.getInstance().getUser().getLogin());
        String groupName = groupNameField.getText();
        if (!checkForEmptyForm()) {
            RequestController.getInstance().createGroupRequest(groupName, members);
            closeRequest();
        }
    }

    /**
     * close scene for create group.
     */
    @FXML
    private  void closeRequest() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Check input group name field for empty
     *
     */
    private boolean checkForEmptyForm() {
        boolean emptyFlag = false;
        if (chatUsersListView.getSelectionModel().getSelectedItems() == null){
            chatUsersListView.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (groupNameField.getText().isEmpty()) {
            groupNameField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        } return emptyFlag;
    }
}
