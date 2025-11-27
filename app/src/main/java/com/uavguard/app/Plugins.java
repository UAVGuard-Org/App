package com.uavguard.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uavguard.plugin.Plugin;
import com.uavguard.utilities.Manager;
import com.uavguard.utilities.Path;
import com.uavguard.utilities.Status;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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

    private Manager manager = new Manager();

    @FXML
    private VBox result;

    @FXML
    private TextField input;

    @FXML
    public void initialize() {
        try {
            loadPlugins();
            loadItems();
        } catch (Exception ignored) {}
    }

    @FXML
    private void onReload() {
        try {
            loadPlugins();
            loadItems();
        } catch (Exception ignored) {}
    }

    @FXML
    private void onSearch() {
        result.getChildren().clear();
        try {
            List<Item> items = Request();
            for (Item itemData : items) {
                if (
                    itemData.name
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

    private void loadItems() throws Exception {
        result.getChildren().clear();
        List<Item> items = Request();

        for (Item itemData : items) {
            itemData.status = Status.Download;

            for (Plugin p : manager.plugins) {
                if (p.getName().equals(itemData.name)) {
                    if (p.getVersion().equals(itemData.version)) {
                        itemData.status = Status.Remove;
                    } else {
                        itemData.status = Status.Update;
                    }
                }
            }

            result.getChildren().add(createItem(itemData));
        }
    }

    private GridPane createItem(Item itemData) throws Exception {
        GridPane item = FXMLLoader.load(
            getClass().getResource("view/item.xml")
        );

        Label modelLabel = (Label) ((HBox) item
                .getChildren()
                .get(0)).getChildren().get(0);
        modelLabel.setText(itemData.name);

        Label versionLabel = (Label) ((HBox) item
                .getChildren()
                .get(1)).getChildren().get(0);
        versionLabel.setText(itemData.version);

        Label statusLabel = (Label) ((HBox) item
                .getChildren()
                .get(2)).getChildren().get(0);
        statusLabel.setText(itemData.status.toString());

        Button btn = (Button) ((HBox) item
                .getChildren()
                .get(3)).getChildren().get(0);
        setButtonGraphic(btn, itemData.status);

        btn.setOnAction(e -> pluginAction(itemData, btn, statusLabel));
        return item;
    }

    private void setButtonGraphic(Button btn, Status status) {
        String path = "";

        try {
            switch (status) {
                case Remove -> path = "icons/remove.xml";
                case Update -> path = "icons/update.xml";
                case Download -> path = "icons/download.xml";
            }

            javafx.scene.Node graphic = FXMLLoader.load(
                getClass().getResource(path)
            );
            btn.setGraphic(graphic);
        } catch (Exception e) {
            switch (status) {
                case Remove -> btn.setText("Remove");
                case Update -> btn.setText("Update");
                case Download -> btn.setText("Download");
            }
        }
    }

    private void pluginAction(Item item, Button btn, Label statusLabel) {
        try {
            switch (item.status) {
                case Download -> {
                    downloadPlugin(item);
                    statusLabel.setText("Remove");
                    item.status = Status.Remove;
                }
                case Remove -> {
                    removePlugin(item);
                    statusLabel.setText("Download");
                    item.status = Status.Download;
                }
                case Update -> {
                    removePlugin(item);
                    downloadPlugin(item);
                    statusLabel.setText("Remove");
                    item.status = Status.Remove;
                }
            }

            setButtonGraphic(btn, item.status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadPlugin(Item item) throws Exception {
        String pluginsDir = Path.getAppData() + "/plugins";

        URL url = new URL(item.link);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());

        FileOutputStream fos = new FileOutputStream(
            pluginsDir + "/" + item.name + ".jar"
        );

        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

        manager.load(pluginsDir);
    }

    private void removePlugin(Item item) throws Exception {
        String pluginsDir = Path.getAppData() + "/plugins";
        File f = new File(pluginsDir + "/" + item.name + ".jar");

        if (f.exists()) f.delete();

        manager.load(pluginsDir);
    }

    private void loadPlugins() throws Exception {
        manager.load(Path.getAppData() + "/plugins");
    }
}
