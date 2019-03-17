package com.local_movement.pc_ui.controllers;

import com.local_movement.core.MovementPropListAdapter;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.pc_ui.model.MovementModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;

import java.io.IOException;

public class MovementViewController implements MovementPropListAdapter {

    @Getter private static MovementViewController instance;

    private ObservableList<MovementModel> movementList =
            FXCollections.observableArrayList();

    @FXML private TableView<MovementModel> movementTable;
    @FXML private TableColumn<MovementModel, Integer> numberColumn;
    @FXML private TableColumn<MovementModel, String> fileNameColumn;
    @FXML private TableColumn<MovementModel, String> movementTypeColumn;
    @FXML private TableColumn<MovementModel, String> lengthColumn;
    @FXML private TableColumn<MovementModel, String> doneColumn;
    @FXML private TableColumn<MovementModel, String> speedColumn;

    @FXML private ButtonBar movementBar;
    @FXML private Button continueButton;
    @FXML private Button pauseButton;
    @FXML private Button cancelButton;

    public MovementViewController() {
        instance = this;
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
        movementTable.setItems(movementList);
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

    @Override
    public void add(MovementProperties movementProperties) throws IOException {
        movementList.add(new MovementModel(movementProperties));
    }

    @Override
    public void remove(MovementProperties movementProperties) {

    }
}
