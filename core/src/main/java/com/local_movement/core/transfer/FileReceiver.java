package com.local_movement.core.transfer;

import com.local_movement.core.AppProperties;
import com.local_movement.core.DialogInterface;
import com.local_movement.core.MovementPropListAdapter;
import com.local_movement.core.model.MovementProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class FileReceiver extends RecursiveAction implements Closeable {

    private Logger logger = LogManager.getLogger(FileReceiver.class);

    private MovementProperties movementProperties;
    private MovementPropListAdapter receiverListAdapter;
    private MovementPropListAdapter movementListAdapter;
    private SocketChannel socketChannel;
    private ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private FileChannel fileChannel;
    private String directory;
    private DialogInterface dialog;

    public FileReceiver(MovementProperties movementProperties, String directory, DialogInterface dialog,
                        MovementPropListAdapter receiverListAdapter, MovementPropListAdapter movementListAdapter) {
        this.movementProperties = movementProperties;
        this.directory = directory;
        this.dialog = dialog;
        this.receiverListAdapter = receiverListAdapter;
        this.movementListAdapter = movementListAdapter;
        socketChannel = movementProperties.getSocketChannel();
    }

    @Override
    public void compute() {
        logger.info("Start");
        try {
            File file = new File(directory, movementProperties.getFileProperties().getFileName());
            logger.info("Create receive file");
            while (!file.createNewFile()) {
                String fileName = dialog.textInput(file.getName(),
                        "Create file error", "A file named \"" + file.getName() +
                                "\" already exists in the directory.",
                        "Enter file netInterfaceName");
                if (fileName == null) {
                    logger.info("File name equals null, send CANCEL");
                    ChannelTransfer.clearFlipWrite(Message.CANCEL, socketChannel, messageBuffer);
                    return;
                }
                file = new File(directory, fileName);
            }
            movementProperties.setFile(file);
            receiverListAdapter.remove(movementProperties);
            movementListAdapter.add(movementProperties);
            receiveFile();

            //Finish message
            logger.info("Write finish message");
            ChannelTransfer.clearFlipWrite(Message.FINISH, socketChannel, messageBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logger.info("Finish");
        }
    }

    private void receiveFile() {
        logger.info("Receive file");
        try (FileChannel fileChannel = FileChannel.open(movementProperties.getFile().toPath(), StandardOpenOption.APPEND)){
            ChannelTransfer.clearFlipWrite(Message.ACCEPT, socketChannel, messageBuffer);

            while (!Arrays.equals(Message.END, ChannelTransfer.clearReadGet(socketChannel, messageBuffer))) {
                ChannelTransfer.clearRead(socketChannel, dataBuffer);
                ChannelTransfer.flipWrite(fileChannel, dataBuffer);
            }
            ChannelTransfer.clearRead(socketChannel, dataBuffer);
            ChannelTransfer.flipWrite(fileChannel, dataBuffer);

        } catch (IOException e) {
            logger.warn(e);
            e.printStackTrace();
        }
        logger.info("End receive file");
    }

    @Override
    public void close() throws IOException {

    }

}
