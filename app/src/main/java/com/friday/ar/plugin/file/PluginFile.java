package com.friday.ar.plugin.file;


import android.content.Context;

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
    private Manifest manifest;

    public PluginFile(String pathname, Context context) throws IOException, JSONException, IllegalArgumentException {
        super(pathname);
        if (!isDirectory()) {
            throw new IllegalArgumentException("The given path points to a file");
        }
        File manifestFile = new File(getPath() + "/meta/manifest.json");
        JSONObject meta = new JSONObject(new String(Files.readAllBytes(manifestFile.toPath()), StandardCharsets.UTF_8));
        manifest = new Manifest(
                meta.getString("applicationName"),
                meta.getString("authorName"),
                meta.getString("versionName")
        );

    }

    public Manifest getManifest() {
        return manifest;
    }
}
