package com.local_movement.core.transfer.json.json_entity;

import lombok.Getter;

@Getter
public class FileProperties extends AbstractJson {

    private String name;
    private long size;

    public FileProperties(String name, long size) {
        super(JsonType.FILE_PROPERTIES);
        this.name = name;
        this.size = size;
    }
}
