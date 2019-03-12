package com.local_movement.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionsReceiverTest {

    private ConnectionsReceiver connectionsReceiver;
    List<MovementProperties> movementList;

    @BeforeEach
    void setUp() {
        movementList = new ArrayList<>();
        ConnectionsReceiver.MovementPropAdder adder = movementList::add;
        connectionsReceiver = new ConnectionsReceiver(adder);
    }

    @AfterEach
    void tearDown() {
        connectionsReceiver.close();
    }

    @Test
    void receiveConnection() {
        String userName = "vlad";
        String fileName = "Captain Marvel";
        long fileSize = 123123123;
        FileProperties originalFileProp = new FileProperties(userName, fileName, fileSize);

        try (SocketChannel socketChannel = SocketChannel.open();) {
            connectionsReceiver.fork();
            String localHost = "localHost";
            socketChannel.connect(new InetSocketAddress(localHost, AppProperties.getPort()));
            ByteBuffer buffer = ByteBuffer.allocate(AppProperties.getBufferLength());

            byte[] data = new ObjectMapper().writeValueAsBytes(originalFileProp);
            int dataPosition = 0;
            byte[] tempData;

            while ((data.length - dataPosition) > AppProperties.getBufferLength()) {
                SocketTransfer.clearPutFlipWrite(Message.NONE, socketChannel, buffer);
                tempData = Arrays.copyOfRange(data, dataPosition, dataPosition + AppProperties.getBufferLength());
                SocketTransfer.clearPutFlipWrite(tempData, socketChannel, buffer);
                dataPosition += AppProperties.getBufferLength();
            }
            SocketTransfer.clearPutFlipWrite(Message.END, socketChannel, buffer);
            tempData = Arrays.copyOfRange(data, dataPosition, data.length);
            SocketTransfer.clearPutFlipWrite(tempData, socketChannel, buffer);

            int first = 0;
            Object blockKey = new Object();
            while (movementList.isEmpty()) {
                synchronized (blockKey) {
                    blockKey.wait(10);
                }
            }
            FileProperties expectedFileProp = movementList.get(first).getFileProperties();
            System.out.println(expectedFileProp);
            assertEquals(expectedFileProp, originalFileProp);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}