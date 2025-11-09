package com.uavguard.app;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField todoItem;

    @FXML
    private ListView todoList;

    @FXML
    protected void onAddButtonClick() {
        this.todoList.getItems().add(this.todoItem.getText());
    }

    @FXML
    protected void onRemoveButtonClick() {
        int indexToRemove =
            this.todoList.getSelectionModel().getSelectedIndex();
        this.todoList.getItems().remove(indexToRemove);
    }
}
