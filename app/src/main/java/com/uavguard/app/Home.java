package com.uavguard.app;

import com.uavguard.plugin.Action;
import com.uavguard.plugin.Command;
import com.uavguard.plugin.Plugin;
import com.uavguard.utilities.Manager;
import com.uavguard.utilities.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class Home {

    private Manager manager = new Manager();
    private Plugin plugin;
    private Command command;
    private String ip;
    private volatile int lx, ly, rx, ry;
    private volatile boolean running = true;

    @FXML
    private Circle lbase;

    @FXML
    private Circle lknob;

    @FXML
    private Circle rbase;

    @FXML
    private Circle rknob;

    @FXML
    private ComboBox<String> modelSelect;

    @FXML
    private ComboBox<String> commandSelect;

    private final double centerX = 100;
    private final double centerY = 100;
    private double lradius;
    private double rradius;

    @FXML
    public void initialize() {
        setIp();
        try {
            manager.load("/home/hasbulla/Documents/UAVGuard/plugins");
            plugin = manager.plugins.get(0);
            command = plugin.getCommands()[0];
            for (Plugin p : manager.plugins) {
                modelSelect.getItems().add(p.getName());
            }

            for (Command c : plugin.getCommands()) {
                commandSelect.getItems().add(c.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rradius = rbase.getRadius();
        lradius = lbase.getRadius();

        new Thread(() -> {
            while (running) {
                try {
                    byte[] pkt = plugin.getPacket();
                    Socket.sendPacket(pkt, ip, plugin.getPort());
                    Thread.sleep(50);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        })
            .start();

        lbase
            .sceneProperty()
            .addListener((obs, oldScene, newScene) -> {
                if (oldScene != null && newScene == null) {
                    running = false;
                }
            });
    }

    @FXML
    public void RightOnMouseDragged(MouseEvent e) {
        var p = rknob.getParent().sceneToLocal(e.getSceneX(), e.getSceneY());

        double dx = p.getX() - centerX;
        double dy = p.getY() - centerY;

        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > rradius) {
            dx = (dx / dist) * rradius;
            dy = (dy / dist) * rradius;
        }

        rknob.setLayoutX(centerX + dx);
        rknob.setLayoutY(centerY + dy);

        plugin.setParameter(Action.ROLL, (int) dx);
        plugin.setParameter(Action.PITCH, -(int) dy);
    }

    @FXML
    public void LeftOnMouseDragged(MouseEvent e) {
        var p = lknob.getParent().sceneToLocal(e.getSceneX(), e.getSceneY());

        double dx = p.getX() - centerX;
        double dy = p.getY() - centerY;

        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > lradius) {
            dx = (dx / dist) * lradius;
            dy = (dy / dist) * lradius;
        }

        lknob.setLayoutX(centerX + dx);
        lknob.setLayoutY(centerY + dy);

        plugin.setParameter(Action.YAW, (int) dx);
        plugin.setParameter(Action.THROTTLE, -(int) dy);
    }

    @FXML
    public void onMouseReleased(MouseEvent e) {
        rknob.setLayoutX(centerX);
        rknob.setLayoutY(centerY);
        lknob.setLayoutX(centerX);
        lknob.setLayoutY(centerY);

        // resetar todos os eixos
        plugin.setParameter(Action.THROTTLE, 0);
        plugin.setParameter(Action.YAW, 0);
        plugin.setParameter(Action.PITCH, 0);
        plugin.setParameter(Action.ROLL, 0);

        try {
            Socket.sendPacket(plugin.getPacket(), ip, plugin.getPort());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @FXML
    public void onModelSelect() {
        setIp();

        String selected = modelSelect.getValue();
        for (Plugin p : manager.plugins) {
            if (p.getName() == selected) {
                this.plugin = p;
                command = plugin.getCommands()[0];

                commandSelect.getItems().clear();
                for (Command c : p.getCommands()) {
                    commandSelect.getItems().add(c.getName());
                }
            }
        }
    }

    @FXML
    public void onCommandSelect() {
        String selected = commandSelect.getValue();
        for (Command c : plugin.getCommands()) {
            if (c.getName() == selected) {
                this.command = c;
            }
        }
    }

    @FXML
    public void onSendCommand() {
        try {
            byte[] pkt = command.getPacket();
            Socket.sendPacket(pkt, ip, plugin.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIp() {
        try {
            Process process = Runtime.getRuntime().exec(
                new String[] { "bash", "-c", "ip route show default " }
            );
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            ip = reader.readLine().trim().split("\\s+")[2];
        } catch (Exception e) {}
    }
}
