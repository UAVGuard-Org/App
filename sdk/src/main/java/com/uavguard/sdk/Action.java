package com.uavguard.sdk;

public class Action {

    private String name;
    private byte[] packet;

    public Action(String name, byte[] packet) {
        this.name = name;
        this.packet = packet;
    }

    public String getName() {
        return name;
    }

    public byte[] getPacket() {
        return packet;
    }
}
