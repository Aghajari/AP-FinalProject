package com.aghajari.ui;

import com.aghajari.Main;
import com.aghajari.MainUI;
import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.models.BaseApiModel;
import com.aghajari.models.LoginModel;
import com.aghajari.models.MyInfo;
import com.aghajari.models.UploadAvatarModel;
import com.aghajari.shared.SocketEvents;
import com.aghajari.shared.models.QRResult;
import com.aghajari.store.EasyApi;
import com.aghajari.store.StaticStorage;
import com.aghajari.util.Animations;
import com.aghajari.util.Toast;
import com.aghajari.util.Utils;
import com.google.zxing.EncodeHintType;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import io.github.palexdev.materialfx.controls.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class LoginController {

    public boolean isSignInPage = true;

    @FXML
    public Label sign_in, sign_up;
    @FXML
    public Pane indicator, tab_sign_in, tab_sign_up, tabs, AnchorPane,
            pages, first_page, username_page, avatar_page;
    @FXML
    public MFXProgressBar progress;
    @FXML
    public MFXTextField signup_email, signup_name, signin_email, username;
    @FXML
    public MFXPasswordField signup_password, signin_password;
    @FXML
    public ImageView avatar;
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView bg;
    @FXML
    public MFXButton qr_scan;
    @FXML
    public javafx.scene.layout.AnchorPane qr_page;
    @FXML
    public ImageView qr_image;
    @FXML
    public MFXProgressSpinner qr_loading;

    private boolean isShowingQRCode = false;
    private File uploadedFile;

    public void initialize() {
        Platform.runLater(this::initNow);
        bg.setImage(StaticStorage.spaceImage);

        ImageView img = new ImageView(new Image(Main.class.getResource("qrcode.png").toExternalForm(), true));
        img.setFitHeight(20);
        img.setFitWidth(20);
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);
        Blend blush = new Blend(
                BlendMode.SRC_ATOP,
                monochrome,
                new ColorInput(0, 0, 200, 200, Color.WHITE)
        );
        img.setEffect(blush);
        qr_scan.setGraphic(img);
    }

    public void initNow() {
        loadGradient();

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(AnchorPane.widthProperty());
        rectangle.heightProperty().bind(AnchorPane.heightProperty());
        tabs.setClip(rectangle);
        rectangle = new Rectangle();
        rectangle.widthProperty().bind(AnchorPane.widthProperty());
        rectangle.heightProperty().bind(AnchorPane.heightProperty());
        pages.setClip(rectangle);


        Circle circle = new Circle(50, 50, 50);
        avatar.setClip(circle);

        avatar_page.setOnDragOver(event -> {
            if (event.getGestureSource() != avatar_page
                    && event.getDragboard().hasFiles()
                    && event.getDragboard().getFiles().size() == 1) {
                String name = event.getDragboard().getFiles().get(0).getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });

        avatar_page.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                uploadedFile = db.getFiles().get(0);
                avatar.setImage(new Image(db.getFiles().get(0).toURI().toString()));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        avatar.setOnMouseClicked(mouseEvent -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose profile");
            chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
                    "Image", "png", "jpg", "jpeg"));
            File file = chooser.showOpenDialog(Toast.ownerStage);
            if (file != null) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    uploadedFile = file;
                    avatar.setImage(new Image(file.toURI().toString()));
                }
            }
        });
    }

    public void signUpClick(MouseEvent mouseEvent) {
        if (!isSignInPage)
            return;
        isSignInPage = false;
        loadGradient();

        Animations.translateX(0, indicator.getWidth(), 250, indicator);
        Animations.translateX(0, -tab_sign_in.getWidth(), 250, tab_sign_in);
        Animations.translateX(tab_sign_up.getWidth(), 0, 250, tab_sign_up);
    }

    public void signInClick(MouseEvent mouseEvent) {
        if (isSignInPage)
            return;
        isSignInPage = true;
        loadGradient();

        Animations.translateX(indicator.getWidth(), 0, 250, indicator);
        Animations.translateX(-tab_sign_in.getWidth(), 0, 250, tab_sign_in);
        Animations.translateX(0, tab_sign_up.getWidth(), 250, tab_sign_up);
    }

    private void loadGradient() {
        String css = "-fx-text-fill: linear-gradient(from 0.0% 0.0% to 100.0% 0.0%, #5ed3f7ff 0.0%, #1763c6ff 100.0%);";
        String css2 = "-fx-text-fill: white;";
        sign_in.setStyle(isSignInPage ? css : css2);
        sign_up.setStyle(!isSignInPage ? css : css2);
    }

    public void next_signup(ActionEvent actionEvent) {
        if (signup_email.getText().trim().isEmpty()) {
            Toast.makeText("Enter your email!");
            return;
        }
        if (signup_password.getText().isEmpty()) {
            Toast.makeText("Enter your password!");
            return;
        }
        if (signup_name.getText().isEmpty()) {
            Toast.makeText("Enter your name!");
            return;
        }
        if (signup_email.getText().trim().length() < 5) {
            Toast.makeText("Email must be at least 5 characters!");
            return;
        }
        if (signup_name.getText().trim().length() < 2) {
            Toast.makeText("Name must be at least 2 characters!");
            return;
        }
        if (signup_password.getText().length() < 8) {
            Toast.makeText("Password must be at least 8 characters!");
            return;
        }
        if (!Utils.isValidPass(signup_password.getText())) {
            Toast.makeText("Enter an stronger password!");
            return;
        }
        ApiService.savePassword(signup_password.getText());
        progress.setVisible(true);

        ApiService.register(signup_name.getText().trim(), signup_email.getText().trim(), signup_password.getText().trim(), new ApiService.Callback() {
            @Override
            public void onResponse(String body) {
                progress.setVisible(false);
                System.out.println(body);
                LoginModel model = LoginModel.parse(body);
                if (model.success) {
                    Animations.translateX(username_page.getWidth(), 0, 250, username_page);
                    Animations.translateX(0, -first_page.getWidth(), 250, first_page);
                } else {
                    Toast.makeText(model.error);
                }
            }

            @Override
            public void onError(boolean network, int code) {
                progress.setVisible(false);
                Toast.makeText("Oops, something went wrong :(");
            }

            @Override
            public void onError2(boolean network, int code, String res) {
                progress.setVisible(false);
                BaseApiModel.toastError(res);
            }
        });

    }

    public void next_signin(ActionEvent actionEvent) {
        if (signin_email.getText().trim().isEmpty()) {
            Toast.makeText("Enter your username or email!");
            return;
        }
        if (signin_password.getText().isEmpty()) {
            Toast.makeText("Enter your password!");
            return;
        }
        if (signin_email.getText().trim().length() < 5) {
            Toast.makeText("Username must be at least 5 characters!");
            return;
        }
        if (signin_password.getText().trim().length() < 6) {
            Toast.makeText("Password must be at least 6 characters!");
            return;
        }
        progress.setVisible(true);
        ApiService.savePassword(signin_password.getText().trim());

        ApiService.login(signin_email.getText().trim(), signin_password.getText().trim(), new ApiService.Callback() {
            @Override
            public void onResponse(String body) {
                System.out.println(body);
                LoginModel model = LoginModel.parse(body);
                if (model.success) {
                    EasyApi.auth(m -> {
                        if (m.get())
                            EasyApi.getChats(chats -> Platform.runLater(LoginController.this::start));
                    });
                } else {
                    Toast.makeText(model.error);
                }
            }

            @Override
            public void onError(boolean network, int code) {
                progress.setVisible(false);
                Toast.makeText("Oops, something went wrong :(");
            }

            @Override
            public void onError2(boolean network, int code, String res) {
                progress.setVisible(false);
                BaseApiModel.toastError(res);
            }
        });
    }

    public void next_username(ActionEvent actionEvent) {
        if (!Utils.isValidUsername(username.getText().trim())) {
            Toast.makeText("Username isn't valid!");
            return;
        }
        String name = username.getText().trim();
        progress.setVisible(true);
        ApiService.changeUsername(name, new ApiService.Callback() {
            @Override
            public void onResponse(String body) {
                MyInfo.getInstance().username = name;
                progress.setVisible(false);
                System.out.println(body);
                skip_username(null);
            }

            @Override
            public void onError(boolean network, int code) {
                progress.setVisible(false);
                Toast.makeText("Oops, something went wrong :(");
            }

            @Override
            public void onError2(boolean network, int code, String res) {
                progress.setVisible(false);
                BaseApiModel.toastError(res);
            }
        });
    }

    public void skip_username(ActionEvent actionEvent) {
        avatar.setImage(Utils.textToImage(MyInfo.getInstance().nickname));
        Animations.translateX(0, -username_page.getWidth(), 250, username_page);
        Animations.translateX(avatar_page.getWidth(), 0, 250, avatar_page);
    }

    public void signup_done(ActionEvent actionEvent) {
        if (uploadedFile != null) {
            progress.setVisible(true);
            ApiService.uploadAvatar(uploadedFile, new ApiService.Callback() {
                @Override
                public void onResponse(String body) {
                    System.out.println(body);
                    UploadAvatarModel.parse(body);
                    EasyApi.auth(model -> {
                        if (model.get())
                            EasyApi.getChats(chats -> Platform.runLater(LoginController.this::start));
                    });
                }

                @Override
                public void onError(boolean network, int code) {
                    progress.setVisible(false);
                    Toast.makeText("Oops, something went wrong :(");
                }

                @Override
                public void onError2(boolean network, int code, String res) {
                    progress.setVisible(false);
                    BaseApiModel.toastError(res);
                }
            });
        } else {
            EasyApi.auth(model -> {
                if (model.get())
                    EasyApi.getChats(chats -> Platform.runLater(LoginController.this::start));
            });
        }
    }

    public void start() {
        progress.setVisible(false);
        Parent root2;
        try {
            root2 = FXMLLoader.load(Main.class.getResource("fxml/home.fxml"));

            root2.setTranslateX(root.getWidth());
            MainUI.mainRoot.getChildren().add(root2);
            Animations.translateX(0, -root.getWidth(), 500, root,
                    actionEvent -> MainUI.mainRoot.getChildren().remove(root));
            Animations.translateX(root.getWidth(), 0, 500, root2);

            if (StaticListeners.updateOpenedChatsListener != null)
                StaticListeners.updateOpenedChatsListener.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back_qr(ActionEvent actionEvent) {
        isShowingQRCode = false;
        Animations.translateX(-first_page.getWidth(), 0, 250, first_page);
        Animations.translateX(0, qr_page.getWidth(), 250, qr_page);
        SocketApi.getInstance().clear(SocketEvents.QRCODE_AUTH);
    }

    public void next_qr(ActionEvent actionEvent) {
        isShowingQRCode = true;

        SocketApi.getInstance().register(SocketEvents.QRCODE_AUTH, model -> {
            QRResult res = model.get();
            MyInfo.getInstance().email = res.userModel.email;
            MyInfo.getInstance().nickname = res.userModel.nickname;
            MyInfo.getInstance().username = res.userModel.username;
            MyInfo.getInstance().id = res.userModel.id;
            MyInfo.getInstance()._id = res.userModel._id;
            MyInfo.getInstance().avatar = res.userModel.avatar;
            MyInfo.getInstance().setImage(null);
            MyInfo.getInstance().setColor(null);
            ApiService.saveUser(MyInfo.getInstance());
            ApiService.saveToken(res.token);

            EasyApi.getChats(chats -> Platform.runLater(LoginController.this::start));
        });

        Animations.translateX(0, -first_page.getWidth(), 250, first_page);
        Animations.translateX(qr_page.getWidth(), 0, 250, qr_page);
        Rectangle rectangle = new Rectangle(120, 120);
        rectangle.setArcWidth(12);
        rectangle.setArcHeight(12);
        qr_image.setClip(rectangle);

        qr_image.setVisible(false);
        qr_loading.setVisible(true);

        EasyApi.requestQRCode(model -> {
            if (!isShowingQRCode)
                return;

            ByteArrayOutputStream out = QRCode.from(model.data.toString())
                    .to(ImageType.PNG)
                    .withHint(EncodeHintType.MARGIN, 0)
                    .withSize(100, 100)
                    .stream();

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            Image img = new Image(in);
            WritableImage writableImage = new WritableImage(img.getPixelReader(),
                    100, 100);

            Color c1 = Color.valueOf("#5ed3f7").darker(),
                    c2 = Color.valueOf("#1763c6");

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    Color clr = writableImage.getPixelReader().getColor(i, j);
                    if (Color.BLACK.equals(clr)) {
                        writableImage.getPixelWriter().setColor(i, j,
                                Utils.getColorByIndex(c1, c2, (i + 1f) / 100f));
                    } /*else if (Color.WHITE.equals(clr)) {
                        writableImage.getPixelWriter().setColor(i, j, Color.TRANSPARENT);
                    }*/
                }
            }

            Platform.runLater(() -> {
                qr_image.setImage(writableImage);
                qr_loading.setVisible(false);
                qr_image.setVisible(true);
            });
        });
    }
}
