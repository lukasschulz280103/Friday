package com.friday.ar.plugin.installer;

import android.content.Context;

import com.friday.ar.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class PluginInstaller {
    private Context context;

    public PluginInstaller(Context context) {
        this.context = context;
    }

    public void installFrom(File pluginDir) throws IOException {
        if (!pluginDir.isDirectory())
            throw new IllegalArgumentException("A Friday plugin package cannot be a directory.");
        else if (!FileUtil.getFileEnding(pluginDir).equals(".jar"))
            throw new IllegalArgumentException("This file has an inappropriate ending");
        else {
            FileUtil.moveFile(pluginDir, new File(context.getFilesDir() + "/plugin"));
        }
    }
}
