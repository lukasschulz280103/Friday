package com.friday.ar.base.system.plugin

import androidx.lifecycle.LiveData
import com.friday.ar.base.system.environment.Context
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.db.LocalPluginsDB

open class PluginManager(private val context: Context) {

    internal fun queryInstalledPlugins(): LiveData<List<Plugin>> {
        val pluginDatabase = LocalPluginsDB.getInstance(context.applicationContext)
        return pluginDatabase.indexedPluginsDAO().getCurrentInstalledPlugins()
    }


}