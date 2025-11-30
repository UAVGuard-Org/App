package com.uavguard.utilities;

import java.io.File;

public class Path {

    public static String getAppData() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return System.getProperty("user.home") + "/AppData/Local/UAVGuard/";
        } else if (
            os.contains("nix") || os.contains("nux") || os.contains("aix")
        ) {
            return System.getProperty("user.home") + "/.local/share/UAVGuard/";
        } else {
            return "";
        }

        /*
        else if (os.contains("mac")) {
            return "";
        }
        */
    }

    public static void checkPaths() {
        File root = new File(Path.getAppData());
        File plugins = new File(Path.getAppData() + "plugins");

        if (!root.exists()) {
            root.mkdirs();
            plugins.mkdirs();
        }

        if (!plugins.exists()) plugins.mkdirs();
    }
}
