package com.uavguard.app;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            App.class.getResource("view/main.xml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("AUVGuard");
        stage.setScene(scene);
        stage.show();
    }
}
