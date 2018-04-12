package com.group42.client.controllers.fx;

import com.group42.client.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.Objects;

/**
 * This class loads 3 main application windows: authorization,
 * registration and main application window.
 */
public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    /**
     * Logging for exception trace
     */
    private final Logger logger = LogManager.getLogger(SceneManager.class);

    /**
     * Instance of primary stage
     */
    private Stage primaryStage;

    /**
     * Paths to javafx view forms. load authorisation, registration and main scene
     */
    private final String authorisationView = "view/AuthorisationView.fxml";
    private final String registrationView = "view/RegistrationView.fxml";
    private final String mainView = "view/MainView.fxml";

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    /**
     * Init first primary stage.
     */
    public void initRootLayout(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setAuthorisationScene();
        //setMainScene();
    }

    /**
     * set authorization scene
     */
    public void setAuthorisationScene() {
        setSpecificScene(authorisationView);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Authorisation");
    }

    /**
     * set registration scene
     */
    public void setRegistrationScene() {
        setSpecificScene(registrationView);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Registration");
    }

    /**
     * This method creates the main chat window: with lists of chat rooms,
     * users, history of messages and others. By analogy with other "scenes",
     * the window with buttons, containers for displaying the list of chats
     * and users (ListView), a container for displaying the history of messages
     * (TextFlow), a field for entering messages, buttons and others is loaded
     * with the file "MainView.fxml". Then listeners are connected.
     */
    public void setMainScene(){
        primaryStage.setTitle("Chat");
        setSpecificScene(mainView);
        primaryStage.sizeToScene();
        primaryStage.setResizable(true);
    }

    /**
     * set define scene
     *
     */
    private void setSpecificScene(String scene){
        try {
            if (primaryStage.isShowing()){
                primaryStage.close();
            }
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getClassLoader().getResource(scene)));
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Primary stage didn't initialize!", e);
        }
    }

    /**
     * set alert scene when connection failed.
     */
    public void connectionFailed(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Server not response!");
            alert.showAndWait();
            SceneManager.getInstance().getPrimaryStage().close();
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
