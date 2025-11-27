package com.uavguard.app;

import com.uavguard.app.Joystick;
import com.uavguard.sdk.Action;
import com.uavguard.sdk.Movement;
import com.uavguard.sdk.Plugin;
import com.uavguard.utilities.Manager;
import com.uavguard.utilities.Path;
import com.uavguard.utilities.Socket;
import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Home {

    private String ip;
    private Plugin plugin;
    private Action command;

    private volatile boolean running = true;

    private final Manager manager = new Manager();
    private final Socket socket = new Socket();

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
            Path.checkPaths();
            loadPlugins();
            configureUI();
            startCommandSender();
        } catch (Exception e) {
            e.printStackTrace();
        }

        control
            .sceneProperty()
            .addListener((obs, oldScene, newScene) -> {
                if (oldScene != null && newScene == null) {
                    running = false;
                }
            });
    }

    private void loadPlugins() throws Exception {
        manager.load(Path.getAppData() + "/plugins");
        modelSelect.getItems().clear();
        commandSelect.getItems().clear();

        if (manager.plugins.isEmpty()) {
            plugin = null;
            return;
        }

        // Primeiro plugin da lista
        plugin = manager.plugins.get(0);
        ip = Socket.getGatewayAddress();

        for (Plugin p : manager.plugins) {
            modelSelect.getItems().add(p.getName());
        }

        loadCommandsFor(plugin);
        setupVideoReceiver(plugin);
    }

    private void loadCommandsFor(Plugin p) {
        commandSelect.getItems().clear();

        if (p == null || p.getCommand() == null) return;

        for (Action action : p.getCommand().getActions()) {
            commandSelect.getItems().add(action.getName());
        }

        command = p.getCommand().getActions()[0];
    }

    private void configureUI() throws Exception {
        Pane left = FXMLLoader.load(
            getClass().getResource("view/joystick.xml")
        );
        Pane right = FXMLLoader.load(
            getClass().getResource("view/joystick.xml")
        );

        setupJoystick(left, true);
        setupJoystick(right, false);

        control.getChildren().add(left);
        control.getChildren().add(right);
    }

    private void setupJoystick(Pane joystick, boolean isLeft) {
        joystick.setOnMouseDragged(e -> {
            if (plugin == null) return;

            Joystick.onMouseDragged(e, (x, y) -> {
                if (isLeft) {
                    plugin.getCommand().setParameter(Movement.YAW, x);
                    plugin.getCommand().setParameter(Movement.THROTTLE, -y);
                } else {
                    plugin.getCommand().setParameter(Movement.ROLL, x);
                    plugin.getCommand().setParameter(Movement.PITCH, -y);
                }
            });
        });

        joystick.setOnMouseReleased(e -> {
            if (plugin == null) return;

            Joystick.onMouseReleased(e, () -> {
                if (isLeft) {
                    plugin.getCommand().setParameter(Movement.THROTTLE, 0);
                    plugin.getCommand().setParameter(Movement.YAW, 0);
                } else {
                    plugin.getCommand().setParameter(Movement.PITCH, 0);
                    plugin.getCommand().setParameter(Movement.ROLL, 0);
                }
            });
        });
    }

    private void setupVideoReceiver(Plugin p) {
        if (p == null || p.getVideo() == null) return;

        try {
            DatagramSocket videoSocket = new DatagramSocket(
                new InetSocketAddress(ip, p.getVideo().getPort())
            );

            try {
                p.getVideo().getSetup(videoSocket);
                p
                    .getVideo()
                    .setCallback((byte[] frame) -> {
                        Platform.runLater(() -> {
                            Image img = new Image(
                                new ByteArrayInputStream(frame)
                            );
                            cameraView.setImage(img);
                        });
                    });
            } catch (Exception ignored) {}

            new Thread(() -> {
                while (running) {
                    try {
                        byte[] buffer = new byte[4096];
                        DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length
                        );

                        videoSocket.receive(packet);

                        byte[] data = new byte[packet.getLength()];
                        System.arraycopy(
                            packet.getData(),
                            0,
                            data,
                            0,
                            packet.getLength()
                        );

                        p.getVideo().getLoop(videoSocket, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            })
                .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCommandSender() {
        new Thread(() -> {
            while (running) {
                try {
                    if (plugin != null) {
                        byte[] pkt = plugin.getCommand().getPacket();
                        socket.sendPacket(
                            pkt,
                            ip,
                            plugin.getCommand().getPort()
                        );
                    }
                    Thread.sleep(50);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        })
            .start();
    }

    @FXML
    public void onModelSelect() {
        String selected = modelSelect.getValue();

        for (Plugin p : manager.plugins) {
            if (p.getName().equals(selected)) {
                plugin = p;
                try {
                    ip = Socket.getGatewayAddress();
                } catch (Exception e) {}
                loadCommandsFor(p);
                setupVideoReceiver(p);
                break;
            }
        }
    }

    @FXML
    public void onCommandSelect() {
        if (plugin == null) return;

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
            if (plugin == null || command == null) return;

            byte[] pkt = command.getPacket();
            socket.sendPacket(pkt, ip, plugin.getCommand().getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
