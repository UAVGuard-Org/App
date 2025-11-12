package com.uavguard.plugin;

public class Command {

    private String name;
    private byte[] packet;

    public Command(String name, byte[] packet) {
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
