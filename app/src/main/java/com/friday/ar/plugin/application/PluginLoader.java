package com.friday.ar.plugin.application;

import android.content.Context;
import android.util.Log;

import com.friday.ar.Constant;
import com.friday.ar.plugin.Plugin;
import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.PluginFile;
import com.friday.ar.plugin.security.PluginVerifier;
import com.friday.ar.plugin.security.VerificationSecurityException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class loads all plugins when the app starts.
 */
public class PluginLoader {
    private static final String LOGTAG = "PluginLoader";
    private ArrayList<Plugin> pluginIndex = new ArrayList<>();
    private File pluginDir;
    private Context context;

    public PluginLoader(Context context) {
        pluginDir = Constant.getPluginDir(context);
        pluginDir.mkdirs();
        this.context = context;
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
            Log.d(LOGTAG, pluginDir.listFiles().toString());
            for (File pluginFile : pluginDir.listFiles()) {
                try {
                    PluginFile plugin = new PluginFile(pluginFile.getPath());
                    Log.d(LOGTAG, "Loading " + plugin.getName());
                    loadPackage(plugin);
                    Log.d(LOGTAG, "Loaded plugins:" + pluginIndex.toString());
                } catch (IOException e) {
                    Log.e(LOGTAG, e.getMessage(), e);
                } catch (JSONException e) {
                    Log.e(LOGTAG, e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    pluginFile.delete();
                    Log.e(LOGTAG, e.getLocalizedMessage(), e);
                }

            }
            return true;
        }
        return false;
    }

    private void loadPackage(@NotNull PluginFile packageDir) {
        try {
            PluginVerifier.verify(packageDir, false);
            Manifest pluginManifest = packageDir.getManifest();
            pluginIndex.add(pluginManifest.toPlugin());
        } catch (IOException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } catch (Manifest.ManifestSecurityException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        } catch (VerificationSecurityException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
    }
}
