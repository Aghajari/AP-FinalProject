package com.aghajari.ui.dialogs;

import com.aghajari.MainUI;
import com.aghajari.ui.ContentCreator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public abstract class DialogBase implements ContentCreator {

    @Override
    public Node create() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefWidth(MainUI.mainRoot.getWidth());
        anchorPane.setPrefHeight(MainUI.mainRoot.getHeight());

        Pane shadow = new Pane();
        shadow.setStyle("-fx-background-color: #000000A0");
        shadow.setPrefWidth(anchorPane.getPrefWidth());
        shadow.setPrefHeight(anchorPane.getPrefHeight());
        shadow.setOpacity(0);
        anchorPane.getChildren().add(shadow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(shadow.opacityProperty(), 1)
                ));

        shadow.setOnMouseClicked(mouseEvent -> {
            timeline.stop();
            hide(anchorPane);
        });

        MainUI.mainRoot.getChildren().add(anchorPane);
        show(anchorPane);
        timeline.play();

        return anchorPane;
    }

    public abstract void show(AnchorPane pane);

    public void hide(AnchorPane pane) {
        Timeline timeline2 = new Timeline(
                new KeyFrame(Duration.millis(200),
                        e -> MainUI.mainRoot.getChildren().remove(pane),
                        new KeyValue(pane.getChildren().get(0).opacityProperty(), 0)
                ));
        timeline2.play();
    }
}
