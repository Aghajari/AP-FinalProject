package com.aghajari.util;

import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.MessageModel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class NotificationCenter {

    private static final long SCALE_DURATION = 150;
    private static final long X_DURATION = 400;
    private static final long SHOW_DURATION = 1800;
    private static final long HIDE_DURATION = 150;

    private static final long DURATION =
            (SCALE_DURATION * 2) + X_DURATION +
                    SHOW_DURATION + HIDE_DURATION + 100;

    private static long lastTime = 0;

    private static Pane panel;
    private static ImageView avatar;
    private static Label label;

    public static void init(Pane panel) {
        NotificationCenter.panel = panel;
        avatar = (ImageView) panel.getChildren().get(0);
        label = (Label) panel.getChildren().get(1);

        double r = avatar.getFitWidth() / 2;
        avatar.setClip(new Circle(r, r, r));

        panel.prefWidthProperty().addListener((observableValue, number, t1) -> {
            Rectangle rect = new Rectangle(panel.getPrefWidth(), panel.getMaxHeight());
            rect.setArcHeight(21);
            rect.setArcWidth(21);
            panel.setLayoutX(Toast.ownerStage.getWidth() / 2 - panel.getPrefWidth() / 2);
            panel.setClip(rect);
        });
        panel.setPrefWidth(panel.getMinWidth());
    }

    public static void notify(MessageModel model) {
        if (panel == null)
            return;
        if (model.fromId.equals(MyInfo.getInstance().getId()))
            return;
        if (!model.toId.equals(MyInfo.getInstance().getId()))
            return;
        if (model.getUser() == null)
            return;

        if (System.currentTimeMillis() - lastTime < DURATION) {
            new Timeline(new KeyFrame(
                    Duration.millis(DURATION - System.currentTimeMillis() + lastTime),
                    e -> notify(model)
            )).play();
            return;
        }

        lastTime = System.currentTimeMillis();
        Platform.runLater(() -> notifyUI(model));
    }

    private static void notifyUI(MessageModel model) {
        Utils.loadAvatar(model.getUser(), avatar);

        String text = model.text.replaceAll("[\\r\\n]+", " ");
        double w = Utils.getTextWidth(text, label.getFont(), 250);
        label.setMinWidth(w);
        label.setMaxWidth(w + 4);
        label.setText(text);

        new SequentialTransition(
                new Timeline(new KeyFrame(
                        Duration.millis(SCALE_DURATION),
                        new KeyValue(panel.scaleXProperty(), 1f),
                        new KeyValue(panel.scaleYProperty(), 1f)
                )),
                new Timeline(new KeyFrame(Duration.millis(SCALE_DURATION))),
                new Timeline(new KeyFrame(
                        Duration.millis(X_DURATION),
                        new KeyValue(panel.prefWidthProperty(), w + 16 + label.getLayoutX())
                )),
                new Timeline(new KeyFrame(Duration.millis(SHOW_DURATION))),
                new Timeline(new KeyFrame(
                        Duration.millis(HIDE_DURATION),
                        e -> {
                            panel.setScaleX(0f);
                            panel.setScaleY(0f);
                            panel.setTranslateY(0f);
                            panel.setPrefWidth(panel.getMinWidth());
                        },
                        new KeyValue(panel.translateYProperty(), -100)
                ))
        ).play();
    }
}
