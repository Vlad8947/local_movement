package com.local_movement.pc_ui.controllers;

import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementType;
import com.local_movement.core.transfer.FileSender;
import com.local_movement.pc_ui.Chooser;
import com.local_movement.pc_ui.Dialog;
import com.local_movement.pc_ui.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;

import static com.local_movement.core.AppProperties.Localisation.messages;

public class SendFileViewController {

    private Dialog dialog = Dialog.getInstance();
    private File file;

    @FXML private Label userNameLabel;
    @FXML private TextField userNameField;

    @FXML private Label destinationIpLabel;
    @FXML private TextField addressField;

    @FXML private Label fileLabel;
    @FXML private Label filePathLabel;
    @FXML private Button selectFileButton;

    @FXML private Button sendButton;

    public SendFileViewController() {
    }

    @FXML
    private void initialize() {
        initSendForm();
    }

    private void initSendForm() {
        initSendFormLabels();
        initButtons();
    }

    private void initSendFormLabels() {
        userNameLabel.setText(messages.getString("form.your_username"));
        destinationIpLabel.setText(messages.getString("form.destination_ip"));
        fileLabel.setText(messages.getString("form.file"));
    }

    private void initButtons() {
        selectFileButton.setText(messages.getString("select_file"));
        selectFileButton.setOnAction(event -> chooseFileAction());

        sendButton.setText(messages.getString("send_file"));
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
        FileSender fileSender = new FileSender(movementProperties, dialog, MovementViewController.getMovementListAdapter());
        MainApp.getExecutorService().execute(fileSender);
    }

}
