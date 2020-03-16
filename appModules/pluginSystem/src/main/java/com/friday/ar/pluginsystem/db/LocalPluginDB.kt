package com.friday.ar.pluginsystem.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.db.pluginIndex.IndexedPluginsDAO


@Database(entities = [Plugin::class], version = 1)
abstract class LocalPluginsDB : RoomDatabase() {
    companion object {
        private var INSTANCE: LocalPluginsDB? = null

        fun getInstance(context: Context): LocalPluginsDB {
            return if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, LocalPluginsDB::class.java, "localPluginDB").build()
                INSTANCE!!
            } else {
                INSTANCE!!
            }
        }
    }

    abstract fun indexedPluginsDAO(): IndexedPluginsDAO


}