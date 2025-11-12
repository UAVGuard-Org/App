package com.uavguard.utilities;

import com.uavguard.plugin.Plugin;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.ArrayList;

public class Manager {

    public final ArrayList<Plugin> plugins = new ArrayList<Plugin>();

    public void load(String folderPath) throws Exception {
        File folder = new File(folderPath);
        for (File file : Objects.requireNonNull(
            folder.listFiles((dir, name) -> name.endsWith(".jar"))
        )) {
            URLClassLoader loader = new URLClassLoader(
                new URL[] { file.toURI().toURL() },
                Plugin.class.getClassLoader()
            );

            var mainClassName = new java.util.jar.JarFile(file)
                .getManifest()
                .getMainAttributes()
                .getValue("mainClass");

            Plugin plugin = (Plugin) Class.forName(mainClassName, true, loader)
                .getDeclaredConstructor()
                .newInstance();
            plugins.add(plugin);
        }
    }
}
