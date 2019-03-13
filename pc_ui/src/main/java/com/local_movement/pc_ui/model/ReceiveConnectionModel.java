package com.local_movement.pc_ui.model;

import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ReceiveConnectionModel {

    private StringProperty userName = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty size = new SimpleStringProperty();

    private MovementProperties movementProperties;

    public ReceiveConnectionModel(MovementProperties movementProperties) throws IOException {
        this.movementProperties = movementProperties;
        FileProperties fileProperties = movementProperties.getFileProperties();
        userName.setValue(fileProperties.getUserName());
        address.setValue(movementProperties.getAddress());
        fileName.setValue(fileProperties.getFileName());
    }
}
