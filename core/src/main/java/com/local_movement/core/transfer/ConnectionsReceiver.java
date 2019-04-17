package com.local_movement.core.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local_movement.core.*;
import com.local_movement.core.model.FileProperties;
import com.local_movement.core.model.MovementProperties;
import com.local_movement.core.model.MovementType;
import com.local_movement.core.view.MovementPropListAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.local_movement.core.transfer.ChannelTransfer.*;

public class ConnectionsReceiver implements Runnable, Closeable {

    private Logger logger = LogManager.getLogger(ConnectionsReceiver.class);

    private ServerSocketChannel server;
    private ByteBuffer dataBuffer = ByteBuffer.allocate(AppProperties.getBufferLength());
    private ByteBuffer messageBuffer = ByteBuffer.allocate(Message.LENGTH);
    private boolean closed = false;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MovementPropListAdapter receiveListAdapter;

    public ConnectionsReceiver(MovementPropListAdapter receiveListAdapter) {
        this.receiveListAdapter = receiveListAdapter;
    }

    @Override
    public void run() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            this.server = server;
            server.bind(new InetSocketAddress(AppProperties.getPort()));

            while (server.isOpen() && !closed) {
                SocketChannel socketChannel = server.accept();
                try {
                    FileProperties fileProperties = receiveFileProperties(socketChannel);
                    InetSocketAddress inetAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                    receiveListAdapter.add(
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
        logger.info("Read file prop");

        int dataLength = readInt(socketChannel, dataBuffer);
        byte[] data = new byte[dataLength];
        int dataPosition = 0;

        do {
            clearReadFlip(socketChannel, dataBuffer);
            System.arraycopy(dataBuffer.array(), 0, data, dataPosition, dataBuffer.limit());
            dataPosition += dataBuffer.limit();
        } while (dataPosition < dataLength-1);

        return objectMapper.readValue(data, FileProperties.class);
    }

    public static void sendCancelConnectionMessage(MovementProperties movementProperties) {
        new Thread(() -> {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Message.LENGTH);
            SocketChannel socketChannel = movementProperties.getSocketChannel();
            try {
                if (socketChannel.isConnected()) {
                    ChannelTransfer.clearPutFlipWriteFB(Message.CANCEL, socketChannel, byteBuffer);
                }
                if (socketChannel.isOpen()) {
                    movementProperties.getSocketChannel().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                byteBuffer.clear();
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
