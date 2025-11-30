package com.uavguard.app;

import com.uavguard.sdk.Action;
import com.uavguard.sdk.Movement;
import com.uavguard.sdk.Plugin;
import com.uavguard.utilities.Manager;
import com.uavguard.utilities.Network;
import com.uavguard.utilities.Path;
import java.io.ByteArrayInputStream;
import java.net.*;
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

    private Plugin[] plugins;

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

            //Load plugins
            plugins = Manager.load(Path.getAppData() + "/plugins");
            modelSelect.getItems().clear();
            commandSelect.getItems().clear();

            if (plugins.length == 0) return;

            plugin = plugins[0];
            ip = Network.getGatewayAddress();

            for (Plugin p : plugins) {
                modelSelect.getItems().add(p.getName());
            }

            for (Action action : plugin.getCommand().getActions()) {
                commandSelect.getItems().add(action.getName());
            }

            command = plugin.getCommand().getActions()[0];

            //Set joysticks
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

            //Command sender
            new Thread(() -> {
                while (running) {
                    try {
                        if (plugin != null) {
                            byte[] pkt = plugin.getCommand().getPacket();
                            System.out.print("Bytes: ");
                            for (byte b : pkt) {
                                System.out.printf("%02X ", b);
                            }
                            System.out.println();

                            // imprime IP e porta destino
                            System.out.println(
                                "Para: " +
                                    ip +
                                    ":" +
                                    plugin.getCommand().getPort()
                            );
                            Network.sendPacket(
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

            //Video reciver
            plugin
                .getVideo()
                .setCallback(frame ->
                    Platform.runLater(() ->
                        cameraView.setImage(
                            new Image(new ByteArrayInputStream(frame))
                        )
                    )
                );

            DatagramNetwork Network = new DatagramNetwork(
                plugin.getVideo().getPort()
            );
            InetAddress ipAddr = InetAddress.getByName(ip);

            plugin.getVideo().setup(Network, ipAddr);

            new Thread(() -> {
                try {
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length
                    );

                    while (running) {
                        Network.receive(packet);

                        int length = packet.getLength();
                        byte[] received = new byte[length];
                        System.arraycopy(
                            packet.getData(),
                            0,
                            received,
                            0,
                            length
                        );

                        plugin.getVideo().loop(Network, ipAddr, received);
                    }

                    Network.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
                .start();
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

    private void setupJoystick(Pane joystick, boolean isLeft) {
        joystick.setOnMouseDragged(e -> {
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

    @FXML
    public void onModelSelect() {
        String selected = modelSelect.getValue();

        for (Plugin p : plugins) {
            if (p.getName().equals(selected)) {
                plugin = p;
                try {
                    ip = Network.getGatewayAddress();
                } catch (Exception e) {}
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
                command = c;
            }
        }
    }

    @FXML
    public void onSendCommand() {
        try {
            if (plugin == null || command == null) return;

            byte[] pkt = command.getPacket();
            Network.sendPacket(pkt, ip, plugin.getCommand().getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
