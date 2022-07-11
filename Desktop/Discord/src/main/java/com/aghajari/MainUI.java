package com.aghajari;

import com.aghajari.api.ApiService;
import com.aghajari.api.SocketApi;
import com.aghajari.util.Toast;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;

public class MainUI extends Application {

    public static AnchorPane mainRoot;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Toast.ownerStage = primaryStage;
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());
        ApiService.loadUser();

        mainRoot = new AnchorPane();
        Parent root = FXMLLoader.load(getClass().getResource("fxml/loading.fxml"));

        mainRoot.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        //MoveApplication.initMovement(root, primaryStage);
        mainRoot.setStyle("-fx-background-color: #202225;");
        mainRoot.getChildren().add(root);

        Scene scene = new Scene(mainRoot, 1000, 630);
        scene.setFill(Color.valueOf("#202225"));
        primaryStage.setTitle("Discord");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            SocketApi.closeSocket();
            System.exit(0);
        });

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        SocketApi.closeSocket();
    }

    public static void start(String[] args) {
        launch(args);
    }
}
