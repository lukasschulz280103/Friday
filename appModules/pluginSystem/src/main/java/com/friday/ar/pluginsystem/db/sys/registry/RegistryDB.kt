package com.friday.ar.pluginsystem.db.sys.registry

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.friday.ar.pluginsystem.sys.registry.RegistryEntry

@Database(entities = [RegistryEntry::class], version = 1)
abstract class RegistryDB : RoomDatabase() {

    companion object {
        private var INSTANCE: RegistryDB? = null

        fun getInstance(context: Context): RegistryDB {
            return if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, RegistryDB::class.java, "registrationEntryDB").build()
                INSTANCE!!
            } else {
                INSTANCE!!
            }
        }

    }

    abstract fun registeredEntryDAO(): RegistryEntryDAO

}