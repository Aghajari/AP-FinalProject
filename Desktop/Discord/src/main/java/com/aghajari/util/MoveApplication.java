package com.aghajari.util;

import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MoveApplication {
    private double xOffset = 0;
    private double yOffset = 0;

    private MoveApplication(Parent root, Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
    }

    public static void initMovement(Parent root, Stage primaryStage) {
        new MoveApplication(root, primaryStage);
    }
}