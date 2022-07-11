package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.FriendshipRequestModel;
import com.aghajari.shared.models.IDModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jiconfont.IconCode;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;

import java.io.IOException;
import java.util.List;


public class MutualFriends extends AbstractContentCreator<Pane> {

    private static Image no_friends = null;
    private Node selectedNode;
    private int index = -1;

    public MutualFriends(UserModel model) {
        user = model;
    }

    public static Image getNoFriendsImage() {
        if (no_friends == null) {
            no_friends = new Image(
                    Main.class.getResource("images/no_friends.png")
                            .toExternalForm(), true);
        }
        return no_friends;
    }

    private final UserModel user;


    @Override
    String getFXMLName() {
        return "content_mutual_friends";
    }

    @Override
    void create(Pane node) {
        StaticListeners.friendshipUpdater = this::load;
        ((ImageView) Utils.findViewById(node, "img"))
                .setImage(getNoFriendsImage());

        EasyApi.friendshipRequest(FriendshipRequestModel.NOTIFY_FRIENDSHIP, user);
        EasyApi.getMutualFriends(model -> {
            List<IDModel> list = model.get();
            if (!list.isEmpty()) {
                Utils.findViewById(node, "box").setVisible(false);

                VBox box = new VBox(12);
                try {
                    box.getChildren().add(new Rectangle(0, 24));
                    int len = list.size();
                    for (int i = 0; i <len; i++) {
                        MyFriends.initUser(box, list.get(i).getUser(), 0, null, i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((MFXScrollPane) Utils.findViewById(node, "scroll")).setContent(box);
            }
        }, user.getId());
        // setBgColor("linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #5ed3f7ff 0.0%, #1763c6ff 100.0%);");
        // setBgColor("linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #006633 0.0%, #339966 100.0%);");
    }

    public void load(FriendshipModel model) {
        if (model.fromId.equals(user.getId()) ||
                model.toId.equals(user.getId())) {
            System.out.println(model);

            if (selectedNode != null)
                animate(selectedNode, false);
            index = model.index;

            if (model.exists()) {
                if (model.accepted) {
                    deleteRequest();
                } else {
                    if (model.fromId.equals(user.getId())) {
                        requested();
                    } else {
                        sentRequest();
                    }
                }
            } else {
                sendRequest();
            }
        }
    }

    void sendRequest() {
        Label lbl = Utils.findViewById(tmp, "btn_send_request");
        Node r = Utils.findViewById(tmp, "ripple_send_request");
        init(lbl, r, GoogleMaterialDesignIcons.PERSON_ADD);

        lbl = Utils.findViewById(tmp, "tv");
        lbl.setText("Do you wanna send a friend request to " + user.nickname + "?");
        Node r2 = Utils.findViewById(tmp, "send_request");
        animate(r2, true);

        r.setOnMouseClicked(mouseEvent -> {
            EasyApi.friendshipRequest(FriendshipRequestModel.SEND_REQUEST, user);
            animate(r2, false);
            sentRequest();
        });
    }

    void sentRequest() {
        Label lbl = Utils.findViewById(tmp, "btn_sent_request");
        Node r = Utils.findViewById(tmp, "ripple_sent_request");
        init(lbl, r, GoogleMaterialDesignIcons.DELETE);

        lbl = Utils.findViewById(tmp, "tv");
        lbl.setText("Friend request has been sent, Waiting for response!");
        Node r2 = Utils.findViewById(tmp, "sent_request");
        animate(r2, true);

        r.setOnMouseClicked(mouseEvent -> {
            EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP, user, index);
            animate(r2, false);
            sendRequest();
        });
    }

    void deleteRequest() {
        Label lbl = Utils.findViewById(tmp, "btn_delete_request");
        Node r = Utils.findViewById(tmp, "ripple_delete_request");
        init(lbl, r, GoogleMaterialDesignIcons.DELETE);

        lbl = Utils.findViewById(tmp, "tv");
        lbl.setText("\u200E" + user.nickname + " is your friend :)");
        Node r2 = Utils.findViewById(tmp, "delete_request");
        animate(r2, true);

        r.setOnMouseClicked(mouseEvent -> {
            EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP, user, index);
            animate(r2, false);
            sendRequest();
        });
    }

    void requested() {
        Node r2 = Utils.findViewById(tmp, "request");
        Label lbl = Utils.findViewById(tmp, "btn_accept_request");
        Node r = Utils.findViewById(tmp, "ripple_accept_request");
        init(lbl, r, GoogleMaterialDesignIcons.CHECK);
        r.setOnMouseClicked(mouseEvent -> {
            EasyApi.friendshipRequest(FriendshipRequestModel.ACCEPT_REQUEST, user, index);
            animate(r2, false);
            deleteRequest();
        });

        lbl = Utils.findViewById(tmp, "btn_reject_request");
        r = Utils.findViewById(tmp, "ripple_reject_request");
        init(lbl, r, GoogleMaterialDesignIcons.CLOSE);
        r.setOnMouseClicked(mouseEvent -> {
            EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP, user, index);
            animate(r2, false);
            sendRequest();
        });

        lbl = Utils.findViewById(tmp, "tv");
        lbl.setText("\u200E" + user.nickname + " wants to be your friend!");
        animate(r2, true);
    }

    protected static void init(Label lbl, Node ripple, IconCode code) {
        IconNode iconNodeAnswer = new IconNode(code);
        iconNodeAnswer.setFill(Color.WHITE);
        iconNodeAnswer.setIconSize(24);
        lbl.setGraphic(iconNodeAnswer);

        ripple.setClip(new Circle(20, 20, 20));
    }

    private Timeline oldTimeline;

    void animate(Node r2, boolean show) {
        if (show && oldTimeline != null)
            oldTimeline.stop();

        if (r2 == selectedNode && show) {
            r2.setScaleX(1);
            r2.setScaleY(1);
            r2.setVisible(true);
            return;
        }

        r2.setScaleX(show ? 0 : 1);
        r2.setScaleY(show ? 0 : 1);
        r2.setVisible(true);
        Timeline tl = new Timeline(new KeyFrame(
                Duration.millis(300),
                new KeyValue(r2.scaleXProperty(), show ? 1 : 0),
                new KeyValue(r2.scaleYProperty(), show ? 1 : 0)
        ));
        if (!show)
            tl.setOnFinished(actionEvent -> r2.setVisible(false));
        tl.play();
        if (show) {
            selectedNode = r2;
            oldTimeline = tl;
        }
    }

    @Override
    public boolean isEditablePage() {
        return true;
    }
}
