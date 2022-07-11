package com.aghajari.ui.contents;

import com.aghajari.Main;
import com.aghajari.api.ApiService;
import com.aghajari.models.SearchUsers;
import com.aghajari.shared.models.UserModel;
import com.aghajari.ui.dialogs.OpenChatDialog;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import java.io.IOException;


public class Search extends AbstractContentCreator<Pane> {

    @Override
    String getFXMLName() {
        return "content_search";
    }

    @Override
    void create(Pane node) {
        ((ImageView) Utils.findViewById(node, "img")).setImage(
                new Image(Main.class.getResource("images/friends.png").toExternalForm(), true)
        );

        Node box = Utils.findViewById(node, "box");
        Node loading = Utils.findViewById(node, "loading");
        MFXScrollPane scroll = Utils.findViewById(node, "scroll");

        TextField edt = Utils.findViewById(node, "edt");
        Node noUser = Utils.findViewById(node, "noUser");


        edt.textProperty().addListener(new ChangeListener<>() {

            Timeline timeline = null;

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (timeline != null)
                    timeline.stop();

                if (edt.getText().length() >= 4) {
                    loading.setVisible(true);
                    box.setVisible(false);
                    scroll.setContent(null);
                    noUser.setVisible(false);

                    timeline = new Timeline(new KeyFrame(Duration.millis(600), e -> {
                        ApiService.search(edt.getText(), new ApiService.Callback() {
                            @Override
                            public void onResponse(String body) {
                                System.out.println(body);
                                SearchUsers users = SearchUsers.parse(body);

                                if (users.users == null || users.users.isEmpty())
                                    noUser.setVisible(true);
                                else
                                    initUsers(users, scroll);

                                loading.setVisible(false);
                                box.setVisible(false);
                            }

                            @Override
                            public void onError(boolean network, int code) {
                                System.out.println(network);
                                //Toast.makeText("Oops, something went wrong!");
                                loading.setVisible(false);
                                box.setVisible(true);
                            }

                            @Override
                            public void onError2(boolean network, int code, String res) {
                                System.out.println(res);
                                //BaseApiModel.toastError(res);
                                loading.setVisible(false);
                                box.setVisible(true);
                            }
                        });
                    }));
                    timeline.play();
                }

            }
        });
    }

    private void initUsers(SearchUsers users, MFXScrollPane scroll) {
        VBox box = new VBox(12);
        try {
            box.getChildren().add(new Rectangle(0, 24));
            int len = users.users.size();
            for (int i = 0; i <len; i++) {
                MyFriends.initUser(box, users.users.get(i), 0, null, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        scroll.setContent(box);
    }

    @Override
    public boolean isEditablePage() {
        return true;
    }
}
