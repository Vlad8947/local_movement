package com.local_movement.pc_ui;

import com.local_movement.core.view.MovementPropListAdapter;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.view.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.io.IOException;

@Getter
public abstract class MovementPropListAdapterImpl<E extends ViewModel> implements MovementPropListAdapter {

    protected ObservableList<E> list =
            FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    protected long lastUpdate = System.currentTimeMillis();

    @Override
    abstract public void add(MovementProperties movementProperties) throws IOException;

    @Override
    public void remove(MovementProperties movementProperties) {
        list.removeIf(model -> model.getMovementProperties().equals(movementProperties));
    }
}
