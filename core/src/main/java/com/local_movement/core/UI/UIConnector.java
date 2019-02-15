package com.local_movement.core.ui;

public class UIConnector {

    private static UITransfer UI_TRANSFER = null;
    private static UISocket UI_SOCKET = null;

    public UIConnector() {
    }

    public static void setUiTransfer(UITransfer uiTransfer) {
        if (UI_TRANSFER == null) {
            UI_TRANSFER = uiTransfer;
        }
    }

    public static void setUiSocket(UISocket uiSocket) {
        if (UI_SOCKET == null) {
            UI_SOCKET = uiSocket;
        }
    }

    public static UITransfer UI_TRANSFER() {
        return UI_TRANSFER;
    }

    public static UISocket UI_SOCKET() {
        return UI_SOCKET;
    }
}
