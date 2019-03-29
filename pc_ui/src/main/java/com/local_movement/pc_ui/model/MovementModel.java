package com.local_movement.pc_ui.model;

import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.Converter;
import com.local_movement.core.view.ViewModel;
import com.local_movement.pc_ui.Updatable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.io.IOException;

@Getter
public class MovementModel extends ViewModel<MovementModel> implements Updatable {

    private IntegerProperty number = new SimpleIntegerProperty();
    private StringProperty movementType = new SimpleStringProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty fileLength = new SimpleStringProperty();
    private StringProperty doneBytes = new SimpleStringProperty();
    private StringProperty speed = new SimpleStringProperty();

    public MovementModel(MovementProperties movementProperties) {
        super(movementProperties);
        fileName.setValue(movementProperties.getFile().getName());
        fileLength.setValue(Converter.length(movementProperties.getFileProperties().getFileLength()));
        speed.setValue(Converter.speedInSecond(0));
        movementType.setValue(movementProperties.getType().toString());
        doneBytes.setValue(Converter.length(movementProperties.getFile().length()));
    }

    @Override
    public void update() {
        doneBytes.setValue(Converter.length(movementProperties.getFile().length()));
    }

}
