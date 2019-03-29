package com.local_movement.core;

import lombok.Getter;
import lombok.Setter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppProperties {

    public enum MultiplierOfByte {
        B, KB, MB, GB
    }

    public static final String TITLE = "Local Movement";
    @Getter private static int bufferLength = 8192;
    @Getter @Setter private static int port = 22022;

}
