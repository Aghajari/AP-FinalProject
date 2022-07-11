package com.aghajari.ui;


import com.aghajari.Main;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.StaticStorage;
import com.aghajari.ui.contents.*;
import com.aghajari.util.Animations;
import com.aghajari.util.NotificationCenter;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;

import java.io.IOException;
import java.util.ArrayList;

public class HomeController {

    @FXML
    public MFXScrollPane chats, items;
    @FXML
    public AnchorPane content;
    public AnchorPane item_content = new AnchorPane();
    @FXML
    public AnchorPane notification;

    private Pane selected, selectedItem;
    private int selectedItemIndex = 1, selectedIndex = 1;
    private com.aghajari.shared.Profile selectedProfile = null;

    public void initialize() {
        StaticListeners.updateOpenedChatsListener =
                () -> Platform.runLater(this::createChats);
        StaticListeners.updateProfile =
                (model) -> Platform.runLater(() -> updateProfile(model));
        StaticListeners.updateOnlineStatus = () -> {

            if (Profile.KEY.equals(content.getChildren().get(0).getId()))
                Platform.runLater(() ->
                        ((Profile) content.getChildren().get(0).getUserData())
                                .load());

            if (Call.KEY.equals(content.getChildren().get(0).getId()))
                Platform.runLater(() ->
                        ((Call) content.getChildren().get(0).getUserData())
                                .load());
        };
        Platform.runLater(this::initNow);
    }

    public void updateProfile(UserModel model) {
        if (StaticListeners.updateProfileContent != null) {
            if (StaticListeners.updateProfileContent.update(model))
                return;
        }
        if (chats.getContent() != null) {
            Pane pane = (Pane) chats.getContent();
            for (Node n : pane.getChildren()) {
                if (n instanceof Pane
                        && n.getUserData() != null
                        && n.getUserData() instanceof UserModel) {
                    if (((UserModel) n.getUserData()).getId().equals(model.getId())) {
                        ImageView img = (ImageView) ((Pane) n).getChildren().get(0);
                        Utils.loadAvatar((UserModel) n.getUserData(), img);
                        Node selector = ((Pane) n).getChildren().get(2);
                        Label lbl = (Label) ((Pane) n).getChildren().get(1);
                        lbl.setText(((UserModel) n.getUserData()).getName());

                        if (selector.isVisible()) {
                            if (((UserModel) n.getUserData()).getImage() == null) {
                                lbl.setStyle("-fx-text-fill: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #5ed3f7ff 0.0%, #1763c6 100.0%);");
                                selector.setStyle("-fx-background-color: #1763c6ff;");
                            }
                            Utils.loadBg((UserModel) n.getUserData(), selector, false);
                            Utils.loadTextColor((UserModel) n.getUserData(), lbl, selector::isVisible);
                        }
                        return;
                    }
                }
            }
        }
    }

    public void initNow() {
        NotificationCenter.init(notification);

        item_content.prefWidthProperty().bind(items.widthProperty());
        items.setContent(item_content);
        items.hvalueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                items.hvalueProperty().removeListener(this);
                items.setHvalue(0);
                items.hvalueProperty().addListener(this);
            }
        });
        createChats();
    }

    public static boolean requestToOpenChat = false;
    public static boolean requestToOpenServer = false;
    public static boolean keepOldProfile = false;
    public static boolean keepOldIndex = false;
    public static boolean forceUpdate = false;
    public static boolean isOnEditPage = false;
    public static boolean isOnChatPage = false;

    private void createChats() {
        if (isOnEditPage && keepOldIndex && !forceUpdate) {
            keepOldIndex = false;
            return;
        }
        forceUpdate = false;

        ArrayList<com.aghajari.shared.Profile> list = new ArrayList<>();
        list.add(MyInfo.getInstance());

        boolean couldFind = false;

        synchronized (this) {
            int len = StaticStorage.chats.openedChats.size();
            if (!keepOldIndex) {
                selectedIndex = requestToOpenServer ? 2 + len : 1 + (requestToOpenChat ? 1 : 0);
                selectedItemIndex = 1;
            }
            requestToOpenChat = requestToOpenServer = keepOldIndex = false;

            for (int i = len - 1; i >= 0; i--)
                list.add(StaticStorage.chats.openedChats.get(i).getUser());

            len = StaticStorage.chats.servers.size();
            System.out.println(len);
            for (int i = len - 1; i >= 0; i--)
                list.add(StaticStorage.chats.servers.get(i));
        }

        System.out.println(list);
        VBox box = new VBox(10);
        int i = 0;

        int len = list.size();
        for (int index = 0; index < len; index++) {
            com.aghajari.shared.Profile user = list.get(index);
            if (user == null)
                continue;

            String name = user.getName();

            if (i == 0)
                box.getChildren().add(new Rectangle(0, 10));
            i++;
            final int finalI = i;

            AnchorPane parent;
            try {
                parent = FXMLLoader.load(Main.class.getResource("fxml/chat_item.fxml"));
            } catch (IOException e) {
                continue;
            }
            Node selector = parent.getChildren().get(2);
            ImageView img = (ImageView) parent.getChildren().get(0);
            Label lbl = (Label) parent.getChildren().get(1);
            lbl.setText(name);

            Utils.loadAvatar(user, img);

            boolean isSelected = false;

            if (keepOldProfile) {
                if (selectedProfile == user) {
                    couldFind = true;
                    selected = parent;
                    selectedIndex = i;
                    isSelected = true;
                } else {
                    img.setClip(new Circle(25, 25, 25));
                }
            } else {
                if (i == selectedIndex) {
                    selectedProfile = user;
                    couldFind = true;
                    selected = parent;
                    initItemsNow(finalI, user);
                    isSelected = true;
                } else {
                    img.setClip(new Circle(25, 25, 25));
                }
            }

            if (isSelected) {
                selector.setVisible(true);
                lbl.setStyle("-fx-text-fill: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #5ed3f7ff 0.0%, #1763c6ff 100.0%);");
                Utils.loadTextColor(user, lbl, selector::isVisible);
                Utils.loadBg(user, selector, false);
                Rectangle rectangle = new Rectangle(img.getFitWidth(), img.getFitHeight());
                rectangle.setArcHeight(24);
                rectangle.setArcWidth(24);
                img.setClip(rectangle);
            }

            EventHandler<MouseEvent> handler = new EventHandler<>() {
                int c = 50;
                Timeline animation;
                final Rectangle rectangle = new Rectangle(img.getFitWidth(), img.getFitHeight());

                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (animation != null)
                        animation.stop();

                    if (selected == parent)
                        return;

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

                        Utils.loadTextColor(user, lbl, selector::isVisible);
                        animation.setCycleCount(26);
                        animation.play();
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

                        selector.setVisible(false);
                        lbl.setStyle("-fx-text-fill: white;");
                        animation.setCycleCount(26);
                        animation.play();
                    }

                }
            };

            parent.setOnMouseEntered(handler);
            parent.setOnMouseExited(handler);

            parent.setOnMouseClicked(mouseEvent -> {
                if (selected == parent)
                    return;

                selectedItemIndex = 1;
                Node node = selected;
                selected = parent;
                node.getOnMouseExited().handle(new MouseEvent(MouseEvent.MOUSE_EXITED,
                        0.0, 0.0, 0.0, 0.0, null,
                        0, false, false, false, false, false,
                        false, false, false, false, false, null));
                Utils.loadBg(user, selector, false);
                selector.setVisible(true);
                initItemsNow(finalI, user);
                selectedIndex = finalI;
                selectedProfile = user;
            });

            parent.setUserData(user);
            box.getChildren().add(parent);

            if (i == 1 || (index + 1 < len
                    && !(user instanceof ServerModel)
                    && (list.get(index + 1) instanceof ServerModel))) {
                Rectangle divider = new Rectangle(30, 2);
                divider.setStyle("-fx-fill: #2F3136;");
                divider.setTranslateX(34);
                box.getChildren().add(divider);
            }
        }
        keepOldProfile = false;

        if (!couldFind) {
            selectedIndex = selectedItemIndex = 1;
            selectedProfile = null;
            createChats();
            return;
        }
        box.setPadding(new Insets(0, 0, 24, 0));
        chats.setContent(box);
    }

    private void initItemsNow(int to, com.aghajari.shared.Profile userModel) {
        try {
            if (to == 1) {
                initItems(selectedIndex, to,
                        new MenuItem("Profile", GoogleMaterialDesignIcons.PERSON, new Profile((UserModel) userModel)),
                        new MenuItem("Search", GoogleMaterialDesignIcons.SEARCH, new Search()),
                        new MenuItem("Saved Msgs", GoogleMaterialDesignIcons.BOOKMARK, new Chat(MyInfo.getInstance())),
                        new MenuItem("Devices", GoogleMaterialDesignIcons.DEVICES, new Devices()),
                        new MenuItem("Servers", GoogleMaterialDesignIcons.DNS, new Servers()),
                        new MenuItem("Bot", GoogleMaterialDesignIcons.ANDROID, new Bot()),
                        new MenuItem("Online", GoogleMaterialDesignIcons.MOOD, new OnlineFriends()),
                        new MenuItem("Friends", GoogleMaterialDesignIcons.PEOPLE, new MyFriends()),
                        new MenuItem("Pending", GoogleMaterialDesignIcons.PEOPLE_OUTLINE, new PendingFriends()),
                        new MenuItem("Blocked", GoogleMaterialDesignIcons.BLOCK, new BlockList())
                );
            } else if (userModel instanceof UserModel) {
                initItems(selectedIndex, to,
                        new MenuItem("Profile", GoogleMaterialDesignIcons.PERSON, new Profile((UserModel) userModel)),
                        new MenuItem("Mutual Friends", GoogleMaterialDesignIcons.PEOPLE, new MutualFriends((UserModel) userModel)),
                        new MenuItem("Chat", GoogleMaterialDesignIcons.CHAT, new Chat((UserModel) userModel)),
                        new MenuItem("Voice Call", GoogleMaterialDesignIcons.CALL, new Call((UserModel) userModel))
                );
            } else {
                ServerModel serverModel = (ServerModel) userModel;

                ArrayList<MenuItem> list = new ArrayList<>();
                list.add(new MenuItem("Info", GoogleMaterialDesignIcons.DNS, new ServerProfile(serverModel)));
                list.add(new MenuItem("Members", GoogleMaterialDesignIcons.PERSON, new MembersOfServer(serverModel)));
                if (serverModel.owner.equals(MyInfo.getInstance().getId()))
                    list.add(new MenuItem("Design", GoogleMaterialDesignIcons.EDIT, new DesignServer(serverModel)));

                if (serverModel.channels == null)
                    serverModel.channels = new ArrayList<>();

                if (serverModel.channels.size() > 0)
                    list.add(null);

                for (ServerModel.ServerChannel channel : serverModel.channels) {
                    list.add(new MenuItem(channel.name,
                            channel.type == 0 ? GoogleMaterialDesignIcons.HEADSET
                                    : GoogleMaterialDesignIcons.GROUP, new Chat(serverModel, channel)));
                }

                initItems(selectedIndex, to, list.toArray(new MenuItem[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class MenuItem {
        final String title;
        final GoogleMaterialDesignIcons icon;
        final ContentCreator creator;

        private MenuItem(String title, GoogleMaterialDesignIcons icon, ContentCreator creator) {
            this.title = title;
            this.icon = icon;
            this.creator = creator;
        }
    }

    private void initItems(int from, int to, MenuItem... menu) throws IOException {
        VBox box = new VBox(2);

        int i = 0;

        for (MenuItem item : menu) {
            if (item == null) {
                box.getChildren().add(new Rectangle(0, 10));
                Rectangle divider = new Rectangle(30, 2);
                divider.setStyle("-fx-fill: #20222580;");
                divider.setTranslateX(77);
                box.getChildren().add(divider);
                box.getChildren().add(new Rectangle(0, 10));
                continue;
            }
            if (i == 0)
                box.getChildren().add(new Rectangle(0, 23));
            i++;
            AnchorPane parent = FXMLLoader.load(Main.class.getResource("fxml/chat_item2.fxml"));
            Label lbl = (Label) parent.getChildren().get(2);
            Label icon = (Label) parent.getChildren().get(1);
            lbl.setText(item.title);
            Node selector = parent.getChildren().get(0);

            IconNode iconNode = new IconNode(item.icon);
            iconNode.setFill(Color.WHITE);
            iconNode.setIconSize(18);
            icon.setGraphic(iconNode);

            final int finalI = i;
            if (i == selectedItemIndex) {
                selectedItemIndex = i;
                selectedItem = parent;
                iconNode.setFill(Color.WHITE);
                lbl.setTextFill(Color.WHITE);

                if (from != to && content.getChildren().size() == 1) {
                    Node node = content.getChildren().get(0);
                    Animations.translateY(0,
                            from > to ? content.getHeight() : -content.getHeight(),
                            300, node, actionEvent -> content.getChildren().remove(node));
                } else {
                    content.getChildren().clear();
                }
                if (item.creator != null) {
                    Node node2 = item.creator.create();
                    if (from != to)
                        node2.setTranslateY(from > to ? -content.getHeight() : content.getHeight());
                    content.getChildren().add(0, node2);
                    if (from != to)
                        Animations.translateY(node2.getTranslateY(), 0, 300, node2);
                }
            } else {
                selector.setVisible(false);
                iconNode.setFill(Color.GRAY);
                lbl.setTextFill(Color.GRAY);
            }

            EventHandler<MouseEvent> handler = mouseEvent -> {
                if (selectedItem == parent)
                    return;
                selector.setVisible(mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED);
                Paint color = selector.isVisible() ? Color.WHITE : Color.GRAY;
                iconNode.setFill(color);
                lbl.setTextFill(color);
            };

            parent.setOnMouseEntered(handler);
            parent.setOnMouseExited(handler);
            parent.setOnMouseClicked(mouseEvent -> {
                if (selectedItem == parent)
                    return;

                selector.setVisible(true);
                selectedItem.getChildren().get(0).setVisible(false);
                ((IconNode) ((Label) selectedItem.getChildren().get(1)).getGraphic())
                        .setFill(Color.GRAY);
                ((Label) selectedItem.getChildren().get(2)).setTextFill(Color.GRAY);
                selectedItem = parent;

                if (content.getChildren().size() == 1) {
                    Node node = content.getChildren().get(0);
                    Animations.translateY(0,
                            selectedItemIndex > finalI ? content.getHeight() : -content.getHeight(),
                            300, node, actionEvent -> content.getChildren().remove(node));
                } else {
                    content.getChildren().clear();
                }
                if (item.creator != null) {
                    try {
                        Node node2 = item.creator.create();
                        node2.setTranslateY(selectedItemIndex > finalI ? -content.getHeight() : content.getHeight());
                        content.getChildren().add(0, node2);
                        Animations.translateY(node2.getTranslateY(), 0, 300, node2);
                    } catch (IOException ignore) {
                    }
                }
                selectedItemIndex = finalI;
            });

            box.getChildren().add(parent);
        }

        if (from != to && item_content.getChildren().size() == 1) {
            Node node = item_content.getChildren().get(0);
            Animations.translateY(0,
                    from > to ? items.getHeight() : -items.getHeight(),
                    300, node, actionEvent -> item_content.getChildren().remove(node));
        } else {
            item_content.getChildren().clear();
        }
        if (from != to)
            box.setTranslateY(from > to ? -items.getHeight() : items.getHeight());
        item_content.getChildren().add(0, box);
        if (box.getTranslateY() != 0)
            Animations.translateY(box.getTranslateY(), 0, 300, box);

        item_content.prefHeightProperty().bind(box.heightProperty());
    }
}
