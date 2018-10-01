package com.v_v.local_movement.common.server;

import com.v_v.local_movement.common.UI.UIInterface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Server {

    private final int PORT = 20200;
    private final String ADDRESS = "localhost";
    private final int bufferCapacity = 512;
    private ByteBuffer buffer;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private UIInterface ui;

    public Server (UIInterface UIInterface) throws IOException {
        ui = UIInterface;
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(ADDRESS, PORT));
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void run() {
        while (true) {
            try {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        access();
                    }
                    else if (key.isReadable()) {
                        read(key);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void access() throws IOException {
        SocketChannel client = serverSocket.accept();
        InetSocketAddress clientAddress = (InetSocketAddress) client.getRemoteAddress();
        boolean uiAccess = ui.accessSocket(clientAddress.getHostName());
        if (uiAccess) {
            client.register(selector, SelectionKey.OP_READ);
        } else {
            client.close();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        int byteAmount = client.read(byteBuffer);
        byte[] byteArray = Arrays.copyOf(byteBuffer.array(), byteAmount);
        String message = new String(byteArray);
        boolean uiRead = ui.readSocket(message);
        if (uiRead) {
            
        }
    }
}
