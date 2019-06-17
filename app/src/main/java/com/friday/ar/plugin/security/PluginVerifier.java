package com.friday.ar.plugin.security;

import android.util.Log;

import com.friday.ar.plugin.file.Manifest;
import com.friday.ar.plugin.file.Manifest.ManifestSecurityException;
import com.friday.ar.plugin.file.PluginFile;
import com.google.common.io.CharStreams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;

public class PluginVerifier {
    private static final String LOGTAG = "PluginVerifier";

    public static void verify(PluginFile plugin) throws IOException, JSONException, ManifestSecurityException {
        ZipEntry manifestEntry = new ZipEntry("meta/manifest.json");
        InputStream manifestInputStream = plugin.getInputStream(manifestEntry);
        String jsonText;
        try (final Reader reader = new InputStreamReader(manifestInputStream)) {
            jsonText = CharStreams.toString(reader);
        }
        JSONObject manifestOject = new JSONObject(jsonText);
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
            Log.w(LOGTAG, "The plugin '" + plugin.getName() + "' has not provided an authors name and thus is untrusted.");
        }
        if (meta.getString("versionName") == null ||
                meta.getString("versionName").isEmpty()) {
            throw new ManifestSecurityException.MissingFieldException("meta", "The manifest meta info is missing the versionName, or its value is empty.");
        }
    }
}
