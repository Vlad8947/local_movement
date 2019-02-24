package com.local_movement.core;

import lombok.Getter;
import lombok.Setter;


public class AppProperties {

    @Getter private static final String title = "Local Movement";
    @Getter private static int bufferSize = 1024;
    @Getter @Setter private static int port = 22022;

    static {

    }

    public AppProperties() {
    }
}
