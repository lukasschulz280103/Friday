package com.friday.ar.pluginsystem.db.pluginIndex

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.friday.ar.pluginsystem.Plugin

@Dao
interface IndexedPluginsDAO {
    @Query("SELECT * FROM localInstalledPlugins")
    fun getCurrentInstalledPlugins(): List<Plugin>

    @Insert
    fun insertPlugin(plugin: Plugin)

    @Query("DELETE FROM localInstalledPlugins")
    fun clear()
}