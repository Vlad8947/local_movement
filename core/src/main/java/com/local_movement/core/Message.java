package com.local_movement.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Message {

    public static final int SIZE = 3;
    public static final byte[] NONE = "non".getBytes();
    public static final byte[] END = "end".getBytes();

    public static byte[] read(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);
        return buffer.array();
    }

}
