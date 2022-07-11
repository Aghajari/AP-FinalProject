package com.aghajari.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class Animations {

    public static void translateX(double fromX, double toX, long ms, Node node) {
        translateX(fromX, toX, ms, node, null);
    }

    public static void translateX(double fromX, double toX, long ms, Node node, EventHandler<ActionEvent> event) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ms), node);
        translateTransition.setFromX(fromX);
        translateTransition.setToX(toX);
        if (event != null)
            translateTransition.setOnFinished(event);
        translateTransition.play();
    }

    public static void translateY(double fromY, double toY, long ms, Node node) {
        translateY(fromY, toY, ms, node, null);
    }

    public static void translateY(double fromY, double toY, long ms, Node node, EventHandler<ActionEvent> event) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ms), node);
        translateTransition.setFromY(fromY);
        translateTransition.setToY(toY);
        if (event != null)
            translateTransition.setOnFinished(event);
        translateTransition.play();
    }

    public static void hideWithAlphaAndScale(long ms, Node node) {
        new Timeline(new KeyFrame(
                Duration.millis(ms),
                new KeyValue(node.opacityProperty(), 0),
                new KeyValue(node.scaleXProperty(), 0.1),
                new KeyValue(node.scaleYProperty(), 0.1)
        )).play();
    }

    public static void windowAlpha(double to, long ms, Window node) {
        new Timeline(new KeyFrame(
                Duration.millis(ms),
                new KeyValue(node.opacityProperty(), to)
        )).play();
    }

    public static void rotate360(long ms, Node node) {
        new Timeline(new KeyFrame(
                Duration.millis(ms),
                new KeyValue(node.rotateProperty(), 360)
        )).play();
    }

    public static void stageX(double to, long ms, Stage stage, boolean close) {
        WritableValue<Double> writableValue = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return stage.getX();
            }

            @Override
            public void setValue(Double aDouble) {
                stage.setX(aDouble);
            }
        };

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(ms),
                new KeyValue(writableValue, to)
        ));

        if (close)
            timeline.setOnFinished(actionEvent -> {
                try {
                    stage.close();
                } catch (Exception ignore) {
                }
            });
        timeline.play();
    }
}
