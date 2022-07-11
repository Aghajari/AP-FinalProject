package com.aghajari.call;

import com.aghajari.Main;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.models.UserModel;
import com.aghajari.store.EasyApi;
import com.aghajari.util.Animations;
import com.aghajari.util.MoveApplication;
import com.aghajari.util.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.util.Duration;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconNode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CallUI {

    private static final int TIME_OUT = 15; //15sec

    private static boolean isInCall = false;
    private static Stage stage;
    private static UserModel user;
    private static Runnable hide;
    private static long duration;
    private static int timeOut;
    private static int autoClose;
    private static boolean justRejected = false;
    private static boolean checkForTimeOut = false;
    private static boolean started = false;
    private static MediaPlayer mediaPlayer = null;

    private static SoundSender soundSender = null;
    private static SoundReceiver soundReceiver = null;

    public static void start(UserModel user, boolean callFromMe) {
        forceStop();
        Pane root;
        try {
            root = FXMLLoader.load(Main.class.getResource("fxml/call.fxml"));
        } catch (IOException e) {
            return;
        }
        started = justRejected = false;
        isInCall = checkForTimeOut = true;
        autoClose = timeOut = 0;

        Label status = Utils.findViewById(root, "status");
        if (callFromMe)
            EasyApi.requestCall(user);

        EasyApi.registerCallListener(answer -> {
            if ((!isInCall || CallUI.hide == null)
                    && answer.eventType == SocketEvents.ANSWER_CALL) {
                EasyApi.rejectCall(user);
                stopMedia();
            } else if (isInCall) {
                System.out.println(answer.eventType);
                if (!user.getId().equals(answer.get())
                        && answer.eventType == SocketEvents.ANSWER_CALL) {
                    EasyApi.rejectCall(user);
                } else if (user.getId().equals(answer.get())) {
                    if (answer.eventType == SocketEvents.REJECT_CALL) {
                        justRejected = true;
                        checkForTimeOut = false;
                        if (started || (timeOut + 1 < TIME_OUT)) {
                            status.setText("Rejected!");
                        }
                        if (callFromMe || started) {
                            if (!justRejected)
                                autoClose = 0;

                            startAutoClose(user);
                        } else
                            close(user);
                    } else if (answer.eventType == SocketEvents.ALREADY_IN_CALL) {
                        checkForTimeOut = false;
                        status.setText("is in call");
                        autoClose = 0;
                        startAutoClose(user);
                    } else {
                        checkForTimeOut = false;
                        startTimer(user, status);
                    }
                }
            }
        });
        root.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

        Label lbl = Utils.findViewById(root, "btn");
        IconNode iconNode = new IconNode(GoogleMaterialDesignIcons.CALL_END);
        iconNode.setFill(Color.WHITE);
        iconNode.setIconSize(30);
        lbl.setGraphic(iconNode);

        Label lbl2 = Utils.findViewById(root, "btn_answer");
        IconNode iconNodeAnswer = new IconNode(GoogleMaterialDesignIcons.CALL);
        iconNodeAnswer.setFill(Color.WHITE);
        iconNodeAnswer.setIconSize(30);
        lbl2.setGraphic(iconNodeAnswer);

        ((Label) Utils.findViewById(root, "name")).setText(
                MyInfo.getUsername(user, "Discord"));

        Node node = Utils.findViewById(root, "ripple");
        node.setClip(new Circle(30, 30, 30));

        Node answer = Utils.findViewById(root, "ripple_answer");
        answer.setClip(new Circle(30, 30, 30));

        lbl2.setVisible(!callFromMe);
        answer.setVisible(!callFromMe);

        if (!callFromMe) {
            lbl.setTranslateX(-100);
            node.setTranslateX(-100);
        }

        answer.setOnMouseClicked(mouseEvent -> {
            if (CallUI.hide != null) {
                checkForTimeOut = false;
                EasyApi.answerCall(user);
                Animations.translateX(-100, 0, 300, node);
                Animations.translateX(-100, 0, 300, lbl);
                Animations.hideWithAlphaAndScale(300, answer);
                Animations.hideWithAlphaAndScale(300, lbl2);
                answer.setDisable(true);

                startTimer(user, status);
            }
        });

        Label hide = Utils.findViewById(root, "hide");
        IconNode iconNodeHide = new IconNode(GoogleMaterialDesignIcons.REMOVE);
        iconNodeHide.setFill(Color.WHITE);
        iconNodeHide.setIconSize(12);
        EventHandler<MouseEvent> event = mouseEvent ->
                hide.setGraphic(mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED
                        ? iconNodeHide : null);
        hide.setOnMouseEntered(event);
        hide.setOnMouseExited(event);

        Utils.loadAvatar(user, (ImageView) Utils.findViewById(root, "img"), false);
        Stage stage = new Stage();

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        //double x = Math.min(Toast.ownerStage.getX() + 1010, screenBounds.getWidth() - 340 - 15);
        double x = screenBounds.getWidth() - 340 - 15;
        stage.setX(screenBounds.getMaxX() + 10);
        //stage.setY(Math.min(Toast.ownerStage.getY() + 15, screenBounds.getHeight() - 630 - 15));
        stage.setY(screenBounds.getHeight() / 2 - 315);
        //stage.initOwner(Toast.ownerStage);
        stage.setResizable(false);
        stage.setWidth(340);
        stage.setHeight(630);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        MoveApplication.initMovement(root, stage);
        stage.show();

        Animations.stageX(x, 300, stage, false);
        // stage.getScene().getWindow().setOpacity(0);
        // Animations.windowAlpha(1, 300, stage.getScene().getWindow());

        CallUI.stage = stage;
        CallUI.user = user;

        CallUI.hide = () -> {
            forceStop();
            if (screenBounds.getWidth() / 2 <= (stage.getX() + 170)) {
                Animations.stageX(screenBounds.getMaxX() + 10, 300, stage, true);
            } else {
                Animations.stageX(-350, 300, stage, true);
            }
            Animations.windowAlpha(0.2, 300, stage.getScene().getWindow());
            isInCall = false;
            checkForTimeOut = false;
            stopMedia();
        };
        node.setOnMouseClicked(mouseEvent -> {
            if (CallUI.hide != null) {
                CallUI.hide.run();
                CallUI.hide = null;
            } else {
                stopMedia();
                try {
                    stage.close();
                    forceStop();
                } catch (Exception ignore) {
                }
            }
            EasyApi.rejectCall(user);
        });
        stage.setOnCloseRequest(windowEvent -> {
            EasyApi.rejectCall(user);
            forceStop();
            CallUI.hide = null;
            stopMedia();
            isInCall = false;
            checkForTimeOut = false;
        });
        hide.setOnMouseClicked(mouseEvent -> stage.setIconified(true));

        startTimeOut(user, status, callFromMe);
        if (!callFromMe)
            playMedia(1);
    }

    private static void startTimer(UserModel user, Label status) {
        soundSender = new SoundSender(user.getId());
        soundReceiver = new SoundReceiver();
        soundSender.start();
        soundReceiver.start();

        started = true;
        stopMedia();
        autoClose = 0;
        timeOut = 0;
        duration = 0;
        Timeline timeline =
                new Timeline(new KeyFrame(Duration.seconds(1)));

        status.setText("00:00");
        SimpleDateFormat df = new SimpleDateFormat("mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        timeline.setOnFinished(actionEvent -> {
            if (justRejected)
                return;

            duration++;
            if (autoClose == 0)
                status.setText(df.format(new Date(duration * 1000L)));
            if (isInCall && CallUI.user == user && hide != null && autoClose == 0)
                timeline.play();
        });
        timeline.play();
    }

    private static void startTimeOut(UserModel user, Label status, boolean callFromMe) {
        timeOut = 0;
        Timeline timeline =
                new Timeline(new KeyFrame(Duration.seconds(1)));

        timeline.setOnFinished(actionEvent -> {
            timeOut++;
            if (timeOut == TIME_OUT) {
                EasyApi.rejectCall(user);
                status.setText("Time out!");
                if (callFromMe)
                    startAutoClose(user);
                else
                    close(user);
                return;
            }
            if (isInCall && checkForTimeOut && hide != null)
                timeline.play();
        });
        timeline.play();
    }

    private static void startAutoClose(UserModel user) {
        if (autoClose == 1 || autoClose == 2)
            return;

        playMedia(2);
        autoClose = 1;
        Timeline timeline =
                new Timeline(new KeyFrame(Duration.seconds(1)));

        timeline.setOnFinished(actionEvent -> {
            autoClose++;
            if (autoClose == 4) {
                close(user);
                return;
            }
            if (isInCall && hide != null)
                timeline.play();
        });
        timeline.play();
    }


    public static boolean isInCall() {
        return isInCall;
    }

    public static Stage getStage() {
        return stage;
    }

    public static UserModel getUser() {
        return user;
    }

    public static void close(UserModel forUser) {
        stopMedia();
        if (user != null && isInCall && user.getId().equals(forUser.getId())
                && hide != null) {
            hide.run();
            hide = null;
        }
    }

    private static void playMedia(int id) {
        stopMedia();
        if (!isInCall)
            return;

        mediaPlayer = new MediaPlayer(
                new Media(CallUI.class.getResource(id + ".mp3").toExternalForm()));
        mediaPlayer.play();
    }

    private static void stopMedia() {
        try {
            if (mediaPlayer != null)
                mediaPlayer.stop();
        } catch (Exception ignore) {
        }
    }

    private static void forceStop() {
        try {
            if (soundReceiver != null) {
                try {
                    soundReceiver.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                soundSender.interrupt();
            }
        } catch (Exception ignore) {
        }
    }
}
