package com.uavguard.plugin;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public interface Video {
    int getPort();
    void getSetup(DatagramSocket socket) throws Exception;
    void getLoop(DatagramSocket socket, byte[] data) throws Exception;
    void setCallback(Consumer<byte[]> callback);
}
