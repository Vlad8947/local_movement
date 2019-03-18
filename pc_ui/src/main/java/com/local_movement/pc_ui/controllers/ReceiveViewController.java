package com.local_movement.pc_ui.controllers;

import com.local_movement.core.Converter;
import com.local_movement.core.MovementPropListAdapter;
import com.local_movement.core.transfer.ChannelTransfer;
import com.local_movement.core.transfer.ConnectionsReceiver;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.transfer.FileReceiver;
import com.local_movement.pc_ui.Chooser;
import com.local_movement.pc_ui.Dialog;
import com.local_movement.pc_ui.model.ReceiveConnectionModel;
import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class ReceiveViewController implements MovementPropListAdapter {

    @Getter private static ReceiveViewController instance;

    private File saveDirectory;
    private ConnectionsReceiver connectionsReceiver;

    private final String receiveConnectionsText = "Receive connections";
    private final String cancelReceiveConnectionsText = "Cancel receive connections";

    private Dialog dialog = Dialog.getInstance();
    private ObservableList<ReceiveConnectionModel> connectionList = FXCollections.observableArrayList();

    @FXML private Label saveDirectoryLabel;
    @FXML private Button selectSaveDirectoryButton;
    @FXML private Label freeSpaceLabel;

    @FXML private Button receiveConnectionsOrCancelButton;
    @FXML private Label waitingLabel;

    @FXML private VBox chooseConnectionVBox;
    @FXML private TableView<ReceiveConnectionModel> connectionTable;
    @FXML private TableColumn<ReceiveConnectionModel, String> userNameColumn;
    @FXML private TableColumn<ReceiveConnectionModel, String> addressColumn;
    @FXML private TableColumn<ReceiveConnectionModel, String> fileNameColumn;
    @FXML private TableColumn<ReceiveConnectionModel, String> sizeColumn;

    @FXML private ButtonBar connectionBar;
    @FXML private Button receiveFileButton;
    @FXML private Button cancelConnButton;

    public ReceiveViewController() {
        instance = this;
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
        int connectionIndex = connectionTable.getSelectionModel().getSelectedIndex();
        return connectionList.get(connectionIndex).getMovementProperties();
    }

    private void receiveFileAction() {
        MovementProperties movementProperties = getSelectionProperties();
        FileReceiver fileReceiver = new FileReceiver(movementProperties, saveDirectory.getPath(), dialog,
                this, MovementViewController.getInstance());
        fileReceiver.fork();
    }

    private void cancelConnectionAction() {
        MovementProperties movementProperties = getSelectionProperties();
        remove(movementProperties);
        ConnectionsReceiver.sendCancelConnectionMessage(movementProperties);
    }

    private void connectionTableInit() {
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserName());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().getFileLength());
        connectionTable.setItems(connectionList);
    }

    private void receiveConnectionsAction() {
        connectionsReceiver = new ConnectionsReceiver(this);
        connectionsReceiver.fork();
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
        connectionList.clear();
        waitingLabel.setVisible(false);
        chooseConnectionVBox.setDisable(true);
        receiveConnectionsOrCancelButton.setText(receiveConnectionsText);
        receiveConnectionsOrCancelButton.setOnAction(event -> receiveConnectionsAction());
    }

    @Override
    public void add(MovementProperties movementProperties) throws IOException {
        connectionList.add(new ReceiveConnectionModel(movementProperties));
    }

    @Override
    public void remove(@NotNull MovementProperties movementProperties) {
        connectionList.removeIf(t -> t.getMovementProperties().equals(movementProperties));
    }
}
