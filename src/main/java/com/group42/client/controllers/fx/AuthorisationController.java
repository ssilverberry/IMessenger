package com.group42.client.controllers.fx;

/*
  Class for organize user authorisation. Controller for
  AuthorisationView.fxml form
 */

import com.group42.client.controllers.RequestController;
import com.group42.client.network.protocol.IncomingServerMessage;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.group42.client.model.Model;

public class AuthorisationController extends Controller {

    /**
     * Logging for exception trace
     */
    private static final Logger logger = LogManager.getLogger(AuthorisationController.class);

    /**
     * binding to fields in view
     */
    @FXML
    private JFXTextField userNameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Hyperlink registerNow;
    @FXML
    private JFXButton signInButton;
    @FXML
    private Label errorLabel;

    private Model user;

    /**
     * Default constructor for correct load
     */
    public AuthorisationController() {
    }

    /**
     * process response from server on authorization step.
     *
     * @param incomingServerMessage instance of response.
     */
    @Override
    public void processResponse(IncomingServerMessage incomingServerMessage) {
        Platform.runLater(() -> {
            switch (incomingServerMessage.getActionId()) {
                case 11:
                    user.setUser(incomingServerMessage.getLogin());
                    logger.info("user successful log in!" +  " " + incomingServerMessage.getLogin());
                    SceneManager.getInstance().setMainScene();
                    break;
                case 12:
                    invalidUser();
                    logger.info("user failed log in!");
                    break;
            }
        });
    }

    /**
     * init function load after scene setup.
     */
    @FXML
    void initialize() {
        user = Model.getInstance();
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                signInListener();
            }
        });
        signInButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                signInListener();
            }
        });
    }

    /**
     * Get data from fields and send request for authorization.
     */
    @FXML
    private void signInListener(){
        if (!checkFieldsForEmpty()) {
            errorLabel.setText("");
            String login = userNameField.getText().trim();
            String password = passwordField.getText();
            RequestController.getInstance().authorizationRequest(login, password);
        }
    }

    /**
     * listen to click "register now" link.
     */
    @FXML
    private void registerNowListener(){
        SceneManager.getInstance().setRegistrationScene();
    }

    /**
     * Check all fields for empty. If it is, warns user for invalid input
     */
    private boolean checkFieldsForEmpty(){
        boolean emptyFlag = false;
        if (userNameField.getText().isEmpty()){
            userNameField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (passwordField.getText().isEmpty()){
            passwordField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        } return emptyFlag;
    }

    /**
     * View warning about invalid data.
     */
    private void invalidUser(){
        errorLabel.setText("! Username or password are invalid!");
        errorLabel.setPrefHeight(20);
    }
}
