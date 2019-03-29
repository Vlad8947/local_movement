package com.local_movement.pc_ui.model;

import com.local_movement.core.Converter;
import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.view.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.io.IOException;

@Getter
public class ReceiveModel extends ViewModel<ReceiveModel> {

    private StringProperty userName = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty fileLength = new SimpleStringProperty();

    public ReceiveModel(MovementProperties movementProperties) throws IOException {
        super(movementProperties);
        FileProperties fileProperties = movementProperties.getFileProperties();
        userName.setValue(fileProperties.getUserName());
        address.setValue(movementProperties.getAddress());
        fileName.setValue(fileProperties.getFileName());
        fileLength.setValue(Converter.length(
                movementProperties.getFileProperties().getFileLength()));
    }

}
