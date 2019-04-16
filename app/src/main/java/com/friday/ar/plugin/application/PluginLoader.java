package com.friday.ar.plugin.application;

import android.content.Context;
import android.util.Log;

import com.friday.ar.plugin.Plugin;
import com.friday.ar.sdk.ArApplication;

import java.io.File;
import java.util.ArrayList;

/**
 * This class loads all plugins when the app starts.
 */
public class PluginLoader {
    public static final String LOGTAG = "PluginLoader";
    private ArrayList<Plugin> pluginIndex = new ArrayList<>();
    private File pluginDir;

    public PluginLoader(Context context) {
        pluginDir = new File(context.getFilesDir().getPath() + "/plugins");
        if (!pluginDir.mkdir()) {
            Log.e(LOGTAG, "Plugin directory could not be created");
        }
    }

    /**
     * Load all plugins into cache.
     * This is basically indexes all installed Plugins.
     *
     * @return Returns a boolean value whether the plugins could be loaded or not.
     */
    public boolean startLoading() {
        if (pluginDir.exists()) {
            for (File dir : pluginDir.listFiles()) {
                ArApplication app = new ArApplication();
                app.start();
            }
            return true;
        }
        return false;
    }

    /**
     * @return List of all installed Plugins
     */
    public ArrayList<Plugin> getIndexedPlugins() {
        return pluginIndex;
    }
}
