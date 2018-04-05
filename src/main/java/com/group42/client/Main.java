package com.group42.client;

import com.group42.client.network.NetworkController;
import javafx.application.Application;
import javafx.stage.Stage;
import com.group42.client.controllers.fx.SceneManager;

/**
 * Main class extend Application by JavaFx.
 */

public class Main extends Application {


    @Override
    public void init() throws Exception {
        NetworkController.getInstance().openConnection();
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstance().initRootLayout(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        NetworkController.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
