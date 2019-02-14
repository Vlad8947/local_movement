package com.local_movement.core.transfer;

import com.local_movement.core.AppProperties;
import com.local_movement.core.transfer.file.FileReaderFromSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Transfer {

    private ByteBuffer byteBuffer = ByteBuffer.allocate(AppProperties.getBufferSize());

    public Transfer() {

    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        byte transferType = readType(socketChannel);

        if (transferType == TransferConstant.FILE) {

        }
        if (transferType == TransferConstant.JSON) {

        }
    }

    private byte readType(SocketChannel socketChannel) throws IOException {
        byteBuffer.clear();
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        return byteBuffer.get();
    }

}
