package com.local_movement.core.ui;

import com.local_movement.core.transfer.json.json_entity.FileProperties;

import java.nio.channels.SocketChannel;

public interface UITransfer {

    void acceptFile(FileProperties fileProperties, SocketChannel clientSocket);

}
