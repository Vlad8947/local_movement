package com.local_movement.core.view;

import com.local_movement.core.model.MovementProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.IOException;

@Getter
@EqualsAndHashCode
public abstract class ViewModel<E extends ViewModel> {

    protected MovementProperties movementProperties;

    public ViewModel(MovementProperties movementProperties) {
        this.movementProperties = movementProperties;
    }

}
