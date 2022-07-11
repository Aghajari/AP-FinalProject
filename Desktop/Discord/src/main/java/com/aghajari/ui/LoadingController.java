package com.aghajari.ui;


import com.aghajari.Main;
import com.aghajari.MainUI;
import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.models.MyInfo;
import com.aghajari.shared.ChatsList;
import com.aghajari.shared.models.UserModel;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.SocketModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticStorage;
import com.aghajari.util.Animations;
import com.aghajari.util.Toast;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

public class LoadingController {

    @FXML
    public AnchorPane root;

    public void initialize() {
        ApiService.loadUser();
        System.out.println(MyInfo.getInstance().getId());
        root.setVisible(false);

        Interpolator interpolator = Interpolator.EASE_BOTH;
        Timeline show = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            root.setVisible(true);
            root.setOpacity(0);
            root.setScaleX(0);
            root.setScaleY(0);
            Timeline show2 = new Timeline(new KeyFrame(
                    Duration.seconds(0.7),
                    new KeyValue(root.opacityProperty(), 1, interpolator),
                    new KeyValue(root.scaleXProperty(), 1, interpolator),
                    new KeyValue(root.scaleYProperty(), 1, interpolator)
            ));
            show2.play();
        }));
        show.play();

        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() throws IOException {
        new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            if (SocketApi.getInstance().isConnected()) {
                System.out.println("SOCKET CONNECTED");
                if (ApiService.isLoggedIn()) {
                    EasyApi.auth(model -> {
                        if (model.get()) {
                            System.out.println("AUTH DONE");
                            EasyApi.getChats(chats -> Platform.runLater(this::openHome));
                        } else {
                            openLogin();
                        }
                    });
                } else {
                    openLogin();
                }
            } else {
                Toast.makeText("Server isn't running :(");
            }
        })).play();
    }

    private void openLogin() {
        if (StaticStorage.spaceImage == null || StaticStorage.spaceImage.isError())
            StaticStorage.spaceImage = new Image(Main.class.getResource("space.gif")
                    .toExternalForm(), true);

        if (StaticStorage.spaceImage.getProgress() >= 1) {
            try {
                done(FXMLLoader.load(Main.class.getResource("fxml/login.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            StaticStorage.spaceImage.progressProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    if (StaticStorage.spaceImage.getProgress() >= 1) {
                        StaticStorage.spaceImage.progressProperty().removeListener(this);
                        try {
                            done(FXMLLoader.load(Main.class.getResource("fxml/login.fxml")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void openHome() {
        try {
            done(FXMLLoader.load(Main.class.getResource("fxml/home.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void done(Node root2) {
        Platform.runLater(() -> {
            root2.setTranslateX(root.getWidth());
            MainUI.mainRoot.getChildren().add(root2);
            Animations.translateX(0, -root.getWidth(), 500, root,
                    actionEvent -> MainUI.mainRoot.getChildren().remove(root));
            Animations.translateX(root.getWidth(), 0, 500, root2);
        });
    }

}
