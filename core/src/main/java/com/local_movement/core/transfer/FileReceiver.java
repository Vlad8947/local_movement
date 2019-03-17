package com.local_movement.core.transfer;

import com.local_movement.core.DialogInterface;
import com.local_movement.core.MovementPropListAdapter;
import com.local_movement.core.model.MovementProperties;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.RecursiveAction;

public class FileReceiver extends RecursiveAction implements Closeable {

    private MovementProperties movementProperties;
    private MovementPropListAdapter receiverListAdapter;
    private MovementPropListAdapter movementListAdapter;
    private SocketChannel socketChannel;
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
        try {

            File file = new File(directory, movementProperties.getFileProperties().getFileName());
            while (!file.createNewFile()) {
                String fileName = dialog.textInput(file.getName(),
                        "Create file error", "A file named \"" + file.getName() +
                                "\" already exists in the directory.",
                        "Enter file netInterfaceName");
                if (fileName == null) {
                    return;
                }
                file = new File(directory, fileName);
            }
            movementProperties.setFile(file);
            Path filePath = Paths.get(file.getPath());
            try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.APPEND)){

                receiverListAdapter.remove(movementProperties);
                movementListAdapter.add(movementProperties);

                //todo принятие файла


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {

    }

}
