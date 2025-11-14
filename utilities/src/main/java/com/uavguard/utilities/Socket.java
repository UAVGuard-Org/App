package com.uavguard.utilities;

import java.net.*;

public class Socket {

    public static void sendPacket(byte[] data, String ip, int port)
        throws Exception {
        String os = System.getProperty("os.name").toLowerCase();

        DatagramSocket socket = new DatagramSocket();
        InetAddress addr = InetAddress.getByName(ip);
        DatagramPacket pkt = new DatagramPacket(data, data.length, addr, port);
        socket.send(pkt);
        for (byte b : data) {
            System.out.printf("%02X", b);
        }

        System.out.printf(" to " + ip);
        System.out.println();
        socket.close();
    }
}
