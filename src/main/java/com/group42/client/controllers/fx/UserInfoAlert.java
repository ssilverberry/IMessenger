package com.group42.client.controllers.fx;

/**
 * Class for view user info. Views in alert window
 */

import com.group42.client.network.protocol.IncomingServerMessage;
import javafx.scene.control.Alert;

class UserInfoAlert extends Alert {

    /**
     * Constructs info panel with users data and show it.
     * @param alertType
     * @param message
     */
    UserInfoAlert(AlertType alertType, IncomingServerMessage message) {
        super(alertType);
        this.setTitle("Info panel");
        this.setHeaderText("USER INFO: ");
        String firstName = "First name: " + message.getFirstName();
        String lastName = "Last name: " + message.getLastName();
        String email = "Email: " + message.getEmail();
        String phoneNumb = "Phone: " + message.getPhoneNumber();
        String dateOfBirth = "Date Of Birth: " + message.getBirthday();
        this.setContentText(firstName + "\n\n" +lastName + "\n\n" + email + "\n\n" + phoneNumb + "\n\n" + dateOfBirth );
        this.getDialogPane().setStyle("-fx-font-size: 14px");
        this.showAndWait();
    }
}
