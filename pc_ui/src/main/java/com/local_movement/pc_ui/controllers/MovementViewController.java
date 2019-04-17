package com.local_movement.pc_ui.controllers;

import com.local_movement.core.model.MovementProperties;
import com.local_movement.pc_ui.MainApp;
import com.local_movement.pc_ui.MovementPropListAdapterImpl;
import com.local_movement.pc_ui.model.MovementModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;

import java.io.IOException;

import static com.local_movement.core.AppProperties.Localisation.messages;

public class MovementViewController {

    @Getter
    private static MovementPropListAdapterImpl<MovementModel> movementListAdapter =
            new MovementPropListAdapterImpl<MovementModel>() {
                @Override
                public void add(MovementProperties movementProperties) {
                    list.add(new MovementModel(movementProperties));
                }
            };

    @FXML
    private TableView<MovementModel> movementTable;
    @FXML
    private ButtonBar movementBar;
    @FXML
    private Button deleteMovementButton;

    public MovementViewController() {
        Runnable statisticUpdater = new Runnable() {
            private ObservableList<MovementModel> list = getMovementListAdapter().getList();
            @Override
            public void run() {
                try {
                    Object syncKey = new Object();
                    while (true) {
                        if(!list.isEmpty())
                        synchronized (syncKey) {
                            syncKey.wait(1000);
                            list.forEach(prop -> prop.update());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        MainApp.getExecutorService().submit(statisticUpdater);
    }

    @FXML
    private void initialize() {
        movementTableInit();
        buttonBarInit();
    }

    private void movementTableInit() {
        movementTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == -1) {
                movementBar.setDisable(true);
                return;
            }
            movementBar.setDisable(false);
        });
        columnsInit();
        movementTable.setItems(movementListAdapter.getList());
    }

    private void columnsInit() {
        TableColumn<MovementModel, String> fileNameColumn, movementTypeColumn, lengthColumn, doneLengthColumn, statusColumn;

        fileNameColumn = new TableColumn<>(messages.getString("file_name"));
        movementTable.getColumns().add(fileNameColumn);

        movementTypeColumn = new TableColumn<>(messages.getString("type"));
        movementTable.getColumns().add(movementTypeColumn);

        statusColumn = new TableColumn<>(messages.getString("status"));
        movementTable.getColumns().add(statusColumn);

        lengthColumn = new TableColumn<>(messages.getString("length"));
        movementTable.getColumns().add(lengthColumn);

        doneLengthColumn = new TableColumn<>(messages.getString("done"));
        movementTable.getColumns().add(doneLengthColumn);

        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        movementTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getMovementType());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatus());
        lengthColumn.setCellValueFactory(cellData -> cellData.getValue().getFileLength());
        doneLengthColumn.setCellValueFactory(cellData -> cellData.getValue().getDoneBytes());
    }

    private void buttonBarInit() {
        movementBar.setDisable(true);
        deleteMovementButton.setText(messages.getString("delete_movement"));
        deleteMovementButton.setOnAction(event -> deleteMovementAction());
    }

    //todo
    private void deleteMovementAction() {
        MovementModel movementModel = getSelectedModel();
        try {
            movementModel.getMovementProperties().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        movementTable.getItems().remove(movementModel);
    }

    private MovementModel getSelectedModel() {
        return movementTable.getSelectionModel().getSelectedItem();
    }

}
