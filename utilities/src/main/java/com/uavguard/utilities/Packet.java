package com.uavguard.utilities;

public class Packet {

    private byte[] parameters = {
        (byte) 0x80, //l/r
        (byte) 0x80, //f/b
        (byte) 0x80, //u/d
        (byte) 0x80, //raw
    };

    private byte[] packet = {
        (byte) 0x66,
        (byte) 0x80,
        (byte) 0x80,
        (byte) 0x80,
        (byte) 0x80,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x99,
    };

    public void setParameter(int index, byte value) {
        packet[index] = value;
    }

    public void setParameter(int index, int percent) {
        parameters[index] = (byte) (128 +
            (percent / 100f) * (percent >= 0 ? 127 : 128));
    }

    public byte[] getPacket() {
        byte checksum = 0x00;

        for (int i = 0; i < parameters.length; i++) {
            packet[i + 1] = parameters[i];
            checksum ^= parameters[i];
        }

        packet[packet.length - 2] = checksum;

        return packet;
    }
}
