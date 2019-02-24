package com.local_movement.pc_ui.view;


import com.local_movement.core.ui.data.PossibleInterfaceData;
import com.local_movement.core.ui.model.PossibleInterface;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;

public class MainViewController {

    @FXML private TableView<PossibleInterface> possibleTableView;
    @FXML private TableColumn<PossibleInterface, String> interfaceNameColumn;
    @FXML private TableColumn<PossibleInterface, String> interfaceIpColumn;
    @FXML @Getter private Tab sendFileTab;
    @FXML @Getter private Tab receiveFileTab;

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
