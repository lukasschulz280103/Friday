package com.friday.ar.plugin.application;

import android.content.Context;
import android.util.Log;

import com.friday.ar.plugin.Plugin;

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
        pluginDir = new File(context.getFilesDir() + "/plugin");
        pluginDir.mkdirs();
    }

    String packageName = "";

    /**
     * @return List of all installed Plugins
     */
    public ArrayList<Plugin> getIndexedPlugins() {
        return pluginIndex;
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
                Log.d(LOGTAG, "Loading " + dir.getName());
                packageName = "";
                loadPackage(dir);
            }
            return true;
        }
        return false;
    }

    private void loadPackage(File packageDir) {
        if (packageDir.listFiles() == null) return;
        for (File middlePackage : packageDir.listFiles()) {
            if (middlePackage.listFiles() != null && middlePackage.listFiles().length == 1) {
                loadPackage(middlePackage.listFiles()[0]);
                packageName.concat(middlePackage + ".");
            } else {
                packageName = packageName.substring(0, packageName.length() - 1);
                Log.d(LOGTAG, "Loaded package:" + packageName);
            }
        }
    }
}
