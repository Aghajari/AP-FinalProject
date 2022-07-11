module Discord {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires MaterialFX;
    requires retrofit2;
    requires com.google.gson;
    requires retrofit2.converter.gson;
    requires retrofit2.converter.scalars;
    requires okhttp3;
    requires java.prefs;
    requires jiconfont;
    requires jiconfont.google.material.design.icons;
    requires jiconfont.javafx;
    requires javafx.media;
    requires qrgen;
    requires com.google.zxing;

    opens com.aghajari.ui to javafx.fxml;
    opens com.aghajari.models to com.google.gson;
    opens com.aghajari.shared.models to com.google.gson;
    exports com.aghajari;
}
