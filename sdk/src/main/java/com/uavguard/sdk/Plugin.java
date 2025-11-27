package com.uavguard.sdk;

import com.uavguard.sdk.Command;
import com.uavguard.sdk.Video;

public interface Plugin {
    String getName();
    String getVersion();
    Command getCommand();
    Video getVideo();
}
