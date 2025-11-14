package com.uavguard.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
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
                if (itemData.model.contains(input.getText())) {
                    result.getChildren().add(createItem(itemData));
                }
            }
        } catch (Exception e) {}
    }

    private List<Item> Request() throws Exception {
        URL url = new URL(
            "https://raw.githubusercontent.com/PSalleSDev/UAVGuard-Plugins/main/plugins.json"
        );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

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
        } catch (Exception e) {}
    }

    private GridPane createItem(Item itemData) {
        GridPane itemGrid = new GridPane();
        itemGrid.setId("item");

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            itemGrid.getColumnConstraints().add(col);
        }

        Label nameLabel = new Label(itemData.model);

        HBox nameBox = new HBox(nameLabel);
        nameBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

        itemGrid.add(nameBox, 0, 0);

        Label versionLabel = new Label(itemData.version);

        HBox versionBox = new HBox(versionLabel);
        versionBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(versionLabel, javafx.scene.layout.Priority.ALWAYS);

        itemGrid.add(versionBox, 1, 0);

        Label statusLabel = new Label(
            itemData.installed ? "Installed" : "Not installed"
        );

        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(statusLabel, javafx.scene.layout.Priority.ALWAYS);

        itemGrid.add(statusBox, 2, 0);

        Button btn = new Button();
        setButtonGraphic(btn, itemData.installed);

        btn.setOnAction(e ->
            toggleInstall(itemData, btn, statusLabel, statusBox)
        );

        HBox buttonBox = new HBox(btn);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(btn, javafx.scene.layout.Priority.ALWAYS);

        itemGrid.add(buttonBox, 3, 0);

        return itemGrid;
    }

    private void setButtonGraphic(Button btn, boolean installed) {
        try {
            String path = installed ? "icons/remove.xml" : "icons/install.xml";
            javafx.scene.Node graphic = javafx.fxml.FXMLLoader.load(
                getClass().getResource(path)
            );
            btn.setGraphic(graphic);
        } catch (Exception e) {
            e.printStackTrace();
            btn.setText(installed ? "Remove" : "Install");
        }
    }

    private void toggleInstall(
        Item item,
        Button btn,
        Label statusLabel,
        HBox statusBox
    ) {
        item.installed = !item.installed;
        statusLabel.setText(item.installed ? "Installed" : "Not installed");
        setButtonGraphic(btn, item.installed);
    }
}
