package com.local_movement.core;

import lombok.Getter;
import lombok.Setter;

public class AppProperties {

    @Getter private static int bufferSize = 1024;
    @Setter
    private static int port = 22022;

    public AppProperties() {
    }
}
