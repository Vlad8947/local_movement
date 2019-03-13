package com.local_movement.pc_ui;

import com.local_movement.core.DialogInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Dialog {

    public static void information(String title, String header, String content) {
        dialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void error(String title, String header, String content) {
        dialog(Alert.AlertType.ERROR, title, header, content);
    }

    private static void dialog(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}
