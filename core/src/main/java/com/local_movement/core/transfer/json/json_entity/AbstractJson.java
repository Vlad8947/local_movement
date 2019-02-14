package com.local_movement.core.transfer.json.json_entity;

import lombok.Getter;

public abstract class AbstractJson {

    @Getter
    private String jsonType;
    public static final String TYPE_FIELD_NAME = "jsonType";

    protected AbstractJson(String jsonType) {
        this.jsonType = jsonType;
    }
}
