package com.aghajari.ui.contents;

import com.aghajari.store.EasyApi;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;

public class Bot extends AbstractContentCreator<Pane> {

    String link = null;

    @Override
    String getFXMLName() {
        return "content_bot";
    }

    @Override
    void create(Pane node) {
        Node bot = Utils.findViewById(tmp, "bot");
        TextField tv = Utils.findViewById(node, "api");
        Node disconnect = Utils.findViewById(node, "disconnect");

        EasyApi.getBotLink(model -> {
            link = model.get();
            if (link != null) {
                tv.setText(link);
                disconnect.setVisible(true);
            }
        });

        MFXProgressSpinner loading = Utils.findViewById(node, "loading");

        tv.textProperty().addListener((observableValue, s, t1)
                -> animate(bot, !tv.getText().trim().isEmpty()));

        Label lbl = Utils.findViewById(node, "btn_bot");
        Node r = Utils.findViewById(node, "ripple_bot");
        MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.CHECK);
        r.setOnMouseClicked(mouseEvent -> {
            String api = tv.getText().trim();
            if (!api.toLowerCase().startsWith("https://")) {
                Toast.makeText("Api must start with https://");
                return;
            }
            if (api.length() < 14) {
                Toast.makeText("Invalid Api!");
                return;
            }

            loading.setVisible(true);
            EasyApi.updateBotLink(tv.getText(), model -> {
                loading.setVisible(false);
                Toast.makeText("Connected :)");
                disconnect.setVisible(true);
            });
        });

        disconnect.setOnMouseClicked(event -> {
            EasyApi.updateBotLink("", model -> Toast.makeText("Disconnected"));
            disconnect.setVisible(false);
            link = null;
            tv.setText("");
        });

    }

    void animate(Node r2, boolean show) {
        if (r2.isVisible() == show)
            return;

        r2.setScaleX(show ? 0 : 1);
        r2.setScaleY(show ? 0 : 1);
        r2.setVisible(true);
        Timeline tl = new Timeline(new KeyFrame(
                Duration.millis(150),
                new KeyValue(r2.scaleXProperty(), show ? 1 : 0),
                new KeyValue(r2.scaleYProperty(), show ? 1 : 0)
        ));
        if (!show)
            tl.setOnFinished(actionEvent -> r2.setVisible(false));
        tl.play();
    }

}
