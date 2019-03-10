package com.local_movement.core;

import lombok.Getter;
import lombok.Setter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class AppProperties {

    public enum MultiplierOfByte {
        B, KB, MB, GB
    }

    @Getter private static final String title = "Local Movement";
    @Getter private static int bufferSize = 1024;
    @Getter @Setter private static int port = 22022;


}
