package com.local_movement.pc_ui.controllers;

import com.local_movement.core.model.MovementProperties;
import com.local_movement.pc_ui.MainApp;
import com.local_movement.pc_ui.MovementPropListAdapterImpl;
import com.local_movement.pc_ui.Updatable;
import com.local_movement.pc_ui.model.MovementModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveAction;

public class MovementViewController {

    @Getter
    private static MovementPropListAdapterImpl<MovementModel> movementListAdapter =
            new MovementPropListAdapterImpl<MovementModel>() {
                @Override
                public void add(MovementProperties movementProperties) throws IOException {
                    list.add(new MovementModel(movementProperties));
                }
            };

    @FXML
    private TableView<MovementModel> movementTable;
    @FXML
    private TableColumn<MovementModel, Integer> numberColumn;
    @FXML
    private TableColumn<MovementModel, String> fileNameColumn;
    @FXML
    private TableColumn<MovementModel, String> movementTypeColumn;
    @FXML
    private TableColumn<MovementModel, String> lengthColumn;
    @FXML
    private TableColumn<MovementModel, String> doneColumn;
    @FXML
    private TableColumn<MovementModel, String> speedColumn;

    @FXML
    private ButtonBar movementBar;
    @FXML
    private Button continueButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button cancelButton;

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
        numberColumn.setCellValueFactory(cellData -> cellData.getValue().getNumber().asObject());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getFileName());
        movementTypeColumn.setCellValueFactory(cellData -> cellData.getValue().getMovementType());
        lengthColumn.setCellValueFactory(cellData -> cellData.getValue().getFileLength());
        doneColumn.setCellValueFactory(cellData -> cellData.getValue().getDoneBytes());
        speedColumn.setCellValueFactory(cellData -> cellData.getValue().getSpeed());
        movementTable.setItems(movementListAdapter.getList());
    }

    private void buttonBarInit() {
        movementBar.setDisable(true);
        continueButton.setOnAction(event -> continueAction());
        pauseButton.setOnAction(event -> pauseAction());
        cancelButton.setOnAction(event -> cancelAction());
    }

    //todo
    private void continueAction() {

    }

    //todo
    private void pauseAction() {

    }

    //todo
    private void cancelAction() {

    }

}
