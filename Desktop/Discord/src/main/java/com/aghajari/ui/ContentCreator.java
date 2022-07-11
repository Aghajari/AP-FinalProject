package com.aghajari.ui;

import javafx.scene.Node;

import java.io.IOException;

public interface ContentCreator {
    Node create() throws IOException;
}
