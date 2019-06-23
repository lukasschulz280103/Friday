package com.friday.ar.plugin.file;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


/**
 * This class represents an Friday plugin file/directory.
 **/
public class PluginFile extends File {
    private static final String LOGTAG = "PluginFile";
    private Manifest manifest;

    public PluginFile(String pathname) throws IOException, JSONException {
        super(pathname);
        if (!isDirectory()) {
            throw new IllegalArgumentException("The given path points to a file");
        }
        File manifestFile = new File(getPath() + "/meta/manifest.json");
        JSONObject meta = new JSONObject(new String(Files.readAllBytes(manifestFile.toPath()), StandardCharsets.UTF_8)).getJSONObject("meta");
        manifest = new Manifest(
                this,
                meta.getString("applicationName"),
                meta.getString("authorName"),
                meta.getString("versionName")
        );

    }

    /**
     * returns a {@link Manifest} object with the values obtained from this plugin file.
     *
     * @return {@link Manifest} object of meta/manifest
     */
    public Manifest getManifest() {
        return manifest;
    }
}
