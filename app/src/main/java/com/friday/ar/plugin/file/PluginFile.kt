package com.friday.ar.plugin.file


import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files


/**
 * This class represents an Friday plugin file/directory.
 */
class PluginFile @Throws(IOException::class, JSONException::class)
constructor(pathname: String) : File(pathname) {
    /**
     * returns a [Manifest] object with the values obtained from this plugin file.
     *
     * @return [Manifest] object of meta/manifest
     */
    val manifest: Manifest

    init {
        if (!isDirectory) {
            throw IllegalArgumentException("The given path points to a file")
        }
        val manifestFile = File("$path/meta/manifest.json")
        val meta = JSONObject(String(Files.readAllBytes(manifestFile.toPath()), StandardCharsets.UTF_8)).getJSONObject("meta")
        manifest = Manifest(
                this,
                meta.getString("applicationName"),
                meta.getString("authorName"),
                meta.getString("versionName")
        )

    }

    companion object {
        private const val LOGTAG = "PluginFile"
    }
}
