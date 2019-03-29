package com.local_movement.core.transfer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelTransfer {

    public static <T extends ByteChannel> int clearRead(T channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        return channel.read(buffer);
    }

    public static <T extends ByteChannel> void clearReadFlip(T channel, ByteBuffer buffer) throws IOException {
        clearRead(channel, buffer);
        buffer.flip();
    }

    public static <T extends ByteChannel> byte[] clearReadGet(T channel, ByteBuffer buffer) throws IOException {
        clearRead(channel, buffer);
        return buffer.array();
    }

    public static void clearPut(ByteBuffer buffer, byte[] data) {
        buffer.clear();
        buffer.put(data);
    }

    public static <T extends ByteChannel> void flipWrite(T channel, ByteBuffer buffer) throws IOException {
        buffer.flip();
        channel.write(buffer);
    }

    public static <T extends ByteChannel> void clearFlipWrite(byte[] data, T channel, ByteBuffer buffer)
            throws IOException {
        clearPut(buffer, data);
        flipWrite(channel, buffer);
    }

    public static <T extends ByteChannel> void flipWriteWithMessage(byte[] message, byte[] data, T channel,
                                                                    ByteBuffer buffer)
            throws IOException {
        clearFlipWrite(message, channel, buffer);
        clearFlipWrite(data, channel, buffer);
    }

    public static <T extends ByteChannel> void flipWriteWithMessage(T channel, ByteBuffer messageBuffer,
                                                                    ByteBuffer dataBuffer)
            throws IOException{
        flipWrite(channel, messageBuffer);
        flipWrite(channel, dataBuffer);
    }

}
