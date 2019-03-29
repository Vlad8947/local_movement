package com.local_movement.pc_ui.controllers;

import com.local_movement.core.Converter;
import com.local_movement.core.transfer.ConnectionsReceiver;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.transfer.FileReceiver;
import com.local_movement.pc_ui.Chooser;
import com.local_movement.pc_ui.Dialog;
import com.local_movement.pc_ui.MainApp;
import com.local_movement.pc_ui.MovementPropListAdapterImpl;
import com.local_movement.pc_ui.model.ReceiveModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class ReceiveViewController {

    @Getter
    private static MovementPropListAdapterImpl<ReceiveModel> receiveListAdapter =
            new MovementPropListAdapterImpl<ReceiveModel>() {
                @Override
                public void add(MovementProperties movementProperties) throws IOException {
                    list.add(new ReceiveModel(movementProperties));
                }
            };
    private ObservableList<ReceiveModel> receiveList = receiveListAdapter.getList();

    private File saveDirectory;
    private ConnectionsReceiver connectionsReceiver;

    private final String receiveConnectionsText = "Receive connections";
    private final String cancelReceiveConnectionsText = "Cancel receive connections";

    private Dialog dialog = Dialog.getInstance();

    @FXML
    private Label saveDirectoryLabel;
    @FXML
    private Button selectSaveDirectoryButton;
    @FXML
    private Label freeSpaceLabel;

    @FXML
    private Button receiveConnectionsOrCancelButton;
    @FXML
    private Label waitingLabel;

    @FXML
    private VBox chooseConnectionVBox;
    @FXML
    private TableView<ReceiveModel> connectionTable;
    @FXML
    private TableColumn<ReceiveModel, String> userNameColumn;
    @FXML
    private TableColumn<ReceiveModel, String> addressColumn;
    @FXML
    private TableColumn<ReceiveModel, String> fileNameColumn;
    @FXML
    private TableColumn<ReceiveModel, String> sizeColumn;

    @FXML
    private ButtonBar connectionBar;
    @FXML
    private Button receiveFileButton;
    @FXML
    private Button cancelConnButton;

    public ReceiveViewController() {

    }

    @FXML
    private void initialize() {
        saveDirectoryInit();
        notReceiveConnectionsPhase();
        connectionTableInit();
        buttonsInit();
    }

    private void saveDirectoryInit() {
        saveDirectory = new File(System.getProperty("user.dir"));
        saveDirectoryLabel.setText(saveDirectory.getPath());
        updateFreeSpace();
    }

    private void updateFreeSpace() {
        freeSpaceLabel.setText(
                Converter.length(saveDirectory.getFreeSpace()));
    }

    private void buttonsInit() {
        selectSaveDirectoryButton.setOnAction(event -> selectSaveDirectoryAction());
        receiveFileButton.setOnAction(event -> receiveFileAction());
        cancelConnButton.setOnAction(event -> cancelConnectionAction());
    }

    private void selectSaveDirectoryAction() {
        File directory = Chooser.chooseDirectory();
        if (directory == null) {
            return;
        }
        saveDirectory = directory;
        saveDirectoryLabel.setText(directory.getPath());
        updateFreeSpace();
    }

    private MovementProperties getSelectionProperties() {
        return connectionTable.getSelectionModel().getSelectedItem().getMovementProperties();
    }

    private void receiveFileAction() {
        MovementProperties movementProperties = getSelectionProperties();
        FileReceiver fileReceiver = new FileReceiver(movementProperties, saveDirectory.getPath(), dialog,
                receiveListAdapter, MovementViewController.getMovementListAdapter());
        MainApp.getExecutorService().execute(fileReceiver);
    }

    private void cancelConnectionAction() {
        MovementProperties movementProperties = getSelectionProperties();
        receiveListAdapter.remove(movementProperties);
        ConnectionsReceiver.sendCancelConnectionMessage(movementProperties);
    }

    private void connectionTableInit() {
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserName());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().getFileLength());
        connectionTable.setItems(receiveList);
    }

    private void receiveConnectionsAction() {
        connectionsReceiver = new ConnectionsReceiver(receiveListAdapter);
        MainApp.getExecutorService().execute(connectionsReceiver);
        receiveConnectionsPhase();
    }

    private void cancelReceiveConnectionsAction() {
        connectionsReceiver.close();
        notReceiveConnectionsPhase();
    }

    private void receiveConnectionsPhase() {
        waitingLabel.setVisible(true);
        chooseConnectionVBox.setDisable(false);
        receiveConnectionsOrCancelButton.setText(cancelReceiveConnectionsText);
        receiveConnectionsOrCancelButton.setOnAction(event -> cancelReceiveConnectionsAction());
    }

    private void notReceiveConnectionsPhase() {
        receiveList.clear();
        waitingLabel.setVisible(false);
        chooseConnectionVBox.setDisable(true);
        receiveConnectionsOrCancelButton.setText(receiveConnectionsText);
        receiveConnectionsOrCancelButton.setOnAction(event -> receiveConnectionsAction());
    }

}
