package com.uavguard.plugin;

import java.util.function.Consumer;

public interface Video {
    void resume();
    void stop();
    void setCallback(Consumer<byte[]> callback);
}
