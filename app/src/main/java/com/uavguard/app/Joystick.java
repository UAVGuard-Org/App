package com.uavguard.app;

import java.util.function.BiConsumer;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class Joystick {

    public static void onMouseDragged(
        MouseEvent e,
        BiConsumer<Integer, Integer> callback
    ) {
        Pane pane = (Pane) e.getSource();

        Circle knob = (Circle) pane.getChildren().get(2);
        Circle base = (Circle) pane.getChildren().get(1);

        double radius = base.getRadius();

        var p = knob.getParent().sceneToLocal(e.getSceneX(), e.getSceneY());

        double dx = p.getX() - 100;
        double dy = p.getY() - 100;

        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > radius) {
            dx = (dx / dist) * radius;
            dy = (dy / dist) * radius;
        }

        knob.setLayoutX(100 + dx);
        knob.setLayoutY(100 + dy);

        callback.accept((int) dx, (int) dy);
    }

    public static void onMouseReleased(MouseEvent e, Runnable callback) {
        Pane pane = (Pane) e.getSource();

        Circle knob = (Circle) pane.getChildren().get(2);

        knob.setLayoutX(100);
        knob.setLayoutY(100);

        callback.run();
    }
}
