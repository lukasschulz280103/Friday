package com.friday.ar.plugin.security;

import android.content.Context;
import android.util.Log;

import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.Manifest.ManifestSecurityException;
import com.friday.ar.plugin.file.PluginFile;
import com.friday.ar.plugin.file.ZippedPluginFile;
import com.friday.ar.util.FileUtil;

import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Use this class to verify any kind of possible {@link PluginFile}
 * This class checks the following cases:
 * <ul>
 * <li>The file contains a manifest.
 * <ul>
 * <li>The manifest contains the required meta info</li>
 * <li>The manifests meta info contains the following keys:<b>applicationName</b>, <b>versionName</b></li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * <h3>Warning policy</h3>
 * In case of missing and security relevant info, such as an identifier and/or name of the author of a plugin, a warning will be thrown.
 * <b>Plugins which are installed from the official Friday store are not allowed to have the required info missing and will be blocked from installing.</b>
 * </p>
 */
public class PluginVerifier {
    private static final String LOGTAG = "PluginVerifier";

    public static void verify(ZippedPluginFile plugin, Context context) throws JSONException, ManifestSecurityException, ZipException, IOException {
        File externalPluginZipCache = new File(context.getExternalCacheDir().getPath() + "/pluginZipCache/" +
                plugin.getFile().getName().replace(FileUtil.getFileExtension(plugin.getFile()), ""));
        plugin.extractAll(externalPluginZipCache.getPath());
        for (File file : externalPluginZipCache.listFiles()) {
            Log.d(LOGTAG, file.getName());
        }
        verify(new PluginFile(externalPluginZipCache.getPath(), context));
        //if(! externalPluginZipCache.delete()){
        //    externalPluginZipCache.deleteOnExit();
        //}
    }

    public static void verify(PluginFile pluginFile) throws ManifestSecurityException, JSONException, IOException {
        String manifestContentString = new String(Files.readAllBytes(pluginFile.toPath()), StandardCharsets.UTF_8);
        JSONObject manifestOject = new JSONObject(manifestContentString);
        JSONObject meta = manifestOject.getJSONObject("meta");
        if (meta == null) {
            throw new Manifest.ManifestSecurityException("Manifest does not contain required meta info!");
        }
        if (meta.getString("applicationName") == null ||
                meta.getString("applicationName").isEmpty()) {
            throw new ManifestSecurityException.MissingFieldException("meta", "The manifest meta info is missing an application name, or its value is empty.");
        }
        if (meta.getString("authorName") == null ||
                meta.getString("authorName").isEmpty()) {
            Log.w(LOGTAG, "The plugin '" + pluginFile.getName() + "' has not provided an authors name and thus is untrusted.");
        }
        if (meta.getString("versionName") == null ||
                meta.getString("versionName").isEmpty()) {
            throw new ManifestSecurityException.MissingFieldException("meta", "The manifest meta info is missing the versionName, or its value is empty.");
        }
    }
}
