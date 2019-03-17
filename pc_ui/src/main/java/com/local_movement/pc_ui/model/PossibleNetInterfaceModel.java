package com.local_movement.pc_ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
@EqualsAndHashCode
public class PossibleNetInterfaceModel {

    private StringProperty netInterfaceName = new SimpleStringProperty();
    private StringProperty ip = new SimpleStringProperty();

    public PossibleNetInterfaceModel(String netInterfaceName, String ip) {
        this.netInterfaceName.setValue(netInterfaceName);
        this.ip.setValue(ip);
    }
}
