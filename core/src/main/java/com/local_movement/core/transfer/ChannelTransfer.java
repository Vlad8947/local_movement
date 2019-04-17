package com.local_movement.core.transfer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelTransfer {

    public static <T extends ByteChannel> void clearReadFB(T channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        while (buffer.hasRemaining()) {
            channel.read(buffer);
        }
    }

    public static <T extends ByteChannel> void clearRead(T channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        channel.read(buffer);
    }

    public static <T extends ByteChannel> void clearReadWithLimitFB(T channel, ByteBuffer buffer, int limit) throws IOException {
        buffer.clear();
        buffer.limit(limit);
        while (buffer.hasRemaining()) {
            channel.read(buffer);
        }
    }

    public static <T extends ByteChannel> void clearReadFlip(T channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        channel.read(buffer);
        buffer.flip();
    }

    public static <T extends ByteChannel> byte[] clearReadGetFB(T channel, ByteBuffer buffer) throws IOException {
        clearReadFB(channel, buffer);
        return buffer.array();
    }

    public static void clearPut(ByteBuffer buffer, byte[] data) {
        buffer.clear();
        buffer.put(data);
    }

    public static <T extends ByteChannel> void flipWriteFB(T channel, ByteBuffer buffer) throws IOException {
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    public static <T extends ByteChannel> void clearPutFlipWriteFB(byte[] data, T channel, ByteBuffer buffer)
            throws IOException {
        clearPut(buffer, data);
        flipWriteFB(channel, buffer);
    }

    public static <T extends ByteChannel> void writeInt(T channel, ByteBuffer buffer, int number) throws IOException {
        buffer.clear();
        buffer.putInt(number);
        flipWriteFB(channel, buffer);
    }

    public static <T extends ByteChannel> int readInt(T channel, ByteBuffer buffer) throws IOException {
        clearReadWithLimitFB(channel, buffer, Integer.BYTES);
        buffer.flip();
        return buffer.getInt();
    }

}
