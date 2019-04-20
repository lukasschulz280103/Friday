package com.friday.ar.plugin.installer;

import com.friday.ar.Util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class PluginInstaller {
    public void install(File pluginDir) throws IOException {
        if (!pluginDir.isDirectory())
            throw new IllegalArgumentException("A Friday plugin package cannot be a directory.");
        else if (!FileUtil.getFileEnding(pluginDir).equals(".jar"))
            throw new IllegalArgumentException("This file has an inappropriate ending");
        else {
            JarFile jarFile = new JarFile(pluginDir);
        }
    }
}
