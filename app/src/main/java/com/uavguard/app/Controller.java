package com.uavguard.app;

import com.uavguard.utilities.Packet;
import java.net.*;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class Controller {

    private Packet pkt = new Packet();

    @FXML
    private Circle lbase;

    @FXML
    private Circle lknob;

    @FXML
    private Circle rbase;

    @FXML
    private Circle rknob;

    private final double centerX = 100; // centro desejado na PANE
    private final double centerY = 100;
    private double lradius;
    private double rradius;

    @FXML
    public void initialize() {
        rradius = rbase.getRadius();
        lradius = rbase.getRadius();
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

        pkt.setParameter(0, (int) dx);
        pkt.setParameter(1, -(int) dy);

        try {
            sendPacket(pkt.getPacket());
        } catch (Exception err) {}
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

        pkt.setParameter(2, -(int) dy);
        pkt.setParameter(3, (int) dx);

        try {
            sendPacket(pkt.getPacket());
        } catch (Exception err) {}
    }

    public void sendPacket(byte[] data) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress addr = InetAddress.getByName("192.168.4.153");
        DatagramPacket pkt = new DatagramPacket(data, data.length, addr, 8090);
        socket.send(pkt);
    }

    @FXML
    public void onMouseReleased(MouseEvent e) {
        rknob.setLayoutX(centerX);
        rknob.setLayoutY(centerY);
        lknob.setLayoutX(centerX);
        lknob.setLayoutY(centerY);

        pkt.setParameter(0, 0);
        pkt.setParameter(1, 0);
        pkt.setParameter(2, 0);
        pkt.setParameter(3, 0);
    }
}
