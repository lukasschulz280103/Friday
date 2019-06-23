package com.friday.ar.plugin.security;

import android.content.Context;
import android.util.Log;

import com.friday.ar.Constant;
import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.Manifest.ManifestSecurityException;
import com.friday.ar.plugin.file.PluginFile;
import com.friday.ar.plugin.file.ZippedPluginFile;

import net.lingala.zip4j.exception.ZipException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

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

    /**
     * Verify a plugin which is zipped.
     * Throws an exception if an plugin file seems o be corrupted.
     *
     * @param plugin            {@link ZippedPluginFile} to run verification on
     * @param context           {@link Context} application context
     * @param deleteOnException boolean whether the cached file should be deleted if the verification fails.
     * @throws JSONException                 is thrown when the verification fails while trying to read the manifest, but its json is invalid
     * @throws VerificationSecurityException is thrown when the {@link PluginVerifier} encounters corrupted plugin modules(e.g. missing manifest entry)
     * @throws ZipException                  is thrown if the file that was passed as the first argument is not a valid {@link net.lingala.zip4j.core.ZipFile}
     * @throws IOException                   general IOException thrown when a problem occurs while trying to use the file
     */
    public static void verify(ZippedPluginFile plugin, Context context, boolean deleteOnException) throws JSONException, VerificationSecurityException, IOException, ZipException {
        File extractedPluginDir = Constant.getPluginCacheDir(context, plugin.getFile().getName().replace(".fpl", ""));
        try {
            plugin.extractAll(extractedPluginDir.getPath());
            Log.d(LOGTAG, "extractionFilePath:" + extractedPluginDir.getPath());
        } catch (ZipException e) {
            Constant.getPluginCacheDir(context).deleteOnExit();
            throw new ZipException(e);
        }
        for (File file : Constant.getPluginCacheDir(context).listFiles()) {
            Log.d(LOGTAG, file.getName());
        }
        PluginFile cacheFile = new PluginFile(extractedPluginDir.getPath() + "/"+extractedPluginDir.getName());
        verify(cacheFile, deleteOnException);
    }

    public static void verify(PluginFile pluginFile, boolean deleteOnException) throws JSONException, IOException, VerificationSecurityException {
        String manifestContentString;
        try {
            manifestContentString = new String(Files.readAllBytes(new File(pluginFile.getPath() + "/meta/manifest.json").toPath()), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            if (deleteOnException) pluginFile.delete();
            throw new VerificationSecurityException("Could not find manifest file in the given plugin file.");
        }
        JSONObject manifestOject = new JSONObject(manifestContentString);
        JSONObject meta = manifestOject.getJSONObject("meta");
        if (meta == null) {
            if(deleteOnException) pluginFile.delete();
            throw new Manifest.ManifestSecurityException("Manifest does not contain required meta info!");
        }
        if (meta.getString("applicationName") == null ||
                meta.getString("applicationName").isEmpty()) {
            if(deleteOnException) pluginFile.delete();
            throw new ManifestSecurityException.MissingFieldException("meta", "The manifest meta info is missing an application name, or its value is empty.");
        }
        if (meta.getString("authorName") == null ||
                meta.getString("authorName").isEmpty()) {
            Log.w(LOGTAG, "The plugin '" + pluginFile.getName() + "' has not provided an authors name and thus is untrusted.");
        }
        if (meta.getString("versionName") == null ||
                meta.getString("versionName").isEmpty()) {
            if(deleteOnException) pluginFile.delete();
            throw new ManifestSecurityException.MissingFieldException("meta", "The manifest meta info is missing the versionName, or its value is empty.");
        }
    }
}
