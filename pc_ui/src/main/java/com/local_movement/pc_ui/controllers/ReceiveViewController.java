package com.local_movement.pc_ui.controllers;

import com.local_movement.core.AppProperties;
import com.local_movement.core.ByteFormatter;
import com.local_movement.core.transfer.ConnectionsReceiver;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.transfer.FileReceiver;
import com.local_movement.core.view.MovementPropListAdapter;
import com.local_movement.pc_ui.Chooser;
import com.local_movement.pc_ui.Dialog;
import com.local_movement.pc_ui.model.ReceiveModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.File;
import java.rmi.server.RMIClassLoader;

import static com.local_movement.core.AppProperties.Localisation.messages;

public class ReceiveViewController {

    private static ObservableList<ReceiveModel> receiveList = FXCollections.observableArrayList();

    @Getter
    private static MovementPropListAdapter<ReceiveModel> receiveListAdapter =
            new MovementPropListAdapter<ReceiveModel>(receiveList) {
                @Override
                public void add(MovementProperties movementProperties) {
                    list.add(new ReceiveModel(movementProperties));
                }
            };

    private File saveFile;
    private ConnectionsReceiver connectionsReceiver;

    private Dialog dialog = Dialog.getInstance();

    @FXML
    private Label saveDirLabel;
    @FXML
    private Label pathLabel;
    @FXML
    private Button selectSaveDirectoryButton;
    @FXML
    private Label freeSpaceLabel;
    @FXML
    private Label freeSpaceNumberLabel;

    @FXML
    private Button receiveConnectionsOrCancelButton;
    @FXML
    private Label waitingLabel;

    @FXML
    private VBox chooseConnectionVBox;
    @FXML
    private Label chooseConnLabel;
    @FXML
    private TableView<ReceiveModel> connectionTable;
    @FXML
    private TableColumn<ReceiveModel, String> userNameColumn;
    @FXML
    private TableColumn<ReceiveModel, String> addressColumn;
    @FXML
    private TableColumn<ReceiveModel, String> fileNameColumn;
    @FXML
    private TableColumn<ReceiveModel, String> lengthColumn;

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
        chooseConnBoxInit();
        buttonActionsInit();
    }

    private void saveDirectoryInit() {
        saveFile = new File(System.getProperty("user.dir"));

        saveDirLabel.setText(messages.getString("form.save_dir"));
        pathLabel.setText(saveFile.getPath());
        selectSaveDirectoryButton.setText(messages.getString("select"));
        freeSpaceLabel.setText(messages.getString("form.free_space_on_the_disc"));

        updateFreeSpaceNumber();
    }

    private void chooseConnBoxInit() {
        chooseConnLabel.setText(messages.getString("choose_connection"));
        connectionTableInit();
        receiveFileButton.setText(messages.getString("receive_file"));
        cancelConnButton.setText(messages.getString("cancel_connection"));
    }

    private void connectionTableInit() {
        userNameColumn.setText(messages.getString("user_name"));
        addressColumn.setText(messages.getString("ip_address"));
        fileNameColumn.setText(messages.getString("file_name"));
        lengthColumn.setText(messages.getString("length"));

        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().getUserName());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().getAddress());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        lengthColumn.setCellValueFactory(cellData -> cellData.getValue().getFileLength());
        connectionTable.setItems(receiveList);
    }

    private void buttonActionsInit() {
        selectSaveDirectoryButton.setOnAction(event -> selectSaveDirectoryAction());
        receiveFileButton.setOnAction(event -> receiveFileAction());
        cancelConnButton.setOnAction(event -> cancelConnectionAction());
    }

    private void updateFreeSpaceNumber() {
        freeSpaceNumberLabel.setText(
                ByteFormatter.length(saveFile.getFreeSpace()));
    }

    private void selectSaveDirectoryAction() {
        File directory = Chooser.chooseDirectory();
        if (directory == null) {
            return;
        }
        saveFile = directory;
        pathLabel.setText(directory.getPath());
        updateFreeSpaceNumber();
    }

    private MovementProperties getSelectionProperties() {
        return connectionTable.getSelectionModel().getSelectedItem().getMovementProperties();
    }

    private void receiveFileAction() {
        MovementProperties movementProperties = getSelectionProperties();
        FileReceiver fileReceiver = new FileReceiver(movementProperties, saveFile.getPath(), dialog,
                receiveListAdapter, MovementViewController.getMovementListAdapter());
        AppProperties.getExecutorService().execute(fileReceiver);
    }

    private void cancelConnectionAction() {
        MovementProperties movementProperties = getSelectionProperties();
        receiveListAdapter.remove(movementProperties);
        ConnectionsReceiver.sendCancelConnectionMessage(movementProperties);
    }

    private void receiveConnectionsAction() {
        connectionsReceiver = new ConnectionsReceiver(receiveListAdapter);
        AppProperties.getExecutorService().execute(connectionsReceiver);
        receiveConnectionsPhase();
    }

    private void cancelReceiveConnectionsAction() {
        connectionsReceiver.close();
        notReceiveConnectionsPhase();
    }

    private void receiveConnectionsPhase() {
        waitingLabel.setVisible(true);
        chooseConnectionVBox.setDisable(false);
        receiveConnectionsOrCancelButton.setText(
                messages.getString("cancel_receive_connections"));
        receiveConnectionsOrCancelButton.setOnAction(event -> cancelReceiveConnectionsAction());
    }

    private void notReceiveConnectionsPhase() {
        receiveList.clear();
        waitingLabel.setVisible(false);
        chooseConnectionVBox.setDisable(true);
        receiveConnectionsOrCancelButton.setText(
                messages.getString("receive_connections"));
        receiveConnectionsOrCancelButton.setOnAction(event -> receiveConnectionsAction());
    }

}
