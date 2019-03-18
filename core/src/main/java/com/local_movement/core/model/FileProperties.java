package com.local_movement.core.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@EqualsAndHashCode
@ToString
public class FileProperties {

    private String userName;
    private String fileName;
    private long fileLength;

    private FileProperties() {
    }

    public FileProperties(String userName, String fileName, long fileLength) {
        this.userName = userName;
        this.fileName = fileName;
        this.fileLength = fileLength;
    }
}
