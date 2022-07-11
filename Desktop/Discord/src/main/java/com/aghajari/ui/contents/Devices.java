package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.shared.models.*;
import com.aghajari.store.EasyApi;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXScrollPane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;

import java.io.IOException;
import java.util.List;


public class Devices extends AbstractContentCreator<Pane> {

    @Override
    String getFXMLName() {
        return "content_devices";
    }

    @Override
    void create(Pane node) {
        MFXScrollPane scroll = Utils.findViewById(node, "scroll");
        VBox box = new VBox(12);
        box.getChildren().add(new Rectangle(0, 24));
        scroll.setContent(box);

        EasyApi.getDevices(model -> {
            try {
                List<DeviceInfo> list = model.get();
                for (DeviceInfo inf : list) {
                    if (inf != null)
                        addDevice(box, inf);
                }
            } catch (Exception ignore) {
            }
        });
    }


    static void addDevice(VBox box, DeviceInfo info) throws IOException {
        Node node = FXMLLoader.load(Main.class.getResource("fxml/content_search_item.fxml"));

        Label lbl = Utils.findViewById(node, "name");
        if (box.getChildren().size() == 1) {
            lbl.setStyle("-fx-text-fill: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #5ed3f7ff 0.0%, #1763c6ff 100.0%);");
        }
        Label lbl2 = Utils.findViewById(node, "full_name");
        lbl.setText(info.name);
        lbl2.setText(info.os);

        ImageView img = Utils.findViewById(node, "img");
        img.setClip(new Circle(25, 25, 25));

        String name = switch (info.type) {
            case DeviceInfo.MAC -> "mac";
            case DeviceInfo.ANDROID -> "android";
            default -> "win";
        };
        img.setImage(new Image(Main.class.getResourceAsStream("images/os_" + name + ".png")));

        if (box.getChildren().size() > 1) {
            lbl = Utils.findViewById(node, "btn_delete_request");
            Node r = Utils.findViewById(node, "ripple_delete_request");
            Utils.findViewById(node, "delete_request").setVisible(true);
            MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.CANCEL);
            r.setOnMouseClicked(mouseEvent -> {
                box.getChildren().remove(node);
                EasyApi.terminate(info);
            });
        }

        EventHandler<MouseEvent> handler = new EventHandler<>() {
            int c = 50;
            Timeline animation;
            final Rectangle rectangle = new Rectangle(img.getFitWidth(), img.getFitHeight());

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (animation != null)
                    animation.stop();

                if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
                    c = 50;
                    animation = new Timeline(new KeyFrame(Duration.millis(5), e -> {
                        if (c > 0) {
                            c--;
                            rectangle.setArcWidth(c);
                            rectangle.setArcHeight(c);
                            img.setClip(rectangle);
                        }
                    }));
                } else {
                    c = 24;
                    animation = new Timeline(new KeyFrame(Duration.millis(5), e -> {
                        if (c < 50) {
                            c++;
                            rectangle.setArcWidth(c);
                            rectangle.setArcHeight(c);
                            img.setClip(rectangle);
                        }
                    }));
                }
                animation.setCycleCount(26);
                animation.play();
            }
        };

        Node touch = Utils.findViewById(node, "touch");
        touch.setOnMouseEntered(handler);
        touch.setOnMouseExited(handler);
        box.getChildren().add(node);
    }
}
