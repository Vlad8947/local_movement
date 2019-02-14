package com.local_movement.core.server;

import com.local_movement.core.AppProperties;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class SelectorLogic implements Runnable, Closeable {

    private Selector selector;

    public SelectorLogic() {
    }

    @Override
    public void run() {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            this.selector = selector;
            serverSocketInit(serverSocket);

            while (selector.isOpen()) {
                try {
                    selector.select();
                    for (SelectionKey key : selector.selectedKeys()) {
                        try {
                            if (key.isAcceptable()) {
                                accept(key);
                            }
                            if (key.isReadable()) {
                                read(key);
                            }
                            if (key.isWritable()) {
                                write(key);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serverSocketInit(ServerSocketChannel serverSocket) throws IOException {
        serverSocket.configureBlocking(false);
        serverSocket.bind(
                new InetSocketAddress(AppProperties.getPort()));
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket =
                (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocket.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {

    }

    private void write(SelectionKey key) throws IOException {

    }

    @Override
    public void close() throws IOException {
        selector.close();
    }
}
