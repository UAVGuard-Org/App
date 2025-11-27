package com.uavguard.sdk;

import com.uavguard.sdk.Action;
import com.uavguard.sdk.Movement;

public interface Command {
    int getPort();

    byte[] getPacket();

    Action[] getActions();

    void setParameter(Movement action, int percent);
}
