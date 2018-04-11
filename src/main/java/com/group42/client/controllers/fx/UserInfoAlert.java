package com.group42.client.controllers.fx;

/*
 * Class for view user info. Views in alert window
 */

import com.group42.client.protocol.IncomingServerMessage;
import javafx.scene.control.Alert;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        Locale.setDefault(Locale.US);
        String firstName = "First name: " + message.getFirstName();
        String lastName = "Last name: " + message.getLastName();
        String email = "Email: " + message.getEmail();
        String phoneNumb = "Phone: " + message.getPhoneNumber();
        String dateOfBirth = "Date Of Birth: " + message.getBirthday().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        this.setContentText(firstName + "\n\n" +lastName + "\n\n" + email + "\n\n" + phoneNumb + "\n\n" + dateOfBirth );
        this.getDialogPane().setStyle("-fx-font-size: 14px");
        this.showAndWait();
    }
}
