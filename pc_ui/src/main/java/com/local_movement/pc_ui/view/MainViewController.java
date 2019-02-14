package com.local_movement.pc_ui.view;

import com.local_movement.pc_ui.model.MovementData;
import com.local_movement.pc_ui.model.ReceiveData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

public class MainViewController {

    @FXML
    private TextField destIPField;
    @FXML
    private Label srcFileNameLabel;

    @FXML
    private TableView<ReceiveData> receiveTable;
    @FXML
    private ToolBar reciveTool;

    @FXML
    private TableView<MovementData> movementTable;
    @FXML
    private ToolBar movementTool;

    public MainViewController() {
    }

    @FXML
    private void initialize(){

    }


    @FXML
    private void selectSrcFileAction() {

    }

    @FXML
    private void sendFileAction() {

    }

    @FXML
    private void moveToMovementAction() {

    }

    @FXML
    private void denialFromReceiveAction() {

    }

    @FXML
    private void continueMovementAction() {

    }

    @FXML
    private void pauseMovementAction() {

    }

    @FXML
    private void denialFromMovementAction() {

    }

}
