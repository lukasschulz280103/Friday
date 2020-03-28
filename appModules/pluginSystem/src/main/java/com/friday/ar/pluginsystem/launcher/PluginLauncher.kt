package com.friday.ar.pluginsystem.launcher

import android.content.Context
import android.util.Log
import com.friday.ar.pluginsystem.Plugin

/**
 * Simple class to launch plugins with. Will may be removed in future versions.
 */
class PluginLauncher(val context: Context) {
    companion object {
        private const val LOGTAG = "PluginLauncher"
        private const val DIVIDER = "----------------------------------------------------------"
    }

    fun launch(plugin: Plugin) {
        Log.d(LOGTAG, DIVIDER)
        Log.d(LOGTAG, "Launching plugin ${plugin.dbID}")
        Log.d(LOGTAG, "plugin meta data:" +
                "------------------" +
                "name: ${plugin.name}" +
                "dataBaseId(may be null): ${plugin.dbID}" +
                "authorName: ${plugin.authorName}" +
                "authorId: ${plugin.authorId}" +
                "versionName: ${plugin.versionName}" +
                "pluginFileUri: ${plugin.pluginFileUri}")
        Log.d(LOGTAG, "LAUNCHING ${plugin.name}...")
        Log.d(LOGTAG, "loading plugin...")
        Log.d(LOGTAG, DIVIDER)
    }
}