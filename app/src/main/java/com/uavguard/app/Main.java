package com.uavguard.app;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Main {

    @FXML
    private HBox content;

    @FXML
    private Button homeButton;

    @FXML
    private Button pluginsButton;

    @FXML
    private Button configsButton;

    @FXML
    public void initialize() {
        homeButton.setOnAction(e -> loadView("view/home.xml"));
        pluginsButton.setOnAction(e -> loadView("view/plugins.xml"));
        configsButton.setOnAction(e -> loadView("view/configs.xml"));
    }

    private void loadView(String fxmlName) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlName));
            content.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
