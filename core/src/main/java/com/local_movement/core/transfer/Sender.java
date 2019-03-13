package com.local_movement.core.transfer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.*;
import com.local_movement.core.model.MovementProperties;
import sun.nio.ch.Net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class Sender extends RecursiveAction implements Closeable {

    private MovementProperties movementProperties;
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private ByteBuffer buffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private SocketChannel socketChannel;
    private DialogInterface dialog;
    private String errorTitle = "Send file error";


    public Sender(MovementProperties movementProperties, DialogInterface dialog) {
        this.movementProperties = movementProperties;
        this.dialog = dialog;
    }

    @Override
    protected void compute() {
        try (SocketChannel socketChannel = SocketChannel.open();) {
            this.socketChannel = socketChannel;
            try {
                Net.checkAddress(movementProperties.getInetAddress());
            } catch (Exception e) {
                String header = "Error! Invalid address";
                dialog.error(errorTitle, header, null);
                return;
            }
            try {
                socketChannel.connect(movementProperties.getInetAddress());
            } catch (Exception e) {
                String header = "Error! Connection error";
                dialog.error(errorTitle, header, null);
                return;
            }
            try {
                sendFileProperties();
            } catch (Exception e) {
                String header = "Error! Send file properties error";
                dialog.error(errorTitle, header, null);
                return;
            }

            synchronized (this) {
                this.wait(500);
            }
        } catch (Exception e) {
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
