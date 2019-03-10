package com.local_movement.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

@Getter
@EqualsAndHashCode
@ToString
public class MovementProperties {

    private SocketChannel socketChannel;
    @Setter private FileChannel fileChannel;
    private FileProperties fileProperties;
    private MovementType type;

    public MovementProperties(SocketChannel socketChannel, FileProperties fileProperties, MovementType type) {
        this.socketChannel = socketChannel;
        this.fileProperties = fileProperties;
        this.type = type;
    }
}
