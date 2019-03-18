package com.local_movement.pc_ui.model;

import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.Converter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MovementModel {

    private MovementProperties movementProperties;
    private IntegerProperty number = new SimpleIntegerProperty();
    private StringProperty movementType = new SimpleStringProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty fileLength = new SimpleStringProperty();
    private StringProperty doneBytes = new SimpleStringProperty();
    private StringProperty speed = new SimpleStringProperty();

    public MovementModel(MovementProperties movementProperties) {
        this.movementProperties = movementProperties;
        movementType.setValue(movementProperties.getType().toString());
        fileName.setValue(movementProperties.getFile().getName());
        fileLength.setValue(Converter.length(
                movementProperties.getFileProperties().getFileLength()));
        updateDoneBytes();
        speed.setValue(Converter.speedInSecond(0));
    }

    public void updateDoneBytes() {
        doneBytes.setValue(Converter.length(
                movementProperties.getFile().length()));
    }

}
