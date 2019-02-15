package com.local_movement.core.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PossibleInterface {

    private StringProperty name;
    private StringProperty ip;

    public PossibleInterface(String name, String ip) {
        this.name = new SimpleStringProperty(name);
        this.ip = new SimpleStringProperty(ip);
    }
}
