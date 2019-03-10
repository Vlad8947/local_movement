package com.local_movement.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class FileProperties {

    private String userName;
    private String fileName;
    private long fileSize;

    private FileProperties() {
    }

    public FileProperties(String userName, String fileName, long fileSize) {
        this.userName = userName;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}
