package com.aghajari.ui.contents;

import com.aghajari.api.ApiService;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.UploadAvatarModel;
import com.aghajari.shared.models.RequestToJoinServer;
import com.aghajari.shared.models.ServerModel;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticStorage;
import com.aghajari.ui.HomeController;
import com.aghajari.ui.StaticListeners;
import com.aghajari.util.ImagePicker;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;

public class Servers extends AbstractContentCreator<Pane> {

    @Override
    String getFXMLName() {
        return "content_servers";
    }

    @Override
    void create(Pane node) {
        Node join = Utils.findViewById(tmp, "join");
        TextField tv = Utils.findViewById(node, "code");
        tv.textProperty().addListener((observableValue, s, t1)
                -> animate(join, !tv.getText().trim().isEmpty()));

        Node create = Utils.findViewById(tmp, "create");
        TextField tv2 = Utils.findViewById(node, "name");
        tv2.textProperty().addListener((observableValue, s, t1)
                -> animate(create, !tv2.getText().trim().isEmpty()));

        Label lbl = Utils.findViewById(node, "btn_join");
        Node r = Utils.findViewById(node, "ripple_join");
        MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.CHECK);
        r.setOnMouseClicked(mouseEvent -> {
            String code = tv.getText().trim();
            if (code.length() <= 4) {
                Toast.makeText("Invalid code!");
                return;
            }
            EasyApi.joinServer(code, model -> {
                RequestToJoinServer res = model.get();
                if (res.resultCode == 404) {
                    Toast.makeText("Invalid code!");
                } else if (res.resultCode == 300) {
                    Toast.makeText("You are already a member of \n'" + res.serverModel.name + "' community!");
                } else if (res.resultCode == 200) {
                    StaticStorage.chats.servers.add(res.serverModel);
                    Toast.makeText("Welcome to " + res.serverModel.name);
                    HomeController.requestToOpenServer = true;
                    if (StaticListeners.updateOpenedChatsListener != null)
                        StaticListeners.updateOpenedChatsListener.update();
                }
            });
        });

        ImageView img = Utils.findViewById(node, "img");
        img.imageProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Image> observableValue, Image image, Image t1) {
                img.imageProperty().removeListener(this);
                img.setClip(new Circle(50, 50, 50));
            }
        });
        ImagePicker picker = new ImagePicker(img, node);
        MFXProgressSpinner loading = Utils.findViewById(node, "loading");

        lbl = Utils.findViewById(node, "btn_create");
        r = Utils.findViewById(node, "ripple_create");
        MutualFriends.init(lbl, r, GoogleMaterialDesignIcons.CHECK);
        r.setOnMouseClicked(mouseEvent -> {
            if (loading.isVisible())
                return;
            loading.setVisible(true);
            tv.setEditable(false);
            tv2.setEditable(false);

            if (picker.getFile() != null) {
                ApiService.uploadImage(picker.getFile(), new ApiService.Callback() {
                    @Override
                    public void onResponse(String body) {
                        System.out.println(body);
                        UploadAvatarModel url = UploadAvatarModel.parseOnlyUrl(body);
                        if (url == null) {
                            onError(false, 0);
                        } else {
                            createServerNow(tv2.getText().trim(), url.url, loading, tv, tv2);
                        }
                    }

                    @Override
                    public void onError(boolean network, int code) {
                        loading.setVisible(false);
                        tv.setEditable(true);
                        tv2.setEditable(true);
                        Toast.makeText("Oops, something went wrong :(");
                    }

                    @Override
                    public void onError2(boolean network, int code, String res) {
                        loading.setVisible(false);
                        tv.setEditable(true);
                        tv2.setEditable(true);
                        BaseApiModel.toastError(res);
                    }
                });
            } else {
                createServerNow(tv2.getText().trim(), "", loading, tv, tv2);
            }
        });
    }

    void createServerNow(String name, String image, Node loading, TextField tv, TextField tv2) {
        EasyApi.createServer(name, image, model -> {
            ServerModel server = model.get();
            if (server == null) {
                loading.setVisible(false);
                tv.setEditable(true);
                tv2.setEditable(true);
                Toast.makeText("Oops, something went wrong :(");
            } else {
                StaticStorage.chats.servers.add(server);
                HomeController.requestToOpenServer = true;
                if (StaticListeners.updateOpenedChatsListener != null)
                    StaticListeners.updateOpenedChatsListener.update();

                Toast.makeText("Server created!");
            }
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

    @Override
    public boolean isEditablePage() {
        return true;
    }
}
