package com.uavguard.plugin;

import com.uavguard.plugin.Command;
import com.uavguard.plugin.Video;

public interface Plugin {
    String getName();
    String getVersion();
    Command getCommand();
    Video getVideo();
}
