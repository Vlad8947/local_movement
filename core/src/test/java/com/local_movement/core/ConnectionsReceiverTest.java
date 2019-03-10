package com.local_movement.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionsReceiverTest {

    private ConnectionsReceiver connectionsReceiver;

    @BeforeEach
    void setUp() {
        connectionsReceiver = new ConnectionsReceiver(new ArrayList<>());
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

            Thread thread = new Thread(connectionsReceiver::receiveConnection);
            thread.start();
            String localHost = "localHost";
            socketChannel.connect(new InetSocketAddress(localHost, AppProperties.getPort()));
            ByteBuffer buffer = ByteBuffer.allocate(AppProperties.getBufferSize());

            byte[] data = new ObjectMapper().writeValueAsBytes(originalFileProp);
            long dataPosition = data.length;

            while (dataPosition > AppProperties.getBufferSize()) {
                sendData(socketChannel, buffer, Message.NONE, data);
                dataPosition -= AppProperties.getBufferSize();
            }
            buffer.clear();
            buffer.put(Message.END);
            buffer.flip();
            socketChannel.write(buffer);

            buffer.clear();
            buffer.put(data);
            buffer.flip();
            socketChannel.write(buffer);

            int first = 0;
            List<MovementProperties> movementList = connectionsReceiver.getMovementList();
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

    private void sendData(SocketChannel socketChannel, ByteBuffer buffer, byte[] message, byte[] data) throws IOException {
        buffer.clear();
        buffer.put(message);
        buffer.flip();
        socketChannel.write(buffer);

        buffer.clear();
        buffer.put(data);
        buffer.flip();
        socketChannel.write(buffer);
    }

}