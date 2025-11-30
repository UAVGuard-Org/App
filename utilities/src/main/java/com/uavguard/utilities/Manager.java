package com.uavguard.utilities;

import com.uavguard.sdk.Plugin;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;

public class Manager {

    public static Plugin[] load(String folderPath) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        Plugin[] plugins = new Plugin[(int) files.length];
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            URLClassLoader loader = new URLClassLoader(
                new URL[] { file.toURI().toURL() },
                Plugin.class.getClassLoader()
            );

            var mainClassName = new java.util.jar.JarFile(file)
                .getManifest()
                .getMainAttributes()
                .getValue("Main-Class");

            Plugin plugin = (Plugin) Class.forName(
                mainClassName,
                true,
                loader
            ).newInstance();

            plugins[i] = plugin;
        }

        return plugins;
    }
}
