package com.local_movement.core.model;

import com.local_movement.core.AppProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Getter
@EqualsAndHashCode
@ToString
public class MovementProperties {

    private String address;
    private InetSocketAddress inetAddress;
    @Setter private SocketChannel socketChannel;
    @Setter private File file;
    private FileProperties fileProperties;
    private MovementType type;

    public MovementProperties(String address, File file, FileProperties fileProperties, MovementType type) {
        this.address = address;
        inetAddress = new InetSocketAddress(address, AppProperties.getPort());
        this.file = file;
        this.fileProperties = fileProperties;
        this.type = type;
    }

    public MovementProperties(InetSocketAddress inetAddress, SocketChannel socketChannel, FileProperties fileProperties, MovementType type) {
        this.inetAddress = inetAddress;
        address = inetAddress.getAddress().getHostAddress();
        this.socketChannel = socketChannel;
        this.fileProperties = fileProperties;
        this.type = type;
    }
}
