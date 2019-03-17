package com.local_movement.pc_ui;

import javafx.fxml.FXMLLoader;
import lombok.NonNull;

import java.io.IOException;
import java.net.URL;

public class ViewLoader {

    public static <T> T loadView(String path) throws IOException {
        ClassLoader classLoader = ViewLoader.class.getClassLoader();
        @NonNull URL url = classLoader.getResource(path);
        return FXMLLoader.load(url);
    }

}
