package com.local_movement.pc_ui;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class Chooser {

    public static File chooseFile() {
        return new FileChooser().showOpenDialog(MainApp.getPrimaryStage());
    }

    public static File chooseDirectory() {
        return new DirectoryChooser().showDialog(MainApp.getPrimaryStage());
    }

}
