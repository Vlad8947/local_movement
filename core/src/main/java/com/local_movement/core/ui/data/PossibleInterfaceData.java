package com.local_movement.core.ui.data;

import com.local_movement.core.ui.model.PossibleInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class PossibleInterfaceData {

    @Getter
    private static ObservableList<PossibleInterface> observableList = FXCollections.observableArrayList();

    public static void update() {
        observableList.clear();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() ||
                        !networkInterface.isUp() ||
                        networkInterface.isVirtual() ||
                        networkInterface.isPointToPoint()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if(Inet4Address.class == address.getClass()) {
                        PossibleInterface possibleInterface =
                                new PossibleInterface(networkInterface.getName(), address.getHostAddress());
                        observableList.add(possibleInterface);
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}
