package com.uavguard.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class Network {

    public static void sendPacket(byte[] data, String ip, int port)
        throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress addr = InetAddress.getByName(ip);
        DatagramPacket pkt = new DatagramPacket(data, data.length, addr, port);
        socket.send(pkt);
        socket.close();
    }

    public static String getGatewayAddress() throws Exception {
        Process process = Runtime.getRuntime().exec(
            new String[] { "bash", "-c", "ip route show default " }
        );
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );

        return reader.readLine().trim().split("\\s+")[2];
    }
}
