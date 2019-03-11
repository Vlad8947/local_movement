package com.local_movement.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.channels.SocketChannel;

@Getter
@EqualsAndHashCode
@ToString
public class MovementProperties {

    private String address;
    @Setter private SocketChannel socketChannel;
    @Setter private File file;
    private FileProperties fileProperties;
    private MovementType type;

    public MovementProperties(String address, FileProperties fileProperties, MovementType type) {
        this.address = address;
        this.fileProperties = fileProperties;
        this.type = type;
    }

    public MovementProperties(String address, SocketChannel socketChannel, FileProperties fileProperties, MovementType type) {
        this.address = address;
        this.socketChannel = socketChannel;
        this.fileProperties = fileProperties;
        this.type = type;
    }
}
