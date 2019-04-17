package com.local_movement.core.view;

public interface DialogInterface {

    void error(String title, String header, String content);

    String textInput(String defaultText, String title, String header, String content) throws InterruptedException;

}
