package com.local_movement.core.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.*;
import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementType;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class ConnectionsReceiver extends RecursiveAction implements Closeable {

    private ServerSocketChannel server;
    private ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private boolean closed = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MovementPropListAdapter movementPropAdder;

    public ConnectionsReceiver(MovementPropListAdapter movementPropAdder) {
        this.movementPropAdder = movementPropAdder;
    }

    @Override
    public void compute() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            this.server = server;
            server.bind(new InetSocketAddress(AppProperties.getPort()));

            while (server.isOpen() && !closed) {
                SocketChannel socketChannel = server.accept();
                try {
                    FileProperties fileProperties = receiveFileProperties(socketChannel);
                    InetSocketAddress inetAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                    movementPropAdder.add(
                            new MovementProperties(inetAddress, socketChannel, fileProperties, MovementType.RECEIVE)
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
        byte[] message;
        byte[] data = new byte[0];
        int dataPosition;

        do {
            message = ChannelTransfer.clearReadGet(socketChannel, messageBuffer);
            ChannelTransfer.clearReadFlip(socketChannel, dataBuffer);
            dataPosition = data.length;
            data = Arrays.copyOf(data, data.length + dataBuffer.limit());
            System.arraycopy(dataBuffer.array(), 0, data, dataPosition, dataBuffer.limit());

        } while (!Arrays.equals(message, Message.END));

        return objectMapper.readValue(data, FileProperties.class);
    }

    public static void sendCancelConnectionMessage(MovementProperties movementProperties) {
        new Thread(() -> {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Message.LENGTH);
            SocketChannel socketChannel = movementProperties.getSocketChannel();
            try {
                if (socketChannel.isConnected()) {
                    ChannelTransfer.clearFlipWrite(Message.CANCEL, socketChannel, byteBuffer);
                }
                if (socketChannel.isOpen()) {
                    movementProperties.getSocketChannel().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
