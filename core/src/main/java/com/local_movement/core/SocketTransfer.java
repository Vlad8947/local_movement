package com.local_movement.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketTransfer {

    public static void clearRead(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();

        System.out.println(socketChannel.read(buffer));
    }

    public static void clearReadFlip(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        clearRead(socketChannel, buffer);
        buffer.flip();
    }

    public static byte[] clearReadGet(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        clearRead(socketChannel, buffer);
        return buffer.array();
    }

    public static void clearPut(ByteBuffer buffer, byte[] data) {
        buffer.clear();
        buffer.put(data);
    }

    public static void flipWrite(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        buffer.flip();
        socketChannel.write(buffer);
    }

    public static void clearPutFlipWrite(byte[] data, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        clearPut(buffer, data);
        flipWrite(socketChannel, buffer);
    }

    public static void writeWithMessage(byte[] message, byte[] data, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        SocketTransfer.clearPutFlipWrite(message, socketChannel, buffer);
        SocketTransfer.clearPutFlipWrite(data, socketChannel, buffer);
    }

}
