package com.aghajari.ui.dialogs;

import com.aghajari.Main;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;

public abstract class AbstractDialog extends DialogBase {

    private Pane dialog;
    private Timeline timeline;
    private boolean isShowing = false;

    public abstract String getLayoutFXML();
    public abstract void load(AnchorPane base, Node dialog);

    @Override
    public void show(AnchorPane pane) {
        if (isShowing)
            return;

        isShowing = true;
        try {
            dialog = FXMLLoader.load(Main.class.getResource("fxml/" + getLayoutFXML() + ".fxml"));
            dialog.setLayoutX(pane.getPrefWidth() / 2 - dialog.getPrefWidth() / 2);
            dialog.setLayoutY(pane.getPrefHeight() / 2 - dialog.getPrefHeight() / 2);

            load(pane, dialog);
            dialog.setScaleX(0);
            dialog.setScaleY(0);
            dialog.setOpacity(0);
            pane.getChildren().add(dialog);

            timeline = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(dialog.opacityProperty(), 1),
                            new KeyValue(dialog.scaleXProperty(), 1),
                            new KeyValue(dialog.scaleYProperty(), 1)
                    ));
            timeline.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hide(AnchorPane pane) {
        if (!isShowing)
            return;
        isShowing = false;


        super.hide(pane);
        timeline.stop();
        timeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(dialog.opacityProperty(), 0),
                        new KeyValue(dialog.scaleXProperty(), 0.2),
                        new KeyValue(dialog.scaleYProperty(), 0.2)
                ));
        timeline.play();
    }
}
