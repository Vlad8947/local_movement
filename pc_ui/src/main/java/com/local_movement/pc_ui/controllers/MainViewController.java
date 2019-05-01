package com.local_movement.pc_ui.controllers;

import com.local_movement.core.network.NetworkInterfaceFinder;
import com.local_movement.pc_ui.ViewLoader;
import com.local_movement.pc_ui.model.PossibleNetInterfaceModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;

import static com.local_movement.core.AppProperties.Localisation.messages;

public class MainViewController {

    private ObservableList<PossibleNetInterfaceModel> interfaceList =
            FXCollections.observableArrayList();
    private MovementViewController movementViewController;

    @FXML private Label localAddressesLabel;
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
        initLabel();
        initInterfaceTable();
        initTabs();
    }

    private void initLabel() {
        localAddressesLabel.setText(messages.getString("your_local_addresses"));
    }

    private void initInterfaceTable() {
        interfaceNameColumn.setText(
                messages.getString("interface_name"));
        interfaceNameColumn.setCellValueFactory(cellData -> cellData.getValue().getNetInterfaceName());

        interfaceAddressColumn.setText(
                messages.getString("your_ip"));
        interfaceAddressColumn.setCellValueFactory(cellData -> cellData.getValue().getIp());

        updateInterfaceList();
        interfaceTable.setItems(interfaceList);
    }

    private void initTabs() throws IOException {
        initSendTab();
        initReceiveTab();
        initMovementTab();
    }

    private void initSendTab() throws IOException {
        String contentPath = "view/SendFileView.fxml";
        String text = messages.getString("send_file");
        initTab(sendTab, text, contentPath);
    }

    private void initReceiveTab() throws IOException {
        String contentPath = "view/ReceiveFileView.fxml";
        String text = messages.getString("receive_file");
        initTab(receiveTab, text, contentPath);
    }

    private void initMovementTab() throws IOException {
        String contentPath = "view/MovementView.fxml";
        String text = messages.getString("movement_table");
        initTab(movementTab, text, contentPath);
    }

    private void updateInterfaceList() {
        interfaceList.clear();
        NetworkInterfaceFinder.find((interfaceName, address) -> {
            interfaceList.add(new PossibleNetInterfaceModel(interfaceName, address));
        });
    }

    private void initTab(Tab tab, String text, String contentPath) throws IOException {
        tab.setText(text);
        tab.setContent(ViewLoader.loadView(contentPath));
    }

}
