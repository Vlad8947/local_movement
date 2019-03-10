package com.local_movement.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;

public class ConnectionsReceiver implements Closeable {

    private ServerSocketChannel server;
    private boolean closed = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Getter private List<MovementProperties> movementList;

    public ConnectionsReceiver(List<MovementProperties> movementList) {
        this.movementList = movementList;
    }

    public void receiveConnection() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            this.server = server;
            server.bind(new InetSocketAddress(AppProperties.getPort()));

            while (server.isOpen() && !closed) {
                SocketChannel socketChannel = server.accept();
                try {
                    FileProperties fileProperties = receiveFileProperties(socketChannel);
                    movementList.add(
                            new MovementProperties(socketChannel, fileProperties, MovementType.RECEIVE)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (!closed) {
                e.printStackTrace();
            }
        }
    }

    private FileProperties receiveFileProperties(SocketChannel socketChannel) throws IOException {
        ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferSize());
        ByteBuffer messageBuffer = ByteBuffer.allocate(Message.SIZE);
        byte[] message;
        byte[] data = new byte[0];
        int dataPosition;

        do {
            message = Message.read(socketChannel, messageBuffer);
            dataBuffer.clear();
            socketChannel.read(dataBuffer);
            dataPosition = data.length;
            data = Arrays.copyOf(data, data.length + dataBuffer.limit());
            System.arraycopy(dataBuffer.array(), 0, data, dataPosition, dataBuffer.limit());

        } while (!Arrays.equals(message, Message.END));

        return objectMapper.readValue(data, FileProperties.class);
    }

    @Override
    public void close() {
        try {
            if (server != null && server.isOpen()) {
                closed = true;
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
