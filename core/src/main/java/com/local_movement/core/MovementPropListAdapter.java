package com.local_movement.core;

import com.local_movement.core.model.MovementProperties;

import java.io.IOException;

public interface MovementPropListAdapter {

    void add (MovementProperties movementProperties) throws IOException;

    void remove(MovementProperties movementProperties);

}
