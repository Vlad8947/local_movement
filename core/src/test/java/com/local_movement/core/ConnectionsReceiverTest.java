package com.local_movement.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.transfer.ConnectionsReceiver;
import com.local_movement.core.transfer.Message;
import com.local_movement.core.transfer.ChannelTransfer;
import com.local_movement.core.view.MovementPropListAdapter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionsReceiverTest {

    private ExecutorService executorService;
    private ConnectionsReceiver connectionsReceiver;
    private List<MovementProperties> movementList;
    private MovementPropListAdapter adder = new MovementPropListAdapter() {
        @Override
        public void add(MovementProperties movementProperties) throws IOException {
            movementList.add(movementProperties);
        }

        @Override
        public void remove(MovementProperties movementProperties) {
            movementList.remove(movementProperties);
        }

    };

    @BeforeEach
    void setUp() {
        executorService = Executors.newCachedThreadPool();
        movementList = new ArrayList<>();
        connectionsReceiver = new ConnectionsReceiver(adder);
    }

    @AfterEach
    void tearDown() {
        connectionsReceiver.close();
        executorService.shutdown();
    }

    @Test
    void receiveConnection() {
        String userName = "vlad";
        String fileName = "Captain Marvel";
        long fileSize = 123123123;
        FileProperties originalFileProp = new FileProperties(userName, fileName, fileSize);

        try (SocketChannel socketChannel = SocketChannel.open();) {
            executorService.execute(connectionsReceiver);
//            connectionsReceiver.fork();
            String localHost = "localHost";
            socketChannel.connect(new InetSocketAddress(localHost, AppProperties.getPort()));
            ByteBuffer buffer = ByteBuffer.allocate(AppProperties.getBufferLength());

            byte[] data = new ObjectMapper().writeValueAsBytes(originalFileProp);
            int dataPosition = 0;
            byte[] tempData;

            while ((data.length - dataPosition) > AppProperties.getBufferLength()) {
                ChannelTransfer.clearFlipWrite(Message.NONE, socketChannel, buffer);
                tempData = Arrays.copyOfRange(data, dataPosition, dataPosition + AppProperties.getBufferLength());
                ChannelTransfer.clearFlipWrite(tempData, socketChannel, buffer);
                dataPosition += AppProperties.getBufferLength();
            }
            ChannelTransfer.clearFlipWrite(Message.END, socketChannel, buffer);
            tempData = Arrays.copyOfRange(data, dataPosition, data.length);
            ChannelTransfer.clearFlipWrite(tempData, socketChannel, buffer);

            int first = 0;
            Object blockKey = new Object();
            while (movementList.isEmpty()) {
                synchronized (blockKey) {
                    blockKey.wait(10);
                }
            }
            FileProperties expectedFileProp = movementList.get(first).getFileProperties();
            assertEquals(expectedFileProp, originalFileProp);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}