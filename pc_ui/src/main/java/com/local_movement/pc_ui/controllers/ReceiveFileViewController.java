package com.local_movement.pc_ui.controllers;

import com.local_movement.pc_ui.model.ReceiveConnectionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ReceiveFileViewController {

    private final String receiveConnectionsText = "Receive connections";
    private final String cancelReceiveConnectionsText = "Cancel receive connections";

    @FXML private Button receiveConnectionsOrCancelButton;
    @FXML private Label waitingLabel;

    @FXML private VBox chooseConnectionVBox;
    private ObservableList<ReceiveConnectionModel> connectionList =
            FXCollections.observableArrayList();
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
    }

    private void connectionTableInit() {
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserName());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().getSize());
        connectionTable.setItems(connectionList);
    }

    //todo
    private void receiveConnectionsAction() {

        receiveConnectionsPhase();
    }

    //todo
    private void cancelReceiveConnectionsAction() {

        notReceiveConnectionsPhase();
    }

    private void receiveConnectionsPhase() {
        waitingLabel.setVisible(true);
        chooseConnectionVBox.setDisable(false);
        receiveConnectionsOrCancelButton.setText(cancelReceiveConnectionsText);
        receiveConnectionsOrCancelButton.setOnAction(event -> cancelReceiveConnectionsAction());
    }

    private void notReceiveConnectionsPhase() {
        waitingLabel.setVisible(false);
        chooseConnectionVBox.setDisable(true);
        receiveConnectionsOrCancelButton.setText(receiveConnectionsText);
        receiveConnectionsOrCancelButton.setOnAction(event -> receiveConnectionsAction());
    }

}
