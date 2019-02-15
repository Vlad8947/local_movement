package com.local_movement.pc_ui.view;

import com.local_movement.core.ui.data.PossibleInterfaceData;
import com.local_movement.core.ui.model.PossibleInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainViewController {

    @FXML private TableView<PossibleInterface> possibleTableView;
    @FXML private TableColumn<PossibleInterface, String> interfaceNameColumn;
    @FXML private TableColumn<PossibleInterface, String> interfaceIpColumn;
    @FXML private Tab sendFileTab;
    @FXML private Tab receiveFileTab;

    public MainViewController() {
    }

    @FXML
    private void initialize() {
        initializePossibleTable();
    }

    private void initializePossibleTable() {
        interfaceNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        interfaceIpColumn.setCellValueFactory(cellData -> cellData.getValue().getIp());
        possibleTableView.setItems(PossibleInterfaceData.getObservableList());
    }

}
