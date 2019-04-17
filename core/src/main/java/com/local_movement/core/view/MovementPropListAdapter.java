package com.local_movement.core.view;

import com.local_movement.core.model.MovementProperties;

import java.io.IOException;

public interface MovementPropListAdapter {

    void add (MovementProperties movementProperties);

    void remove(MovementProperties movementProperties);

    boolean exist(MovementProperties movementProperties);

}
