package com.local_movement.pc_ui;

import com.local_movement.core.DialogInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.NoSuchElementException;
import java.util.Optional;

public class Dialog implements DialogInterface {

    private static Dialog instance;

    private Dialog(){}

    public static synchronized Dialog getInstance() {
        if(instance == null) {
            instance = new Dialog();
        }
        return instance;
    }

    public void information(String title, String header, String content) {
        dialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    @Override
    public void error(String title, String header, String content) {
        dialog(Alert.AlertType.ERROR, title, header, content);
    }

    @Override
    public String textInput(String defaultText, String title, String header, String content)
            throws NoSuchElementException {
        TextInputDialog dialog = new TextInputDialog(defaultText);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        Optional<String> result = dialog.showAndWait();
        String text;
        while (result.isPresent()) {
            text = result.get();
            if (!text.isEmpty()) {
                return text;
            }
            dialog.setHeaderText("The field can not be empty! Enter again");
            dialog.getEditor().setText(defaultText);
            result = dialog.showAndWait();
        }
        return null;
    }

    private void dialog(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}
