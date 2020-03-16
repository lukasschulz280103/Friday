package com.friday.ar.pluginsystem.db.sys.registry

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.friday.ar.pluginsystem.sys.registry.RegistryEntry

@Dao
interface RegistryEntryDAO {
    @Query("SELECT * FROM registrationEntryDB")
    fun getRegistrationEntrys(): LiveData<List<RegistryEntry>>

    @Insert
    fun insertRegistrationEntry(regEntry: RegistryEntry)

    @Delete
    fun removeRegistrationEntry(regEntry: RegistryEntry)
}