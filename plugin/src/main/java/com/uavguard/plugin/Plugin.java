package com.uavguard.plugin;

import com.uavguard.plugin.Action;
import com.uavguard.plugin.Command;

public interface Plugin {
    int getPort();
    String getName();
    byte[] getPacket();
    Command[] getCommands();
    void setParameter(Action action, int percent);
}
