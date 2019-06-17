package com.friday.ar.plugin.application;

import android.content.Context;
import android.util.Log;

import com.friday.ar.plugin.Plugin;
import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.PluginFile;
import com.friday.ar.plugin.security.PluginVerifier;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
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
            for (File pluginFile : pluginDir.listFiles()) {
                try {
                    PluginFile plugin = new PluginFile(pluginFile.getPath());
                    Log.d(LOGTAG, "Loading " + plugin.getName());
                    loadPackage(plugin);
                } catch (IOException e) {
                    Log.e(LOGTAG, e.getMessage(), e);
                } catch (JSONException e) {
                    Log.e(LOGTAG, e.getMessage(), e);
                } catch (Manifest.ManifestSecurityException e) {
                    Log.e(LOGTAG, e.getMessage(), e);
                }

            }
            return true;
        }
        return false;
    }

    private void loadPackage(@NotNull PluginFile packageDir) {
        try {
            PluginVerifier.verify(packageDir);
        } catch (IOException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } catch (Manifest.ManifestSecurityException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
    }
}
