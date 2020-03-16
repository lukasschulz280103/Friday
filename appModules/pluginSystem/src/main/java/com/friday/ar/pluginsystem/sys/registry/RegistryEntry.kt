package com.friday.ar.pluginsystem.sys.registry

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * a simple registry entry.
 */
@Entity(tableName = "registrationEntryDB")
class RegistryEntry {

    /**
     * registry ID in registry database
     */
    @PrimaryKey
    var registryEntryID: Int = 0

    /**
     * id of source plugin in the [com.friday.ar.pluginsystem.db.LocalPluginsDB]
     */
    var pluginDBId: Int = 0

    /**
     * key of this [RegistryEntry]
     */
    var entryKey: String = ""

    /**
     * value of this [RegistryEntry]
     */
    var entryValue: String = ""
}