package com.local_movement.pc_ui.model;

import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ReceiveData {

    private StringProperty fromIP;
    private StringProperty login;
    private StringProperty fileName;
    private StringProperty size;

    public ReceiveData() {
    }


}
