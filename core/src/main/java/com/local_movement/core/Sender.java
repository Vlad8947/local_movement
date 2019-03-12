package com.local_movement.core;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class Sender extends RecursiveAction implements Closeable {

    private MovementProperties movementProperties;
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private ByteBuffer buffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private SocketChannel socketChannel;

    public Sender(MovementProperties movementProperties) {
        this.movementProperties = movementProperties;
    }

    @Override
    protected void compute() {
        try (SocketChannel socketChannel = SocketChannel.open();) {
            this.socketChannel = socketChannel;
            InetSocketAddress address =
                    new InetSocketAddress(movementProperties.getAddress(), AppProperties.getPort());
            socketChannel.connect(address);
            sendFileProperties();

            synchronized (this) {
                this.wait(500);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {

    }

    private void sendFileProperties() throws IOException {
        byte[] data = new ObjectMapper().writeValueAsBytes(
                movementProperties.getFileProperties());
        int dataPosition = 0;
        byte[] tempData;

        while ((data.length - dataPosition) > AppProperties.getBufferLength()) {
            tempData = Arrays.copyOfRange(data, dataPosition, dataPosition + AppProperties.getBufferLength());
            SocketTransfer.writeWithMessage(Message.NONE, tempData, socketChannel, buffer);
            dataPosition += AppProperties.getBufferLength();
        }
        tempData = Arrays.copyOfRange(data, dataPosition, data.length);
        SocketTransfer.writeWithMessage(Message.END, tempData, socketChannel, buffer);
    }
}
