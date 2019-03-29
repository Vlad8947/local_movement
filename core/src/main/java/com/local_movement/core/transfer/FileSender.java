package com.local_movement.core.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.*;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementStatus;
import com.local_movement.core.view.DialogInterface;
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

public class FileSender implements Runnable, Closeable {

    private Logger logger = LogManager.getLogger(FileSender.class);

    private MovementProperties movementProperties;
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private SocketChannel socketChannel;
    private DialogInterface dialog;
    private String errorTitle = "Send file error";

    public FileSender(MovementProperties movementProperties, DialogInterface dialog) {
        this.movementProperties = movementProperties;
        this.dialog = dialog;
    }

    @Override
    public void run() {
        logger.info("Start");
        try (SocketChannel socketChannel = SocketChannel.open()) {
            this.socketChannel = socketChannel;

            if (!addressValid()) return;
            connect();
            sendFileProperties();
            if (!accept()) return;
            sendFile();
            readFinishMessage();

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

    private void connect() throws IOException {
        logger.info("Connecting");
        socketChannel.connect(movementProperties.getInetAddress());
    }

    private void sendFileProperties() throws IOException {
        logger.info("Send file properties");

        byte[] data = new ObjectMapper().writeValueAsBytes(movementProperties.getFileProperties());
        int dataPosition = 0;
        byte[] tempData;

        while ((data.length - dataPosition) >= AppProperties.getBufferLength()) {
            tempData = Arrays.copyOfRange(data, dataPosition, dataPosition + AppProperties.getBufferLength());
            ChannelTransfer.flipWriteWithMessage(Message.NONE, tempData, socketChannel, dataBuffer);
            dataPosition += AppProperties.getBufferLength();
        }
        if ((data.length - dataPosition) > 0) {
            logger.info("Send one_more message");
            tempData = Arrays.copyOfRange(data, dataPosition, data.length);
            ChannelTransfer.flipWriteWithMessage(Message.ONE_MORE, tempData, socketChannel, dataBuffer);

        } else {
            logger.info("Send end message");
            ChannelTransfer.clearFlipWrite(Message.END, socketChannel, messageBuffer);
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

    private void sendFile() throws IOException {
        logger.info("Send file");
        try (FileChannel fileChannel =
                     FileChannel.open(movementProperties.getFile().toPath(), StandardOpenOption.READ);) {
            while (ChannelTransfer.clearRead(fileChannel, dataBuffer) == AppProperties.getBufferLength()) {
                if (movementProperties.getStatus().equals(MovementStatus.PAUSE)) {
                    //todo pause
                }
                ChannelTransfer.clearPut(messageBuffer, Message.NONE);
                ChannelTransfer.flipWriteWithMessage(socketChannel, messageBuffer, dataBuffer);
                //todo listen a pause message
            }
            if (dataBuffer.position() != 0) {
                logger.info("Send one-more message");
                ChannelTransfer.clearPut(messageBuffer, Message.ONE_MORE);
                ChannelTransfer.flipWriteWithMessage(socketChannel, messageBuffer, dataBuffer);
            } else {
                logger.info("Send end message");
                ChannelTransfer.clearFlipWrite(Message.END, socketChannel, messageBuffer);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            logger.info("End send file");
        }
    }

    private void readFinishMessage() throws IOException {
        //Finish message
        logger.info("Wait finish message");
        ChannelTransfer.clearRead(socketChannel, messageBuffer);
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
