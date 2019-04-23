package com.friday.ar.plugin.installer;

import android.content.Context;
import android.util.Log;

import com.friday.ar.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class PluginInstaller {
    public static final String LOGTAG = "PluginInstaller";
    private Context context;

    public PluginInstaller(Context context) {
        this.context = context;
    }

    public void installFrom(File pluginDir) throws IOException, IllegalFileException {
        Log.d(LOGTAG, FileUtil.getFileExtension(pluginDir));
        if (pluginDir.isDirectory())
            throw new IllegalArgumentException("A Friday plugin package cannot be a directory.");
        else if (!FileUtil.getFileExtension(pluginDir).equals(".jar"))
            throw new IllegalFileException("This file has an inappropriate ending");
        else {
            FileUtil.moveFile(pluginDir, new File(context.getFilesDir() + "/plugin"));
        }
    }

    public class IllegalFileException extends IllegalStateException {
        public IllegalFileException(String message) {
            super(message);
        }
    }
}
