package com.group42.client.controllers.fx;

/*
  User registration class controller for RegistrationView.fxml form
 */

import com.group42.client.network.protocol.IncomingServerMessage;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.group42.client.controllers.RequestController;
import com.group42.client.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.format.DateTimeFormatter;

public class RegistrationController extends Controller {

    /**
     * Logging for exception trace
     */
    private final Logger logger = LogManager.getLogger(RequestController.class);

    /**
     * binding to fields in view
     */
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastFieldName;
    @FXML
    private JFXTextField phoneNumbField;
    @FXML
    private JFXDatePicker dateOfBirth;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField userNameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField confirmPasswordField;
    @FXML
    private JFXButton signUpButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private Label errorLabel;

    /**
     * Instance of model
     */
    private Model model;

    /**
     * Default constructor for correct load
     */
    public RegistrationController() {
    }

    /**
     * Process response from server on registration step.
     *
     * @param incomingServerMessage instance of response
     */
    @Override
    public void processResponse(IncomingServerMessage incomingServerMessage) {
        Platform.runLater(() -> {
            switch (incomingServerMessage.getActionId()) {
                case 21:
                    model.setUser(incomingServerMessage.getLogin());
                    SceneManager.getInstance().setAuthorisationScene();
                    logger.info("user successful registered!");
                    break;
                case 22:
                    userExists();
                    logger.error("user failed registration");
                    break;
            }
        });
    }

    @FXML
    void initialize() {
        model = Model.getInstance();
        cancelButton.setOnAction(event -> SceneManager.getInstance().setAuthorisationScene());
    }

    /**
     * Get data from fields and send request for registration
     */
    @FXML
    private void signUpListener(){
        if (!checkFieldsForEmpty()) {
            errorLabel.setPrefHeight(20);
            errorLabel.setText("");
            if (!checkEmailForValid()) {
                String firsName = firstNameField.getText();
                String lastName = lastFieldName.getText();
                String phoneNumber = phoneNumbField.getText();
                String birthDate = dateOfBirth.getValue().format(DateTimeFormatter.ofPattern("dd LLLL yyyy"));
                String email = emailField.getText();
                String login = userNameField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                if (!password.equals(confirmPassword)){
                    errorLabel.setText("Passwords do not match!");
                    passwordField.setStyle("-fx-border-color: #d62f2f;");
                    confirmPasswordField.setStyle("-fx-border-color: #d62f2f;");
                } else {
                    RequestController.getInstance().registrationRequest(firsName, lastName, phoneNumber,
                            birthDate, email, login, password);
                    clearRegistrationFields();
                }
            }
        }
    }

    /**
     * Clear registration fields
     */
    private void clearRegistrationFields(){
        emailField.clear();
        userNameField.clear();
        passwordField.clear();
    }

    /**
     * listen to cancel registration
     */
    @FXML
    private void cancelListener(){
        SceneManager.getInstance().setAuthorisationScene();
    }

    /**
     * Check all fields for empty. If it is, warns user for invalid input
     */
    private boolean checkFieldsForEmpty(){
        boolean emptyFlag = false;
        if (emailField.getText().isEmpty()){
            emailField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (userNameField.getText().isEmpty()){
            userNameField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (passwordField.getText().isEmpty()){
            passwordField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (confirmPasswordField.getText().isEmpty()){
            confirmPasswordField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (firstNameField.getText().isEmpty()){
            firstNameField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (lastFieldName.getText().isEmpty()){
            lastFieldName.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (phoneNumbField.getText().isEmpty()){
            phoneNumbField.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        if (dateOfBirth.getValue().toString().isEmpty()){
            dateOfBirth.setStyle("-fx-border-color: #d62f2f;");
            emptyFlag = true;
        }
        return emptyFlag;
    }

    /**
     * Check email for invalid
     */
    private boolean checkEmailForValid(){
        boolean invalidFlag = false;
        String line = emailField.getText();
        if (!line.contains("@")){
            invalidEmail();
            invalidFlag = true;
        }
        if (!(line.indexOf("@") < line.indexOf(".")) || (line.indexOf(".") - line.indexOf("@") < 3)){
            invalidEmail();
            invalidFlag = true;
        } return invalidFlag;
    }

    /**
     * Warns  that the user is already exist.
     */
    private void userExists(){
        errorLabel.setText("This nickname already exists!");
        errorLabel.setPrefHeight(20);
    }

    /**
     * View warning about invalid email
     */
    private void invalidEmail(){
        emailField.setStyle("-fx-border-color: #d62f2f;");
        errorLabel.setText("! Invalid email");
        errorLabel.setPrefHeight(20);
    }

}
