package com.friday.ar.plugin.file;


import com.google.common.io.CharStreams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class represents an Friday plugin file.
 **/
public class PluginFile extends ZipFile {
    private Manifest manifest;

    public PluginFile(String pathname) throws IOException, JSONException, Manifest.ManifestSecurityException {
        super(pathname);
        ZipEntry manifestEntry = new ZipEntry("meta/manifest.json");
        InputStream manifestInputStream = getInputStream(manifestEntry);
        String jsonText;
        try (final Reader reader = new InputStreamReader(manifestInputStream)) {
            jsonText = CharStreams.toString(reader);
        }
        JSONObject manifestOject = new JSONObject(jsonText);
        JSONObject meta = manifestOject.getJSONObject("meta");
        if (meta == null) {
            throw new Manifest.ManifestSecurityException("Manifest does not contain required meta info!");
        }
        manifest = new Manifest(
                meta.getString("applicationName"),
                meta.getString("authorInfo"),
                meta.getString("versionName")
        );

    }

    public Manifest getManifest() {
        return manifest;
    }
}
