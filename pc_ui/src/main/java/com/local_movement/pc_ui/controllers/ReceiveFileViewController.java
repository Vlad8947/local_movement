package com.local_movement.pc_ui.controllers;

import com.local_movement.core.transfer.ConnectionsReceiver;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.pc_ui.model.ReceiveConnectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ReceiveFileViewController implements ConnectionsReceiver.MovementPropAdder {

    private final String receiveConnectionsText = "Receive connections";
    private final String cancelReceiveConnectionsText = "Cancel receive connections";

    private ObservableList<ReceiveConnectionModel> connectionList = FXCollections.observableArrayList();
    private ConnectionsReceiver connectionsReceiver;

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

    public ReceiveFileViewController() {
    }

    @FXML
    private void initialize() {
        notReceiveConnectionsPhase();
        connectionTableInit();
        buttonsInit();
    }

    private void buttonsInit() {
        receiveFileButton.setOnAction(event -> receiveFileAction());
        cancelConnButton.setOnAction(event -> cancelConnectionAction());
    }

    private void receiveFileAction() {
        int connectionIndex = connectionTable.getSelectionModel().getSelectedIndex();
        MovementProperties movementProperties = connectionList.get(connectionIndex).getMovementProperties();

    }

    private void cancelConnectionAction() {

    }

    private void connectionTableInit() {
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserName());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().getSize());
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
}
