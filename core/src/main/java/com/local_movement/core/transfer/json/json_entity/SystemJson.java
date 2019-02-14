package com.local_movement.core.transfer.json.json_entity;

import lombok.Getter;

public class SystemJson extends AbstractJson {

    @Getter
    private String command;

    public SystemJson(String command) {
        super(JsonType.SYSTEM_COMMAND);
        this.command = command;
    }
}
