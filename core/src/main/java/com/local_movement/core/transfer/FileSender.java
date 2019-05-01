package com.local_movement.core.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.*;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementStatus;
import com.local_movement.core.view.DialogInterface;
import com.local_movement.core.view.MovementPropListAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.nio.ch.Net;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static com.local_movement.core.transfer.ChannelTransfer.*;
import static com.local_movement.core.AppProperties.Localisation.messages;

public class FileSender implements Runnable, Closeable {

    private Logger logger = LogManager.getLogger(FileSender.class);

    private MovementProperties movementProperties;
    private MovementPropListAdapter movementListAdapter;
    private ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private SocketChannel socketChannel;
    private DialogInterface dialog;

    public FileSender(MovementProperties movementProperties, DialogInterface dialog, MovementPropListAdapter movementListAdapter) {
        this.movementProperties = movementProperties;
        this.dialog = dialog;
        this.movementListAdapter = movementListAdapter;
    }

    @Override
    public void run() {
        String errorTitle = messages.getString("dialog.send_file_error");
        logger.info("Start");
        try (SocketChannel socketChannel = SocketChannel.open()) {
            this.socketChannel = socketChannel;

            if (!addressIsValid()) return;
            if (addressIsUse()) return;
            movementProperties.setCloseable(this);
            connect();
            addTaskInMovementTable();
            sendFileProperties();
            if (!confirmSend()) return;
            sendFile();
            readFinishMessage();

        } catch (JsonProcessingException e) {
            String header = messages.getString("dialog.json_error");
            logger.warn(header);
            dialog.error(errorTitle, header, null);
        }
        catch (IOException e) {
            if (socketChannel.isOpen()) {
                String header = messages.getString("dialog.connection_error") + " " + movementProperties.toString();
                logger.warn(header);
                dialog.error(errorTitle, header, null);
            }
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        } finally {
            logger.info("Finish");
            movementProperties.setStatus(MovementStatus.FINISH);
            close();
        }
    }

    private boolean addressIsValid() {
        try {
            logger.info("Check address " + movementProperties.getAddress());
            Net.checkAddress(movementProperties.getInetAddress());
        } catch (IllegalArgumentException e) {
            String errorTitle = messages.getString("dialog.send_file_error");
            String header = messages.getString("dialog.invalid_address") + " " + movementProperties.getAddress();
            logger.warn(header);
            dialog.error(errorTitle, header, null);
            return false;
        }
        return true;
    }

    private boolean addressIsUse() {
        MovementProperties existProperties = movementListAdapter.get(movementProperties);
        if (existProperties != null &&
                existProperties.getStatus() != MovementStatus.FINISH) {
            String title = messages.getString("dialog.address_valid.title");
            String header = messages.getString("dialog.address_valid.header");
            String content = null;
            dialog.error(title, header, content);
            return true;
        }
        return false;
    }

    private void connect() throws IOException {
        logger.info("Connecting");
        socketChannel.connect(movementProperties.getInetAddress());
    }

    private void addTaskInMovementTable() {
        movementProperties.setStatus(MovementStatus.WAITING_FOR_CONFORMATION);
        movementListAdapter.add(movementProperties);
    }

    private void sendFileProperties() throws IOException {
        logger.info("Send file properties");

        byte[] data = new ObjectMapper().writeValueAsBytes(movementProperties.getFileProperties());
        byte[] tempData;
        int bufferLength = AppProperties.getBufferLength();

        int position = 0;
        int limit;

        writeInt(socketChannel, dataBuffer, data.length);

        do {
            limit = position + bufferLength;
            if (limit > data.length) {
                limit = data.length;
            }
            tempData = Arrays.copyOfRange(data, position, limit);
            clearPutFlipWriteFB(tempData, socketChannel, dataBuffer);
            position += dataBuffer.limit();

        } while (position < data.length-1);

    }

    private boolean confirmSend() throws IOException {
        logger.info("Waiting confirmSend message");

        byte[] message;
        message = clearReadGetFB(socketChannel, messageBuffer);
        if (Arrays.equals(message, Message.CANCEL)) {
            logger.info("Read CANCEL");
            movementProperties.setStatus(MovementStatus.CANCELED);
            return false;
        } else if (Arrays.equals(message, Message.CONFIRM)) {
            logger.info("Read CONFIRM");
            return true;
        } else {
            logger.warn("Unknown message: " + new String(message));
        }
        return false;
    }

    private void sendFile() throws IOException {
        logger.info("Send file");
        movementProperties.setStatus(MovementStatus.MOVE);
        File file = movementProperties.getFile();
        try (FileChannel fileChannel =
                     FileChannel.open(file.toPath(), StandardOpenOption.READ);) {

            long partsAmount = file.length() / AppProperties.getBufferLength();
            int lastPartLength = (int) (file.length() % AppProperties.getBufferLength());

            for (int i = 0; i < partsAmount; i++) {
                clearReadFB(fileChannel, dataBuffer);
                flipWriteFB(socketChannel, dataBuffer);
                movementProperties.addDoneBytes(dataBuffer.limit());
            }
            if (lastPartLength != 0) {
                sendOneMorePartOfFile(lastPartLength, fileChannel);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            logger.info("End send file");
        }
    }

    private void sendOneMorePartOfFile(int lastPartLength, FileChannel fileChannel) throws IOException {
        clearReadWithLimitFB(fileChannel, dataBuffer, lastPartLength);
        flipWriteFB(socketChannel, dataBuffer);
        movementProperties.addDoneBytes(dataBuffer.limit());
    }

    private void readFinishMessage() throws IOException {
        logger.info("Wait finish message");
        clearReadFB(socketChannel, messageBuffer);
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
