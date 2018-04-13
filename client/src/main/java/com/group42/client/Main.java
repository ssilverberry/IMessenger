package com.group42.client;

import com.group42.client.controllers.NetworkController;
import javafx.application.Application;
import javafx.stage.Stage;
import com.group42.client.controllers.fx.SceneManager;

/**
 * Main class extend Application by JavaFx.
 */

public class Main extends Application {


    @Override
    public void init() {
        NetworkController.getInstance().openConnection();
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstance().initRootLayout(primaryStage);
    }

    @Override
    public void stop() {
        NetworkController.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
