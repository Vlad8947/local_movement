package com.local_movement.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Sender {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Runnable serv = Sender::receiveModel;
        Runnable client = () -> sendModel(
                new FileProperties("vlad8947", "my_file", 58585858L), "localhost");
        executorService.execute(serv);
        executorService.execute(client);
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        executorService.shutdown();
        System.out.println("mainF");
    }

    public static void sendModel(FileProperties fileProperties, String address) {
        int port = AppProperties.getPort();
        InetSocketAddress inetAddress = new InetSocketAddress(address, port);
        try (SocketChannel socketChannel = SocketChannel.open(inetAddress);
        ) {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] byteArray = objectMapper.writeValueAsBytes(fileProperties);
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("sendF");
    }

    public static void receiveModel() {
        try (ServerSocketChannel serverSocketChannel =
                     ServerSocketChannel.open()
                             .bind(new InetSocketAddress(
                                     AppProperties.getPort()));
             SocketChannel socketChannel = serverSocketChannel.accept();
        ) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(AppProperties.getBufferSize());
            byteBuffer.clear();
            StringBuilder jsonStringBuilder = new StringBuilder();
            while (socketChannel.read(byteBuffer) > -1) {
                byteBuffer.flip();
                byte[] byteArray = new byte[byteBuffer.limit()];
                byteBuffer.get(byteArray);
                jsonStringBuilder.append(new String(byteArray, "UTF-8"));
                byteBuffer.clear();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            FileProperties fileProperties =
                    objectMapper.readValue(
                            jsonStringBuilder.toString(), FileProperties.class);
            System.out.println(fileProperties.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("recF");
    }

}
