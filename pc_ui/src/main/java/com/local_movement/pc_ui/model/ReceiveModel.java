package com.local_movement.pc_ui.model;

import com.local_movement.core.ByteFormatter;
import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.view.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter
public class ReceiveModel extends ViewModel<ReceiveModel> {

    private StringProperty userName = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty fileLength = new SimpleStringProperty();

    public ReceiveModel(MovementProperties movementProperties) {
        super(movementProperties);
        FileProperties fileProperties = movementProperties.getFileProperties();
        userName.setValue(fileProperties.getUserName());
        address.setValue(movementProperties.getAddress());
        fileName.setValue(fileProperties.getFileName());
        fileLength.setValue(ByteFormatter.length(
                movementProperties.getFileProperties().getFileLength()));
    }

}
