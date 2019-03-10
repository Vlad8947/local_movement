package com.local_movement.core.possible_network_interface;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkInterfaceFinder {

    public static void find(NetworkInterfaceAdder adder) {
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
                Enumeration<InetAddress> addressEnum = networkInterface.getInetAddresses();
                String interfaceName = networkInterface.getName();
                InetAddress address;
                while(addressEnum.hasMoreElements()) {
                    address = addressEnum.nextElement();
                    if(address.getClass() == Inet4Address.class) {
                        adder.add(interfaceName, address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}
