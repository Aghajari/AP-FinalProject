package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class DesignServer extends AbstractContentCreator<Pane> {

    private final ServerModel serverModel;
    private static final int PADDING = 10;
    private AnchorPane items;
    private final ArrayList<Node> itemsInOrder = new ArrayList<>();

    public DesignServer(ServerModel serverModel) {
        this.serverModel = serverModel;
    }

    @Override
    String getFXMLName() {
        return "content_design_server";
    }

    @Override
    void create(Pane node) {
        items = new AnchorPane();
        items.setPadding(new Insets(0, 0, 24 + 56 + 24, 0));

        MFXScrollPane scroll = Utils.findViewById(node, "scroll");
        scroll.setContent(items);

        Label lbl = Utils.findViewById(node, "btn");
        IconNode iconNode = new IconNode(GoogleMaterialDesignIcons.ADD);
        iconNode.setFill(Color.WHITE);
        iconNode.setIconSize(30);
        lbl.setGraphic(iconNode);

        Node r = Utils.findViewById(node, "ripple");
        r.setClip(new Circle(30, 30, 30));

        r.setOnMouseClicked(mouseEvent -> addItem(null));

        if (serverModel.channels == null)
            serverModel.channels = new ArrayList<>();

        for (ServerModel.ServerChannel c : serverModel.channels)
            addItem(c);

        Utils.findViewById(node, "apply")
                .setOnMouseClicked(event -> {
                    serverModel.channels.clear();
                    for (Node item : itemsInOrder) {
                        ServerModel.ServerChannel c = (ServerModel.ServerChannel) item.getUserData();
                        if (c.name == null || c.name.trim().isEmpty())
                            continue;
                        serverModel.channels.add(c);
                    }
                    EasyApi.updateServerChannels(serverModel);

                    HomeController.forceUpdate = true;
                    HomeController.keepOldIndex = true;
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();
                });
    }

    void addItem(ServerModel.ServerChannel channel) {
        try {
            if (channel == null)
                channel = new ServerModel.ServerChannel("", Utils.rnd(), 0);
            ServerModel.ServerChannel finalChannel = channel;

            Node item = FXMLLoader.load(Main.class.getResource("fxml/design_item.fxml"));
            item.setUserData(finalChannel);

            TextField edt = Utils.findViewById(item, "edt");
            edt.setText(finalChannel.name);
            edt.textProperty().addListener((observableValue, s, t1) ->
                    finalChannel.name = edt.getText().trim());

            Label lbl2 = Utils.findViewById(item, "drag");
            IconNode iconNode2 = new IconNode(GoogleMaterialDesignIcons.DRAG_HANDLE);
            iconNode2.setFill(Color.GRAY);
            iconNode2.setIconSize(22);
            lbl2.setGraphic(iconNode2);
            dragNode(item, lbl2);

            lbl2 = Utils.findViewById(item, "btn_channel");
            iconNode2 = new IconNode(GoogleMaterialDesignIcons.HEADSET);
            iconNode2.setFill(Color.WHITE);
            iconNode2.setIconSize(24);
            lbl2.setGraphic(iconNode2);
            Node r2 = Utils.findViewById(item, "ripple_channel");
            r2.setClip(new Circle(20, 20, 20));
            r2.setOnMouseClicked(event -> {
                animate(Utils.findViewById(item, "channel"), false);
                animate(Utils.findViewById(item, "group"), true);
                edt.setPromptText("Group Name");
                finalChannel.type = 1;
            });

            lbl2 = Utils.findViewById(item, "btn_group");
            iconNode2 = new IconNode(GoogleMaterialDesignIcons.GROUP);
            iconNode2.setFill(Color.WHITE);
            iconNode2.setIconSize(24);
            lbl2.setGraphic(iconNode2);
            r2 = Utils.findViewById(item, "ripple_group");
            r2.setClip(new Circle(20, 20, 20));
            r2.setOnMouseClicked(event -> {
                animate(Utils.findViewById(item, "channel"), true);
                animate(Utils.findViewById(item, "group"), false);
                edt.setPromptText("Channel Name");
                finalChannel.type = 0;
            });

            if (finalChannel.type == 1) {
                edt.setPromptText("Group Name");
                Utils.findViewById(item, "group").setVisible(true);
                Utils.findViewById(item, "channel").setVisible(false);
            }

            item.setLayoutX(24);
            item.setLayoutY(24 + (itemsInOrder.size() * 56) + (itemsInOrder.size() * PADDING));
            items.getChildren().add(item);
            itemsInOrder.add(item);
        } catch (IOException ignore) {
        }
    }

    public void dragNode(Node node, Node listen) {
        AtomicReference<Double> x = new AtomicReference<>();
        AtomicReference<Double> y = new AtomicReference<>();
        AtomicReference<Integer> i = new AtomicReference<>();
        AtomicReference<Integer> type = new AtomicReference<>();

        listen.setOnMousePressed(mouseEvent -> {
            node.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 10, 0.5, 0.0, 0.0);");
            type.set(0);
            i.set(itemsInOrder.indexOf(node) + 1);
            items.getChildren().remove(node);
            items.getChildren().add(node);
            y.set(node.getLayoutY() - mouseEvent.getSceneY());
            x.set(node.getLayoutX() - mouseEvent.getSceneX());
            mouseEvent.consume();
        });
        listen.setOnMouseReleased(mouseEvent -> {
            node.setCursor(Cursor.DEFAULT);
            if (type.get() == 1) {
                int p = i.get() - 1;
                new Timeline(new KeyFrame(
                        new Duration(150),
                        new KeyValue(node.layoutYProperty(),
                                24 + (p * 56) + (p * PADDING))
                )).play();
            } else if (type.get() == 2) {
                if (node.getOpacity() >= 0.5) {
                    new Timeline(new KeyFrame(
                            new Duration(150),
                            new KeyValue(node.opacityProperty(), 1),
                            new KeyValue(node.layoutXProperty(), 24)
                    )).play();
                } else {
                    new Timeline(new KeyFrame(
                            new Duration(150),
                            e -> items.getChildren().remove(node),
                            new KeyValue(node.opacityProperty(), 0),
                            new KeyValue(node.layoutXProperty(), node.getLayoutX() - 100)
                    )).play();
                    for (int j = i.get(); j < itemsInOrder.size(); j++) {
                        new Timeline(new KeyFrame(
                                new Duration(150),
                                new KeyValue(itemsInOrder.get(j).layoutYProperty(),
                                        24 + ((j - 1) * 56) + ((j - 1) * PADDING))
                        )).play();
                    }
                    itemsInOrder.remove(i.get() - 1);
                }
            }
            node.setStyle("");
            mouseEvent.consume();
        });
        listen.setOnMouseDragged(mouseEvent -> {
            if (type.get() == 0) {
                double dy = Math.abs(mouseEvent.getSceneY() + y.get() - node.getLayoutY());
                double dx = Math.abs(mouseEvent.getSceneX() + x.get() - node.getLayoutX());

                if (dx >= 2 || dy >= 2)
                    type.set(Double.compare(dy, dx) > 0 ? 1 : 2);
            }

            if (type.get() == 1) {
                node.setLayoutY(Math.max(24, mouseEvent.getSceneY() + y.get()));

                int newIndex = (int) Math.round(((node.getLayoutY() - 24) / (56 + PADDING)) + 1);
                if (i.get() != newIndex) {
                    if (newIndex >= 1 && newIndex <= itemsInOrder.size()) {
                        int p = i.get() - 1;
                        new Timeline(new KeyFrame(
                                new Duration(150),
                                new KeyValue(itemsInOrder.get(newIndex - 1).layoutYProperty(),
                                        24 + (p * 56) + (p * PADDING))
                        )).play();
                        Collections.swap(itemsInOrder, p, newIndex - 1);
                        i.set(newIndex);
                    }
                }

            } else if (type.get() == 2) {
                node.setLayoutX(mouseEvent.getSceneX() + x.get());
                double lx = Math.abs(node.getLayoutX() - 24);
                node.setOpacity(1 - lx / ((Pane) node).getPrefWidth());
            }
            mouseEvent.consume();
        });
        listen.setOnMouseEntered(event -> node.setCursor(Cursor.HAND));
    }

    void animate(Node r, boolean show) {
        r.setScaleX(show ? 0 : 1);
        r.setScaleY(show ? 0 : 1);
        r.setVisible(true);
        Timeline tl = new Timeline(new KeyFrame(
                Duration.millis(300),
                new KeyValue(r.scaleXProperty(), show ? 1 : 0),
                new KeyValue(r.scaleYProperty(), show ? 1 : 0)
        ));
        if (!show)
            tl.setOnFinished(actionEvent -> r.setVisible(false));
        tl.play();
    }

    @Override
    public boolean isEditablePage() {
        return true;
    }
}
