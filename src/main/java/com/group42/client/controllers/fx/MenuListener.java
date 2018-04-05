package com.group42.client.controllers.fx;

/*
 * Class for process action on menu button
 */

import com.group42.client.controllers.RequestController;
import com.group42.client.model.Model;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.util.Optional;

public class MenuListener implements EventHandler<MouseEvent> {

    /**
     * instance of menu button.
     */
    private ImageView menuButton;

    /**
     * Default constructor needs for initialize fxml form.
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
        userInfo.setOnAction(event1 -> RequestController.getInstance().getUserInfoRequest(Model.getInstance().getUser().getLogin()));
        newGroup.setOnAction(newGroupEvent -> { new CreateGroupListener().setCreateGroupScene(); });
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
