package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.api.SocketApi;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketModel;
import com.aghajari.shared.models.IsTypingModel;
import com.aghajari.shared.models.MessageModel;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.PermissionUtils;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Chat extends AbstractContentCreator<Pane> {

    private static final ArrayList<Image> reactions = new ArrayList<>();
    private static final ArrayList<Image> reactionsSmall = new ArrayList<>();
    private static final Image hiImage = new Image(
            Main.class.getResource("images/hi.gif").toExternalForm(), true);

    private static final Image savedMessages = new Image(
            Main.class.getResource("images/saved_messages.gif").toExternalForm(), true);

    static {
        for (int i = 1; i <= 18; i++) {
            reactions.add(new Image(Main.class.getResource("reactions/" + i + ".gif")
                    .toExternalForm(), true));
            reactionsSmall.add(new Image(Main.class.getResource("reactions/" + i + ".png")
                    .toExternalForm(), true));
        }
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy");
    private final LinkedHashMap<Integer, ChatViewModel> messages = new LinkedHashMap<>();
    private final String id;

    private final Font font = new Font("MyFont", 16);

    private final UserModel user;
    private final ServerModel server;
    private final ServerModel.ServerChannel channel;
    private final boolean reactionEnabled = true;

    public Chat(UserModel model) {
        user = model;
        id = model.getId();
        //reactionEnabled = true;
        this.server = null;
        this.channel = null;
    }

    public Chat(ServerModel model, ServerModel.ServerChannel channel) {
        id = model.serverId + "#" + channel.id;
        //reactionEnabled = false;
        this.user = null;
        this.server = model;
        this.channel = channel;
    }

    private static class ChatViewModel {
        public MessageModel model;
        public Pane pane;
        public HBox reactions;

        private ChatViewModel(MessageModel model) {
            this.model = model;
        }
    }

    @Override
    String getFXMLName() {
        return "chat";
    }

    @Override
    void create(Pane node) {
        TextArea edt = Utils.findViewById(node, "edt");
        AnchorPane parent = (AnchorPane) edt.getParent();
        MFXScrollPane scroll = Utils.findViewById(node, "scroll");
        scroll.prefHeightProperty().bind(parent.layoutYProperty().subtract(4));
        scroll.minHeightProperty().bind(parent.layoutYProperty().subtract(4));

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        ImageView imageView = Utils.findViewById(node, "send");
        Blend blush = new Blend(
                BlendMode.SRC_ATOP,
                monochrome,
                new ColorInput(0, 0, 200, 200, Color.WHITE)
        );
        imageView.setEffect(blush);

        edt.textProperty().addListener((observableValue, s, t1) -> {
            double h = Math.max(32, Utils.getTextHeight(edt.getText(), edt.getFont(), edt.getPrefWidth()));
            h = Math.min(h, 160);
            edt.setPrefHeight(h);

            new Timeline(new KeyFrame(Duration.millis(100),
                    new KeyValue(parent.prefHeightProperty(), h + 24),
                    new KeyValue(parent.layoutYProperty(), 630 - 48 - h))).play();
        });

        if (channel == null || channel.type != 0) {
            edt.textProperty().addListener(new ChangeListener<String>() {
                long lastTime = 0;
                final Timeline timeline = new Timeline(new KeyFrame(
                        new Duration(500), e -> {
                    if (System.currentTimeMillis() - lastTime > 1000) {
                        EasyApi.requestIsTyping(isTyping = false, id,
                                server != null ? server.serverId : null);
                    } else {
                        start();
                    }
                }
                ));

                boolean isTyping = false;

                public void start() {
                    timeline.playFromStart();
                }

                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    lastTime = System.currentTimeMillis();
                    if (!isTyping)
                        EasyApi.requestIsTyping(isTyping = true, id,
                                server != null ? server.serverId : null);
                    start();
                }
            });
        }
        load();
    }

    @Override
    public Node create() throws IOException {
        if (tmp != null)
            load();
        return super.create();
    }

    @Override
    public boolean isEditablePage() {
        return true;
    }

    @Override
    public boolean isChatPage() {
        return true;
    }

    public void load() {
        StaticListeners.messageUpdater = this::notifyNewMessage;
        StaticListeners.isTypingUpdater = this::notifyTyping;
        StaticListeners.updateBlockUnblock = this::checkForBlockingStatus;

        if (server != null && !server.owner.equals(MyInfo.getInstance().getId()))
            Utils.findViewById(tmp, "access")
                    .setVisible(!PermissionUtils.canSendMessage(server, channel));

        checkForBlockingStatus(user);

        EasyApi.getMessages(id, server != null, model -> {
            List<MessageModel> list = model.get();
            messages.clear();

            for (MessageModel m : list)
                messages.put(m.index, new ChatViewModel(m));

            System.out.println("Messages Fetched: " + messages.size());
            createMessages();
        });
    }

    public void checkForBlockingStatus(UserModel user) {
        if (server == null && this.id.equals(user.getId())) {
            EasyApi.getBlockStatus(id, model -> {
                int status = model.get();
                if (status != 0) {
                    Utils.findViewById(tmp, "access").setVisible(true);

                    Label lbl = Utils.findViewById(tmp, "access_lbl");
                    if (status == 1)
                        lbl.setText("You have blocked " + user.getName() + "!");
                    else
                        lbl.setText("You don't have access!");
                } else {
                    Utils.findViewById(tmp, "access").setVisible(false);
                }
            });
        }
    }

    void notifyTyping(IsTypingModel.IsTypingResponse model) {
        if (!id.equals(model.id))
            return;
        if (channel != null && channel.type == 0)
            return;

        System.out.println(model.typings);
        Label lbl = Utils.findViewById(tmp, "typing");

        if (model.typings == null || model.typings.isEmpty() ||
                (model.typings.size() == 1 && model.typings.keySet().
                        iterator().next().equals(MyInfo.getInstance().getId()))) {
            if (lbl.isVisible()) {
                new Timeline(new KeyFrame(new Duration(100),
                        e -> lbl.setVisible(false),
                        new KeyValue(lbl.scaleXProperty(), 0),
                        new KeyValue(lbl.scaleYProperty(), 0)
                )).play();
            }
        } else {
            StringBuilder names = new StringBuilder();
            int count = 0;
            for (Map.Entry<String, String> entry : model.typings.entrySet()) {
                if (entry.getKey().equals(MyInfo.getInstance().getId()))
                    continue;
                names.append(entry.getValue()).append(", ");
                count++;
                if (count == 3)
                    break;
            }
            names = new StringBuilder(names.substring(0, names.length() - 2));
            if (count == 3 && model.typings.size() > 3) {
                names.append(" and ").append(model.typings.size() - 3).append(" others");
            }
            if (count > 1)
                names.append(" are typing...");
            else
                names.append(" is typing...");

            lbl.setText(names.toString());

            if (!lbl.isVisible()) {
                lbl.setScaleX(0);
                lbl.setScaleY(0);
                lbl.setVisible(true);
                new Timeline(new KeyFrame(new Duration(100),
                        new KeyValue(lbl.scaleXProperty(), 1),
                        new KeyValue(lbl.scaleYProperty(), 1)
                )).play();
            }
        }
    }

    boolean notifyNewMessage(MessageModel model) {
        if ((model.fromId.equals(id) && model.toId.equals(MyInfo.getInstance().getId()))
                || (model.fromId.equals(MyInfo.getInstance().getId()) && model.toId.equals(id))
                || (model.toId.contains("#") && model.toId.equals(id))) {

            if (messages.containsKey(model.index)) {
                ChatViewModel vm = messages.get(model.index);
                vm.model = model;
                updateReactions(vm.pane, vm.model, vm.reactions);
            } else {
                checkForScrollToEnd(Utils.findViewById(tmp, "scroll"));
                ChatViewModel vm = new ChatViewModel(model);
                messages.put(model.index, vm);
                addMessage(vm, true);
            }
            return HomeController.isOnChatPage;
        }
        return false;
    }

    void checkForScrollToEnd(MFXScrollPane scroll) {
        if (scroll.getVvalue() >= 0.95) {
            scroll.vvalueProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    scroll.vvalueProperty().removeListener(this);
                    scroll.setVvalue(1.0);
                }
            });
        }
    }

    void createMessages() {
        oldPane = null;
        year = day = 0;
        type = "null";

        MFXScrollPane scroll = Utils.findViewById(tmp, "scroll");
        BorderPane border = new BorderPane();
        border.minHeightProperty().bind(scroll.prefHeightProperty().subtract(24));
        border.minWidthProperty().bind(scroll.minWidthProperty().subtract(40));
        box = new VBox(1);
        box.minWidthProperty().bind(border.minWidthProperty());
        box.getChildren().add(new Rectangle(0, 24));
        synchronized (messages) {
            for (ChatViewModel vm : messages.values())
                addMessage(vm, false);
        }
        box.setPadding(new Insets(0, 0, 24, 0));
        border.setBottom(box);
        scroll.setContent(border);
        scroll.setVvalue(1);

        TextArea edt = Utils.findViewById(tmp, "edt");

        Runnable send = () -> {
            if (edt.getText().trim().length() == 0)
                return;

            sendMessage(edt.getText().trim(), scroll);
            edt.setText("");
        };

        edt.setOnKeyPressed(keyEvent -> {
            if (keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.ENTER) {
                send.run();
            }
        });
        Utils.findViewById(tmp, "send_pnl").setOnMouseClicked(mouseEvent -> send.run());

        if (messages.isEmpty()) {
            Utils.findViewById(tmp, "hiPnl").setVisible(true);
            ImageView img = Utils.findViewById(tmp, "hi");
            MFXButton btn = Utils.findViewById(tmp, "sayHi");
            img.setImage(!id.equals(MyInfo.getInstance().getId()) ? hiImage : savedMessages);
            btn.setOnAction(actionEvent -> sendMessage("Hello", scroll));
            btn.setVisible(!Utils.findViewById(tmp, "access").isVisible());

            box.getChildren().addListener(new ListChangeListener<>() {
                @Override
                public void onChanged(Change<? extends Node> change) {
                    Utils.findViewById(tmp, "hiPnl").setVisible(false);
                    box.getChildren().removeListener(this);
                    img.setImage(null);
                }
            });
        }
    }

    private void sendMessage(String msg, MFXScrollPane scroll) {
        MessageModel messageModel = new MessageModel(msg,
                System.currentTimeMillis(),
                MyInfo.getInstance().getId(),
                id);

        EasyApi.sendMessage(messageModel, done -> {
            MessageModel res = done.get();
            if (res == null)
                return;

            checkForScrollToEnd(scroll);
            ChatViewModel vm = new ChatViewModel(res);
            messages.put(res.index, vm);
            addMessage(vm, true);
        });
    }

    private VBox box;
    private Pane oldPane;
    private String type;
    private int year, day;

    private void addMessage(ChatViewModel vm, boolean animate) {
        MessageModel model = vm.model;
        boolean fromMe = model.fromId.equals(MyInfo.getInstance().getId());
        String lastType = type;
        type = fromMe ? "me" : model.fromId;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(model.time);
        int y = calendar.get(Calendar.YEAR);
        int d = calendar.get(Calendar.DAY_OF_YEAR);
        if (year != y || day != d) {
            if (box.getChildren().size() > 0)
                box.getChildren().add(new Rectangle(0, 10));

            lastType = "null";
            year = y;
            day = d;

            AnchorPane datePane = new AnchorPane();
            datePane.setPrefHeight(34);
            datePane.setMinWidth(473);
            datePane.setTranslateX(56);
            Rectangle rectangle = new Rectangle(datePane.getMinWidth(), 1);
            rectangle.setFill(Color.GRAY);
            datePane.getChildren().add(rectangle);
            rectangle.setLayoutY(11);
            box.getChildren().add(datePane);

            Label date = new Label();
            date.setText(dateFormat.format(new Date(model.time)));
            date.setTextFill(Color.GRAY);
            date.setStyle("-fx-font-family: MyFont; -fx-font-size: 14; -fx-background-color: #37393E;");
            date.setAlignment(Pos.CENTER);
            date.setMinWidth(Utils.getTextWidth(date.getText(), date.getFont(), 200) + 24);
            date.setMinHeight(Utils.getTextHeight(date.getText(), date.getFont(), 200));
            date.setLayoutY(11 - date.getMinHeight() / 2);
            date.setLayoutX(datePane.getMinWidth() / 2 - date.getMinWidth() / 2);
            datePane.getChildren().add(date);
        }

        boolean same = lastType.equals(type);

        if (box.getChildren().size() > 1 && !same) {
            box.getChildren().add(new Rectangle(0, 10));
        }

        if (oldPane != null && same && !fromMe)
            oldPane.setStyle("-fx-background-color: #2F3136; -fx-background-radius: 0 12 12 0;");

        AnchorPane pane = new AnchorPane();
        vm.pane = pane;

        if (fromMe) {
            pane.setStyle("-fx-background-color: #2F3136; -fx-background-radius: 12 " +
                    (!same ? 12 : 0) + " 0 12;");
        } else {
            pane.setStyle("-fx-background-color: #2F3136; -fx-background-radius: 0 12 12 12;");
        }
        oldPane = !fromMe ? pane : null;

        Label label = new Label();
        label.setFont(font);
        label.setTextFill(Color.WHITE);
        label.setWrapText(true);

        label.setText(model.text);
        pane.setMaxWidth(Utils.getTextWidth(model.text, label.getFont(), 350) + 24);
        pane.setMinWidth(pane.getMaxWidth());
        label.setPrefWidth(pane.getMaxWidth() - 24);

        double h = Utils.getTextHeight(model.text, label.getFont(), label.getPrefWidth());
        pane.setPrefHeight(h + 24);
        label.setPrefHeight(h);
        label.setLayoutX(12);
        label.setLayoutY(12);

        pane.setUserData(new Pair<>(pane.getMaxWidth(), pane.getPrefHeight()));
        HBox reactionsBox = new HBox(1);
        vm.reactions = reactionsBox;
        pane.getChildren().add(reactionsBox);
        updateReactions(pane, model, reactionsBox);
        setReactions(vm);

        pane.setTranslateX(!fromMe ? 48 + 8 : 585 - 48 - 8 - pane.getMaxWidth());
        pane.getChildren().add(label);

        if (!same) {
            AnchorPane pane2 = new AnchorPane();
            pane.setLayoutY(17);
            pane2.getChildren().add(pane);

            if (!fromMe) {
                ImageView img = new ImageView();
                img.setClip(new Circle(20, 20, 20));
                img.setFitWidth(40);
                img.setFitHeight(40);

                if (model.getUser() == null)
                    img.setImage(Utils.textToImage(""));
                else
                    Utils.loadAvatar(model.getUser(), img);

                pane.setLayoutX(48);
                pane.setTranslateX(0);
                pane2.getChildren().add(img);
                pane2.setTranslateX(8);
            } else {
                pane.setLayoutX(pane.getTranslateX());
                pane.setTranslateX(0);
            }

            Label hint = new Label();
            hint.setTextFill(Color.GRAY);
            hint.setStyle("-fx-font-family: MyFont; -fx-font-size: " + (server != null ? 12 : 10));
            hint.setLayoutY(-2);
            hint.setAlignment(fromMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            hint.setPrefHeight(17);
            hint.setText(!fromMe && server != null && model.getUser() != null ? model.getUser().getName() :
                    timeFormat.format(new Date(model.time)));

            if (fromMe) {
                hint.setLayoutX((pane.getLayoutX() + pane.getMaxWidth())
                        - Utils.getTextWidth(hint.getText(), hint.getFont(), 100) + (server == null ? 8 : 0));
            } else {
                hint.setLayoutX(48);
            }
            pane2.getChildren().add(hint);
            pane = pane2;
        }

        Label time = new Label();
        time.setStyle("-fx-font-family: MyFont; -fx-font-size: 10");
        time.setTextFill(Color.GRAY);
        time.setText(timeFormat.format(new Date(model.time)));
        vm.pane.getChildren().add(time);
        time.translateYProperty().bind(vm.pane.prefHeightProperty()
                .divide(2).subtract(5));

        if (fromMe) {
            time.setTranslateX(-Utils.getTextWidth(time.getText(), time.getFont(), 100));
        } else {
            time.translateXProperty().bind(vm.pane.minWidthProperty().add(8));
        }
        time.setVisible(false);
        EventHandler<MouseEvent> event = e ->
                time.setVisible(e.getEventType() == MouseEvent.MOUSE_ENTERED);
        vm.pane.setOnMouseEntered(event);
        vm.pane.setOnMouseExited(event);

        if (animate) {
            pane.setOpacity(0);
            pane.setTranslateY(100);
            new Timeline(new KeyFrame(
                    Duration.millis(250),
                    new KeyValue(pane.opacityProperty(), 1),
                    new KeyValue(pane.translateYProperty(), 0)
            )).play();
        }
        box.getChildren().add(pane);
    }

    void updateReactions(Pane pane, MessageModel model, HBox reactionsBox) {
        if (!reactionEnabled)
            return;

        reactionsBox.getChildren().clear();
        //noinspection unchecked
        Pair<Double, Double> pair = (Pair<Double, Double>) pane.getUserData();

        if (model.reactions == null || model.reactions.size() == 0) {
            reactionsBox.setLayoutX(0);
            reactionsBox.setLayoutY(0);
            pane.setMaxWidth(pair.getKey());
            pane.setMinWidth(pair.getKey());
            pane.setPrefHeight(pair.getValue());
            if (model.fromId.equals(MyInfo.getInstance().getId()))
                if (pane.getTranslateX() != 0)
                    pane.setTranslateX(585 - 48 - 8 - pane.getMaxWidth());
                else
                    pane.setLayoutX(585 - 48 - 8 - pane.getMaxWidth());
            return;
        }

        for (Integer reaction : model.reactions.values()) {
            if (reactionsSmall.size() < reaction || reaction <= 0)
                continue;

            ImageView img = new ImageView(reactionsSmall.get(reaction - 1));
            img.setFitWidth(20);
            img.setFitHeight(20);
            reactionsBox.getChildren().add(img);
        }

        double top = pair.getValue();
        reactionsBox.setLayoutX(4);
        reactionsBox.setLayoutY(top - 8);
        pane.setPrefHeight(top + 20);

        double w = Math.max(model.reactions.size() * 22 + 8, pair.getKey());
        if (w != pane.getMaxWidth()) {
            pane.setMinWidth(w);
            pane.setMaxWidth(w);
            if (model.fromId.equals(MyInfo.getInstance().getId()))
                if (pane.getTranslateX() != 0)
                    pane.setTranslateX(585 - 48 - 8 - pane.getMaxWidth());
                else
                    pane.setLayoutX(585 - 48 - 8 - pane.getMaxWidth());
        }
    }

    void setReactions(ChatViewModel vm) {
        if (!reactionEnabled)
            return;

        vm.pane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                AnchorPane pane = new AnchorPane();
                pane.prefWidthProperty().bind(tmp.prefWidthProperty());
                pane.prefHeightProperty().bind(tmp.prefHeightProperty());

                double x = mouseEvent.getSceneX() - 415;
                double y = mouseEvent.getSceneY();
                tmp.getChildren().add(pane);

                MFXScrollPane scrollPane = new MFXScrollPane();
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setPrefHeight(56);
                scrollPane.setPrefWidth(200);
                scrollPane.setLayoutX(Math.min(pane.getPrefWidth() - 220, x - 150));
                scrollPane.setLayoutY(Math.max(0, Math.min(pane.getPrefHeight() - 200, y - 23)));
                pane.getChildren().add(scrollPane);

                HBox hBox = new HBox(2);
                hBox.setTranslateY(6);
                hBox.setAlignment(Pos.CENTER);

                Rectangle r1 = new Rectangle(12, 40);
                r1.setFill(Color.TRANSPARENT);
                Rectangle r2 = new Rectangle(12, 40);
                r2.setFill(Color.TRANSPARENT);

                Runnable remove = () -> new Timeline(
                        new KeyFrame(
                                new Duration(100),
                                e -> tmp.getChildren().remove(pane),
                                new KeyValue(scrollPane.opacityProperty(), 0),
                                new KeyValue(scrollPane.scaleXProperty(), 0),
                                new KeyValue(scrollPane.scaleYProperty(), 0)
                        )).play();

                hBox.getChildren().add(r1);
                for (int i = 0; i < reactions.size(); i++) {
                    ImageView img = new ImageView(reactions.get(i));
                    img.setFitWidth(40);
                    img.setFitHeight(40);
                    hBox.getChildren().add(img);

                    int finalI = i + 1;
                    img.setOnMouseClicked(event -> {
                        remove.run();

                        MessageModel model = vm.model;
                        if (model.reactions == null)
                            model.reactions = new HashMap<>();

                        Integer old = model.reactions.get(MyInfo.getInstance().getId());
                        if (old != null && old == finalI)
                            model.reactions.remove(MyInfo.getInstance().getId());
                        else
                            model.reactions.put(MyInfo.getInstance().getId(), finalI);

                        updateReactions(vm.pane, model, vm.reactions);
                        EasyApi.reaction(model.index, model.reactions
                                .containsKey(MyInfo.getInstance().getId()) ? finalI : 0);
                        event.consume();
                    });
                }
                hBox.getChildren().add(r2);

                scrollPane.setStyle("-fx-background-color: #202225; -fx-background-radius: 100;");
                Rectangle rectangle = new Rectangle(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());
                rectangle.setArcHeight(54);
                rectangle.setArcWidth(54);
                scrollPane.setClip(rectangle);
                scrollPane.setContent(hBox);

                scrollPane.setOnMouseClicked(Event::consume);
                pane.setOnMouseClicked(event -> remove.run());

                scrollPane.setOpacity(0);
                scrollPane.setScaleX(0);
                scrollPane.setScaleY(0);
                new Timeline(
                        new KeyFrame(
                                new Duration(100),
                                new KeyValue(scrollPane.opacityProperty(), 1),
                                new KeyValue(scrollPane.scaleXProperty(), 1),
                                new KeyValue(scrollPane.scaleYProperty(), 1)
                        )).play();
            }
        });
    }
}
