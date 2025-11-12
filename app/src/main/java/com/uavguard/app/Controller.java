package com.uavguard.app;

import com.uavguard.plugin.Action;
import com.uavguard.plugin.Plugin;
import com.uavguard.utilities.Manager;
import java.net.*;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class Controller {

    private Manager manager = new Manager();
    private Plugin plugin;

    @FXML
    private Circle lbase;

    @FXML
    private Circle lknob;

    @FXML
    private Circle rbase;

    @FXML
    private Circle rknob;

    private final double centerX = 100;
    private final double centerY = 100;
    private double lradius;
    private double rradius;

    @FXML
    public void initialize() {
        try {
            manager.load(
                "/home/hasbulla/Documents/UAVGuard Plugins/wrj12620/target/"
            );

            for (Plugin p : manager.plugins) {
                if (p.getName().equals("wrj12620")) {
                    this.plugin = p;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rradius = rbase.getRadius();
        lradius = lbase.getRadius();
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

        try {
            sendPacket(plugin.getPacket());
        } catch (Exception err) {
            err.printStackTrace();
        }
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

        try {
            sendPacket(plugin.getPacket());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void sendPacket(byte[] data) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress addr = InetAddress.getByName("192.168.4.153");
        DatagramPacket pkt = new DatagramPacket(data, data.length, addr, 8090);
        socket.send(pkt);
        socket.close();
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
            sendPacket(plugin.getPacket());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
