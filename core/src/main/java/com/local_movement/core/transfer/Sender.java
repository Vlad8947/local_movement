package com.local_movement.core.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.*;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.nio.ch.Net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class Sender extends RecursiveAction implements Closeable {

    private Logger logger = LogManager.getLogger(Sender.class);

    private MovementProperties movementProperties;
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private SocketChannel socketChannel;
    private DialogInterface dialog;
    private String errorTitle = "Send file error";

    public Sender(MovementProperties movementProperties, DialogInterface dialog) {
        this.movementProperties = movementProperties;
        this.dialog = dialog;
    }

    @Override
    protected void compute() {
        logger.info("Start");
        try (SocketChannel socketChannel = SocketChannel.open()) {
            this.socketChannel = socketChannel;

            if (!addressValid()) return;
            logger.info("Connecting");
            socketChannel.connect(movementProperties.getInetAddress());
            sendFileProperties();
            if (!accept()) return;
            sendFile();

            //Finish message
            logger.info("Wait finish message");
            ChannelTransfer.clearRead(socketChannel, messageBuffer);

        } catch (JsonProcessingException e) {
            String header = "Json processing error";
            logger.warn(header);
            dialog.error(errorTitle, header, null);
        }
        catch (IOException e) {
            String header = "Connection error: " + movementProperties.toString();
            logger.warn(header);
            dialog.error(errorTitle, header, null);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        } finally {
            logger.info("Finish");
            close();
        }
    }

    private boolean accept() throws IOException {
        logger.info("Waiting accept message");

        byte[] message;
        message = ChannelTransfer.clearReadGet(socketChannel, messageBuffer);
        if (Arrays.equals(message, Message.CANCEL)) {
            logger.info("Read CANCEL");
            movementProperties.setStatus(MovementStatus.CANCELED);
            return false;
        } else if (Arrays.equals(message, Message.ACCEPT)) {
            logger.info("Read ACCEPT");
            return true;
        }
        return false;
    }

    private boolean addressValid() {
        try {
            logger.info("Check address " + movementProperties.getAddress());
            Net.checkAddress(movementProperties.getInetAddress());
        } catch (IllegalArgumentException e) {
            String header = "Invalid address: " + movementProperties.getAddress();
            logger.warn(header);
            dialog.error(errorTitle, header, null);
            return false;
        }
        return true;
    }

    private void sendFile() throws IOException {
        logger.info("Send file");
        try (FileChannel fileChannel = FileChannel.open(movementProperties.getFile().toPath(), StandardOpenOption.READ);) {
            while (ChannelTransfer.clearRead(fileChannel, dataBuffer) == AppProperties.getBufferLength()) {
                if (movementProperties.getStatus().equals(MovementStatus.PAUSE)) {
                    //todo pause
                }
                ChannelTransfer.clearPut(messageBuffer, Message.NONE);
                ChannelTransfer.flipWriteWithMessage(socketChannel, messageBuffer, dataBuffer);
                //todo listen a pause message
            }
            ChannelTransfer.clearPut(messageBuffer, Message.END);
            ChannelTransfer.flipWriteWithMessage(socketChannel, messageBuffer, dataBuffer);

        } catch (IOException e) {
            throw e;
        } finally {
            logger.info("End send file");
        }
    }

    private void sendFileProperties() throws IOException {
        logger.info("Send file properties");

        byte[] data = new ObjectMapper().writeValueAsBytes(movementProperties.getFileProperties());
        int dataPosition = 0;
        byte[] tempData;

        while ((data.length - dataPosition) > AppProperties.getBufferLength()) {
            tempData = Arrays.copyOfRange(data, dataPosition, dataPosition + AppProperties.getBufferLength());
            ChannelTransfer.flipWriteWithMessage(Message.NONE, tempData, socketChannel, dataBuffer);
            dataPosition += AppProperties.getBufferLength();
        }
        tempData = Arrays.copyOfRange(data, dataPosition, data.length);
        ChannelTransfer.flipWriteWithMessage(Message.END, tempData, socketChannel, dataBuffer);
    }

    @Override
    public void close() {
        if (socketChannel.isOpen()) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
