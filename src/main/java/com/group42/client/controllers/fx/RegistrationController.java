package com.group42.client.controllers.fx;

/*
  User registration class controller for RegistrationView.fxml form
 */

import com.group42.client.protocol.IncomingServerMessage;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.group42.client.controllers.RequestController;
import com.group42.client.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    clearRegistrationFields();
                    logger.info("user successful registered!");
                    break;
                case 22:
                    userExists();
                    logger.error("user failed registration");
                    break;
            }
        });
    }

    /**
     * init model instance, set locale to default view names
     * of all elements and sets some listeners.
     */
    @FXML
    void initialize() {
        model = Model.getInstance();
        Locale.setDefault(Locale.US);
        cancelButton.setOnAction(event -> SceneManager.getInstance().setAuthorisationScene());
        phoneNumbField.setOnMouseClicked(event -> {
            if (phoneNumbField.getText().isEmpty()){
                phoneNumbField.setText("+38");
                phoneNumbField.positionCaret("+38".length());
            }
        });
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
                String phoneNumber = checkPhoneNumberForValid(phoneNumbField.getText());
                if (phoneNumber != null) {
                    LocalDate birthDate = dateOfBirth.getValue();
                    String email = emailField.getText();
                    String login = userNameField.getText();
                    String password = passwordField.getText();
                    String confirmPassword = confirmPasswordField.getText();
                    if (checkConfirmPassword(password, confirmPassword)) {
                        RequestController.getInstance().registrationRequest(firsName, lastName, phoneNumber,
                                birthDate, email, login, password);
                    }
                }
            }
        }
    }

    /**
     * checks phone number for valid. Uses regExp for check on
     * right symbol in phone number.
     * @param phoneNumber
     * @return
     */
    private String checkPhoneNumberForValid(String phoneNumber){
        String regex = "^\\(?(\\+38)\\)?[-\\s]?(050|063|06[6-8]{1}|09[1-9]{1})[-\\s]?([0-9]{3})[-\\s]?([0-9]{2})[-\\s]?([0-9]{2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.find() && matcher.group().equals(phoneNumber)){
            return matcher.replaceFirst("($1)-$2-$3-$4-$5");
        } else {
            errorLabel.setText("Phone number is incorrect!");
            phoneNumbField.setStyle("-fx-border-color: #d62f2f;");
            return null;
        }
    }

    /**
     * Checks if the password is correctly confirmed.
     * @param password
     * @param confirmPassword
     */
    private boolean checkConfirmPassword(String password, String confirmPassword){
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match!");
            passwordField.setStyle("-fx-border-color: #d62f2f;");
            confirmPasswordField.setStyle("-fx-border-color: #d62f2f;");
            return false;
        } else return true;
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
        if (dateOfBirth.getValue() != null) {
            if (dateOfBirth.getValue().toString().isEmpty()) {
                dateOfBirth.setStyle("-fx-border-color: #d62f2f;");
                emptyFlag = true;
            } else if (dateOfBirth.getValue().compareTo(LocalDate.now()) > 0){
                dateOfBirth.setStyle("-fx-border-color: #d62f2f;");
                emptyFlag = true;
            }
        } else {
            dateOfBirth.setStyle(dateOfBirth.getStyle() + "-fx-border-color: #d62f2f;");
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
