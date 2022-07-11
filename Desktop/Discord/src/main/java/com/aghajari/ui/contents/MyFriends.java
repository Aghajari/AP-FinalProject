package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.IDFinder;
import com.aghajari.shared.models.FriendshipModel;
import com.aghajari.shared.models.FriendshipRequestModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticStorage;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.ui.dialogs.OpenChatDialog;
import com.aghajari.ui.dialogs.PermissionsDialog;
import com.aghajari.util.PermissionUtils;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.ArrayList;
import java.util.List;


public class MyFriends extends AbstractContentCreator<Pane> {

    boolean isFull;

    @Override
    String getFXMLName() {
        return "content_friends";
    }

    @Override
    public Node create() throws IOException {
        init();
        StaticListeners.friendshipUpdater = model -> {
            EasyApi.getListOfMyFriends();
            EasyApi.getListOfPendingFriends();
        };
        return super.create();
    }

    @Override
    void create(Pane node) {
        callApi();

        ((Label) Utils.findViewById(tmp, "empty")).setText(getEmptyText());
        ((ImageView) Utils.findViewById(node, "img")).setImage(
                new Image(Main.class.getResource("images/" + getImage()).toExternalForm(), true)
        );

        load();

        TextField edt = Utils.findViewById(node, "edt");
        edt.setPromptText(getSearchPrompt());
        edt.textProperty().addListener(new ChangeListener<>() {

            Timeline timeline = null;

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (timeline != null)
                    timeline.stop();

                if (edt.getText().length() >= 4 || !isFull) {
                    timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> load()));
                    timeline.play();
                }
            }
        });
    }

    public void load() {
        Node box = Utils.findViewById(tmp, "box");
        Node box2 = Utils.findViewById(tmp, "searchBox");
        MFXScrollPane scroll = Utils.findViewById(tmp, "scroll");
        TextField edt = Utils.findViewById(tmp, "edt");
        Node noUser = Utils.findViewById(tmp, "noUser");

        List<IDFinder> list;

        String searchFor = edt.getText().trim();
        scroll.setContent(null);
        noUser.setVisible(false);
        box.setVisible(false);

        List<? extends IDFinder> org = getList();
        if (box2.isVisible() && searchFor.length() >= 4) {
            list = new ArrayList<>();
            synchronized (getList()) {
                for (IDFinder m : org) {
                    if (m.getUser() == null)
                        continue;
                    if (m.getUser().nickname.toLowerCase().contains(searchFor.toLowerCase())
                            || (m.getUser().username != null && m.getUser().username.toLowerCase().contains(searchFor.toLowerCase())))
                        list.add(m);
                }
            }
            if (list.isEmpty())
                noUser.setVisible(true);
        } else {
            list = new ArrayList<>(org);
            box.setVisible(list.isEmpty());
            box2.setVisible(!box.isVisible());
        }
        isFull = list.size() == org.size();

        if (!list.isEmpty()) {
            Platform.runLater(() -> initUsers(list, scroll));
        }
    }

    private void initUsers(List<IDFinder> list, MFXScrollPane scroll) {
        VBox box = new VBox(12);
        try {

            box.getChildren().add(new Rectangle(0, 24));
            int len = list.size();
            for (int i = 0; i < len; i++) {
                IDFinder fm = list.get(i);
                UserModel model = fm.getUser();
                if (model == null)
                    continue;

                initUser(box, model, getType(), fm, i, getServer());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scroll.setContent(box);
    }

    public ServerModel getServer() {
        return null;
    }

    public List<? extends IDFinder> getList() {
        return StaticStorage.friendships;
    }

    public void init() {
        StaticListeners.updateFriendshipList = this::load;
    }

    public void callApi() {
        EasyApi.getListOfMyFriends();
    }

    public String getImage() {
        return "friends.png";
    }

    public String getEmptyText() {
        return "You have no friends, Nobody likes you :D";
    }

    public String getSearchPrompt() {
        return "Search for friends";
    }

    public int getType() {
        return 1;
    }

    static void initUser(VBox box, UserModel model, int type, IDFinder finder, int index) throws IOException {
        initUser(box, model, type, finder, index, null);
    }

    static void initUser(VBox box, UserModel model, int type, IDFinder finder, int index, ServerModel server) throws IOException {
        if (type != 3 && model.getId().equals(MyInfo.getInstance().getId()))
            return;

        Node node = FXMLLoader.load(Main.class.getResource("fxml/content_search_item.fxml"));
        FriendshipModel fm = finder instanceof FriendshipModel ? (FriendshipModel) finder : null;

        if (type == 1) {
            Label lbl = Utils.findViewById(node, "btn_delete_request");
            Node r = Utils.findViewById(node, "ripple_delete_request");
            Utils.findViewById(node, "delete_request").setVisible(true);
            MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.DELETE);
            r.setOnMouseClicked(mouseEvent -> {
                EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP, model, fm.index);
                EasyApi.getListOfMyFriends();
            });
        } else if (type == 2 && fm != null) {
            if (fm.fromId.equals(MyInfo.getInstance().getId())) {
                Label lbl = Utils.findViewById(node, "btn_sent_request");
                Node r = Utils.findViewById(node, "ripple_sent_request");
                Utils.findViewById(node, "sent_request").setVisible(true);
                MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.DELETE);
                r.setOnMouseClicked(mouseEvent -> {
                    EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP, model, fm.index);
                    EasyApi.getListOfPendingFriends();
                });
            } else {
                Label lbl = Utils.findViewById(node, "btn_accept_request");
                Node r = Utils.findViewById(node, "ripple_accept_request");
                MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.CHECK);
                r.setOnMouseClicked(mouseEvent -> {
                    EasyApi.friendshipRequest(FriendshipRequestModel.ACCEPT_REQUEST, model, fm.index);
                    EasyApi.getListOfPendingFriends();
                    EasyApi.getListOfMyFriends();
                });

                lbl = Utils.findViewById(node, "btn_reject_request");
                r = Utils.findViewById(node, "ripple_reject_request");
                MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.CLOSE);
                r.setOnMouseClicked(mouseEvent -> {
                    EasyApi.friendshipRequest(FriendshipRequestModel.CANCEL_FRIENDSHIP, model, fm.index);
                    EasyApi.getListOfPendingFriends();
                });

                Utils.findViewById(node, "request").setVisible(true);
            }
        } else if (server != null && type == 3 &&
                !model.getId().equals(MyInfo.getInstance().getId()) &&
                !server.owner.equals(model.getId())) {

            if (server.owner.equals(MyInfo.getInstance().getId()) ||
                    PermissionUtils.canChangePermissions(server)) {
                Label lbl = Utils.findViewById(node, "btn_permissions");
                Node r = Utils.findViewById(node, "ripple_permissions");
                MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.SECURITY);
                r.setOnMouseClicked(mouseEvent -> {
                    new PermissionsDialog(server, model).create();
                });
            } else {
                Utils.findViewById(node, "btn_permissions").setVisible(false);
                Utils.findViewById(node, "ripple_permissions").setVisible(false);
            }

            if (server.owner.equals(MyInfo.getInstance().getId()) ||
                    PermissionUtils.canRemoveMember(server)) {
                Label lbl = Utils.findViewById(node, "btn_remove");
                Node r = Utils.findViewById(node, "ripple_remove");
                MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.REMOVE);
                r.setOnMouseClicked(mouseEvent -> {
                    EasyApi.removeFromServer(server.serverId, model.getId());
                    HomeController.forceUpdate = true;
                    HomeController.keepOldIndex = true;
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();
                });
            } else {
                Utils.findViewById(node, "btn_remove").setVisible(false);
                Utils.findViewById(node, "ripple_remove").setVisible(false);
            }
            Utils.findViewById(node, "server").setVisible(true);
        }

        Label lbl = Utils.findViewById(node, "name");
        Label lbl2 = Utils.findViewById(node, "full_name");
        lbl.setText(model.getName());
        lbl2.setText(model.nickname);

        ImageView img = Utils.findViewById(node, "img");
        img.setClip(new Circle(25, 25, 25));
        Utils.loadAvatar(model, img);

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
                    lbl.setStyle("-fx-text-fill: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #5ed3f7ff 0.0%, #1763c6ff 100.0%);");
                    lbl2.setStyle("-fx-text-fill: white;");
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

                    lbl.setStyle("-fx-text-fill: lightgray;");
                    lbl2.setStyle("-fx-text-fill: gray;");
                }
                animation.setCycleCount(26);
                animation.play();

            }
        };

        Node touch = Utils.findViewById(node, "touch");
        touch.setOnMouseEntered(handler);
        touch.setOnMouseExited(handler);
        touch.setOnMouseClicked(mouseEvent -> {
            if (model.getId().equals(MyInfo.getInstance().getId()))
                return;
            new OpenChatDialog(model, img.imageProperty()).create();
        });

        if (index <= 10) {
            node.setOpacity(0);
            node.setTranslateY(56);
            new Timeline(
                    new KeyFrame(
                            Duration.millis(200),
                            new KeyValue(node.opacityProperty(), 1),
                            new KeyValue(node.translateYProperty(), 0)
                    )
            ).play();
        }
        box.getChildren().add(node);
    }

    @Override
    public boolean isEditablePage() {
        return true;
    }
}
