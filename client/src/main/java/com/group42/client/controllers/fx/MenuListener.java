package com.group42.client.controllers.fx;


import com.group42.client.controllers.RequestController;
import com.group42.client.model.Model;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.util.Optional;

/**
 * This class handles actions in the context menu window. Has the following
 * functions: create a group (sends control to the controller to create a
 * group chat) show information about the current user and exit the account.
 */
public class MenuListener implements EventHandler<MouseEvent> {

    /**
     * instance of menu button.
     */
    private ImageView menuButton;

    /**
     * Default constructor is needed to be for correct initialize fxml form.
     */
    public MenuListener() {
    }


    MenuListener(ImageView menuButton) {
        this.menuButton = menuButton;
    }

    /**
     * main method to process event on press button.
     *
     * @param event
     */
    @Override
    public void handle(MouseEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem userInfo = new MenuItem("User Info");
        MenuItem newGroup = new MenuItem("New Group");
        MenuItem logOut = new MenuItem("Log Out");
        userInfo.setOnAction(event1 -> {
            String currUser = Model.getInstance().getUser().getLogin();
            RequestController.getInstance().getUserInfoRequest(currUser);
        }
        );
        newGroup.setOnAction(newGroupEvent -> new CreateGroupListener().setCreateGroupScene());
        logOut.setOnAction(logOutEvent -> processLogOutRequest());
        contextMenu.getItems().addAll(newGroup, userInfo, logOut);
        contextMenu.show(menuButton, event.getScreenX(), event.getScreenY());
    }

    /**
     * Method to process log out request. Change scene to authorisation and
     * send request to server for log out.
     */
    private void processLogOutRequest() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure to log out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            RequestController.getInstance().logOutRequest(Model.getInstance().getUser().getLogin());
            Model.getInstance().setChatHistoryMap(null);
            Model.getInstance().setChatUsersMap(null);
            SceneManager.getInstance().setAuthorisationScene();
        } else alert.close();
    }


}
