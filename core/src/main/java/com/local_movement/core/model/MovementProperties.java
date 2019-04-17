package com.local_movement.core.model;

import com.local_movement.core.AppProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Getter
@EqualsAndHashCode
@ToString(of = {"address", "fileProperties"})
public class MovementProperties implements Closeable {

    private String address;
    private InetSocketAddress inetAddress;
    @Setter private SocketChannel socketChannel;
    @Setter private File file;
    private FileProperties fileProperties;
    private MovementType type;
    private long doneBytes = 0;
    @Setter private MovementStatus status = MovementStatus.MOVE;
    @Setter private Closeable closeable;

    public MovementProperties(String address, File file, FileProperties fileProperties, MovementType type) {
        this.address = address;
        inetAddress = new InetSocketAddress(address, AppProperties.getPort());
        this.file = file;
        this.fileProperties = fileProperties;
        this.type = type;
    }

    public MovementProperties(InetSocketAddress inetAddress, SocketChannel socketChannel,
                              FileProperties fileProperties, MovementType type) {
        this.inetAddress = inetAddress;
        address = inetAddress.getAddress().getHostAddress();
        this.socketChannel = socketChannel;
        this.fileProperties = fileProperties;
        this.type = type;
    }

    public void addDoneBytes(int number) {
        doneBytes += number;
    }


    @Override
    public void close() throws IOException {
        closeable.close();
    }
}
