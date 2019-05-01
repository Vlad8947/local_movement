package com.local_movement.pc_ui;

import com.local_movement.core.view.DialogInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.local_movement.core.AppProperties.Localisation.messages;

public class Dialog implements DialogInterface {

    private static Dialog instance;

    private Dialog() {
    }

    public static synchronized Dialog getInstance() {
        if (instance == null) {
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
            throws NoSuchElementException, InterruptedException {

        StringBuilder builder = new StringBuilder();
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog(defaultText);
            dialog.setTitle(title);
            dialog.setHeaderText(header);
            dialog.setContentText(content);
            Optional<String> optional = dialog.showAndWait();
            String text;
            String headerText = messages.getString("dialog.field_cant_be_empty");
            while (optional.isPresent()) {
                text = optional.get();
                if (!text.isEmpty()) {
                    builder.append(text);
                    break;
                }
                dialog.setHeaderText(headerText);
                dialog.getEditor().setText(defaultText);
                optional = dialog.showAndWait();
            }
            synchronized (builder) {
                builder.notify();
            }
        });
        synchronized (builder) {
            builder.wait();
        }
        String result = builder.toString();
        return result.isEmpty() ? null : result;

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
