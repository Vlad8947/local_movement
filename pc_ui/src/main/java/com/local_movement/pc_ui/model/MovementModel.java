package com.local_movement.pc_ui.model;

import com.local_movement.core.model.MovementType;
import com.local_movement.core.SizeConverter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MovementModel {

    private IntegerProperty number = new SimpleIntegerProperty();
    private StringProperty movementType = new SimpleStringProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private StringProperty size = new SimpleStringProperty();
    private StringProperty doneSize = new SimpleStringProperty();
    private StringProperty speed = new SimpleStringProperty();

    public MovementModel(int number, String fileName, MovementType movementType, long size) {
        this.movementType.setValue(movementType.toString());
        this.number.setValue(number);
        this.fileName.setValue(fileName);
        this.size.setValue(SizeConverter.size(size));
        doneSize.setValue(SizeConverter.size(0));
        speed.setValue(SizeConverter.speedInSecond(0));
    }

    public void setDoneSize(long size) {
        doneSize.setValue(
                SizeConverter.size(size));
    }

    public void setSpeed(long sizeInSecond) {
        speed.setValue(SizeConverter.speedInSecond(sizeInSecond));
    }

}
