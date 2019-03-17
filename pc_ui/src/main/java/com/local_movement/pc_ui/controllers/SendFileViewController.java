package com.local_movement.pc_ui.controllers;

import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementType;
import com.local_movement.core.transfer.Sender;
import com.local_movement.pc_ui.Chooser;
import com.local_movement.pc_ui.Dialog;
import com.local_movement.pc_ui.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;


public class SendFileViewController {

    private Dialog dialog = Dialog.getInstance();
    private File file;

    @FXML private TextField userNameField;
    @FXML private TextField addressField;
    @FXML private Button selectFileButton;
    @FXML private Label filePathLabel;
    @FXML private Button sendButton;

    public SendFileViewController() {
    }

    @FXML
    private void initialize() {
        initButtons();
    }

    private void initButtons() {
        selectFileButton.setOnAction(event -> chooseFileAction());
        sendButton.setOnAction(event -> sendAction());
    }

    private void chooseFileAction() {
        File newFile = Chooser.chooseFile();
        if (newFile == null) {
            return;
        }
        file = newFile;
        filePathLabel.setText(file.getPath());
    }

    private void sendAction() {
        if (userNameField.getText().isEmpty() || addressField.getText().isEmpty() || file == null) {
            String title = "Send file error";
            String header = "The field(s) is empty!";
            dialog.error(title, header, null);
            return;
        }

        FileProperties fileProperties = new FileProperties(userNameField.getText(), file.getName(), file.length());
        MovementProperties movementProperties =
                new MovementProperties(addressField.getText(), file, fileProperties, MovementType.SEND);
        Sender sender = new Sender(movementProperties, dialog);
        sender.fork();
    }

}
