package com.aghajari.util;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;

public class ImagePicker {

    private File file;

    public ImagePicker(ImageView img, Node node){
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node
                    && event.getDragboard().hasFiles()
                    && event.getDragboard().getFiles().size() == 1) {
                String name = event.getDragboard().getFiles().get(0).getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                file = db.getFiles().get(0);
                img.setImage(new Image(db.getFiles().get(0).toURI().toString()));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        img.setOnMouseClicked(mouseEvent -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose profile");
            chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
                    "Image", "png", "jpg", "jpeg"));
            File f = chooser.showOpenDialog(Toast.ownerStage);
            if (f != null) {
                String name = f.getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    file = f;
                    img.setImage(new Image(f.toURI().toString()));
                }
            }
        });
    }

    public File getFile() {
        return file;
    }
}
