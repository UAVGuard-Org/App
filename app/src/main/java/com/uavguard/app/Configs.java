package com.uavguard.app;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Configs {

    @FXML
    private ScrollPane root;

    @FXML
    private TextField pathField;

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    public void initialize() {
        directoryChooser.setTitle("Select a plugin folder");
    }

    @FXML
    public void onMouseClicked() {
        Stage stage = (Stage) root.getScene().getWindow();

        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            pathField.setText(selectedDirectory.getAbsolutePath());
        } else {
            System.out.println("Nenhuma pasta selecionada.");
        }
    }
}
