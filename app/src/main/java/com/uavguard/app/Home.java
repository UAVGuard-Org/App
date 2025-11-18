package com.uavguard.app;

import com.uavguard.app.Joystick;
import com.uavguard.plugin.Action;
import com.uavguard.plugin.Movement;
import com.uavguard.plugin.Plugin;
import com.uavguard.utilities.Manager;
import com.uavguard.utilities.Socket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Home {

    private Manager manager = new Manager();
    private Plugin plugin;
    private Action command;
    private final Socket socket = new Socket();
    private String ip;
    private volatile boolean running = true;

    @FXML
    private HBox control;

    @FXML
    private ComboBox<String> modelSelect;

    @FXML
    private ComboBox<String> commandSelect;

    @FXML
    private ImageView cameraView;

    @FXML
    public void initialize() {
        try {
            ip = Socket.getGatewayAddress();

            manager.load("/home/hasbulla/Documents/UAVGuard/plugins");
            plugin = manager.plugins.get(0);
            command = plugin.getCommand().getActions()[0];
            for (Plugin p : manager.plugins) {
                modelSelect.getItems().add(p.getName());
            }

            for (Action c : plugin.getCommand().getActions()) {
                commandSelect.getItems().add(c.getName());
            }

            Pane left_joystick = FXMLLoader.load(
                getClass().getResource("view/joystick.xml")
            );
            Pane right_joystick = FXMLLoader.load(
                getClass().getResource("view/joystick.xml")
            );

            left_joystick.setOnMouseDragged(e -> {
                Joystick.onMouseDragged(e, (x, y) -> {
                    plugin.getCommand().setParameter(Movement.YAW, x);
                    plugin.getCommand().setParameter(Movement.THROTTLE, -y);
                });
            });

            left_joystick.setOnMouseReleased(e -> {
                Joystick.onMouseReleased(e, () -> {
                    plugin.getCommand().setParameter(Movement.THROTTLE, 0);
                    plugin.getCommand().setParameter(Movement.YAW, 0);
                });
            });

            right_joystick.setOnMouseDragged(e -> {
                Joystick.onMouseDragged(e, (x, y) -> {
                    plugin.getCommand().setParameter(Movement.ROLL, x);
                    plugin.getCommand().setParameter(Movement.PITCH, -y);
                });
            });

            right_joystick.setOnMouseReleased(e -> {
                Joystick.onMouseReleased(e, () -> {
                    plugin.getCommand().setParameter(Movement.PITCH, 0);
                    plugin.getCommand().setParameter(Movement.ROLL, 0);
                });
            });

            control.getChildren().add(left_joystick);
            control.getChildren().add(right_joystick);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while (running) {
                try {
                    byte[] pkt = plugin.getCommand().getPacket();
                    socket.sendPacket(pkt, ip, plugin.getCommand().getPort());
                    Thread.sleep(50);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        })
            .start();

        //         while (running) {
        //             byte[] frame = decoder.nextFrame();
        //             if (frame != null) {
        //                 Image img = new Image(new ByteArrayInputStream(data));
        //                 cameraView.setImage(img);
        //             }
        //         }

        control
            .sceneProperty()
            .addListener((obs, oldScene, newScene) -> {
                if (oldScene != null && newScene == null) {
                    running = false;
                }
            });
    }

    @FXML
    public void onModelSelect() {
        try {
            ip = Socket.getGatewayAddress();
        } catch (Exception e) {}

        String selected = modelSelect.getValue();
        for (Plugin p : manager.plugins) {
            if (p.getName().equals(selected)) {
                this.plugin = p;
                command = plugin.getCommand().getActions()[0];

                commandSelect.getItems().clear();
                for (Action c : p.getCommand().getActions()) {
                    commandSelect.getItems().add(c.getName());
                }
            }
        }
    }

    @FXML
    public void onCommandSelect() {
        String selected = commandSelect.getValue();
        for (Action c : plugin.getCommand().getActions()) {
            if (c.getName().equals(selected)) {
                this.command = c;
            }
        }
    }

    @FXML
    public void onSendCommand() {
        try {
            byte[] pkt = command.getPacket();
            socket.sendPacket(pkt, ip, plugin.getCommand().getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
