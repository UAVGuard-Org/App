package com.uavguard.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Plugins {

    @FXML
    private VBox result;

    @FXML
    private TextField input;

    @FXML
    public void initialize() {
        loadItems();
    }

    @FXML
    private void onReload() {
        loadItems();
    }

    @FXML
    private void onSearch() {
        result.getChildren().clear();
        try {
            List<Item> items = Request();
            for (Item itemData : items) {
                if (
                    itemData.model
                        .toLowerCase()
                        .contains(input.getText().toLowerCase())
                ) {
                    result.getChildren().add(createItem(itemData));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Item> Request() throws Exception {
        URL url = new URL(
            "https://raw.githubusercontent.com/PSalleSDev/UAVGuard-Plugins/main/plugins.json"
        );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        Type listType = new TypeToken<List<Item>>() {}.getType();

        List<Item> items = new Gson().fromJson(reader, listType);

        reader.close();
        conn.disconnect();
        return items;
    }

    private void loadItems() {
        result.getChildren().clear();
        try {
            List<Item> items = Request();
            for (Item itemData : items) {
                result.getChildren().add(createItem(itemData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GridPane createItem(Item itemData) throws Exception {
        GridPane item = FXMLLoader.load(
            getClass().getResource("view/item.xml")
        );

        HBox rowModel = (HBox) item.getChildren().get(0);
        Label modelLabel = (Label) rowModel.getChildren().get(0);
        modelLabel.setText(itemData.model);

        HBox rowVersion = (HBox) item.getChildren().get(1);
        Label versionLabel = (Label) rowVersion.getChildren().get(0);
        versionLabel.setText(itemData.version);

        HBox rowStatus = (HBox) item.getChildren().get(2);
        Label statusLabel = (Label) rowStatus.getChildren().get(0);
        statusLabel.setText(itemData.installed ? "Installed" : "Not installed");

        HBox rowButton = (HBox) item.getChildren().get(3);
        Button btn = (Button) rowButton.getChildren().get(0);

        setButtonGraphic(btn, itemData.installed);

        btn.setOnAction(e -> {
            toggleInstall(itemData, btn, statusLabel);
        });

        return item;
    }

    private void setButtonGraphic(Button btn, boolean installed) {
        try {
            String path = installed ? "icons/remove.xml" : "icons/install.xml";
            javafx.scene.Node graphic = FXMLLoader.load(
                getClass().getResource(path)
            );
            btn.setGraphic(graphic);
        } catch (Exception e) {
            btn.setText(installed ? "Remove" : "Install");
        }
    }

    private void toggleInstall(Item item, Button btn, Label statusLabel) {
        item.installed = !item.installed;
        statusLabel.setText(item.installed ? "Installed" : "Not installed");
        setButtonGraphic(btn, item.installed);
    }
}
