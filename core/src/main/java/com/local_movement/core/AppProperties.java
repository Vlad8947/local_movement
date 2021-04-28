package com.local_movement.core;

import lombok.Getter;
import lombok.Setter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AppProperties {

    public static class Localisation {
        private static final String BUNDLE_NAME = "localization.Messages";
        public static final ResourceBundle messages = ResourceBundle.getBundle(BUNDLE_NAME);
    }

    public static final String TITLE = "Local Movement";
    @Getter
    private static int bufferLength = 8192;
    @Getter
    @Setter
    private static int port = 22022;

    @Getter
    private static ExecutorService executorService =
            Executors.newCachedThreadPool(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = Executors.defaultThreadFactory().newThread(r);
                    thread.setDaemon(true);
                    return thread;
                }
            });

}
