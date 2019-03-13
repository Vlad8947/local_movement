package com.local_movement.pc_ui.controllers;

import com.local_movement.core.network.NetworkInterfaceFinder;
import com.local_movement.pc_ui.MainApp;
import com.local_movement.pc_ui.model.PossibleNetInterfaceModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;

public class MainViewController {

    private ObservableList<PossibleNetInterfaceModel> interfaceList =
            FXCollections.observableArrayList();
    @FXML private TableView<PossibleNetInterfaceModel> interfaceTable;
    @FXML private TableColumn<PossibleNetInterfaceModel, String> interfaceNameColumn;
    @FXML private TableColumn<PossibleNetInterfaceModel, String> interfaceAddressColumn;
    @FXML private Tab sendTab;
    @FXML private Tab receiveTab;
    @FXML private Tab movementTab;

    public MainViewController() {
    }

    @FXML
    private void initialize() throws IOException {
        initInterfaceTable();
        initTabs();
    }

    private void initInterfaceTable() {
        interfaceNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        interfaceAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getIp());
        updateInterfaceList();
        interfaceTable.setItems(interfaceList);
    }

    private void updateInterfaceList() {
        interfaceList.clear();
        NetworkInterfaceFinder.find((interfaceName, address) -> {
            interfaceList.add(new PossibleNetInterfaceModel(interfaceName, address));
        });
    }

    private void initTabs() throws IOException {
        loadSendView();
        loadReceiveView();
        loadMovementView();
    }

    private void loadSendView() throws IOException {
        String path = "view/SendFileView.fxml";
        Node sendView = MainApp.getInstance().loadView(path);
        sendTab.setContent(sendView);
    }

    private void loadReceiveView() throws IOException {
        String path = "view/ReceiveFileView.fxml";
        Node view = MainApp.getInstance().loadView(path);
        receiveTab.setContent(view);
    }

    private void loadMovementView() throws IOException {
        String path = "view/MovementView.fxml";
        Node view = MainApp.getInstance().loadView(path);
        movementTab.setContent(view);
    }

}
