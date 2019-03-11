package com.local_movement.pc_ui.controllers;

import com.local_movement.core.FileProperties;
import com.local_movement.core.MovementProperties;
import com.local_movement.core.MovementType;
import com.local_movement.core.Sender;
import com.local_movement.pc_ui.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;


public class SendFileViewController {

    @FXML private TextField addressTextField;
    @FXML private Button selectFileButton;
    @FXML private Label filePathLabel;
    @FXML private Button sendFileButton;
    @FXML private TextField userNameField;
    private File file;

    public SendFileViewController() {
    }

    @FXML
    private void initialize() {
        initButtons();
    }

    private void initButtons() {
        selectFileButton.setOnAction(event -> chooseFileAction());
        sendFileButton.setOnAction(event -> sendFileAction());
    }

    private void chooseFileAction() {
        file = MainApp.chooseFile();
        filePathLabel.setText(file.getPath());
    }

    private void sendFileAction() {
        FileProperties fileProperties = new FileProperties(userNameField.getText(), file.getName(), file.length());
        MovementProperties movementProperties =
                new MovementProperties(addressTextField.getText(), fileProperties, MovementType.SEND);
        Sender sender = new Sender(movementProperties);
        sender.fork();
    }

}
