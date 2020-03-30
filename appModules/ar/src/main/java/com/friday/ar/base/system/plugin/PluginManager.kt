package com.friday.ar.base.system.plugin

import android.content.Context
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.db.LocalPluginsDB

open class PluginManager(private val context: Context) {

    internal fun queryInstalledPlugins(): List<Plugin> {
        val pluginDatabase = LocalPluginsDB.getInstance(context)
        return pluginDatabase.indexedPluginsDAO().getCurrentInstalledPlugins()
    }


}