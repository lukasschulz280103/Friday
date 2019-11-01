@file:Suppress("unused")

package com.friday.ar.pluginsystem

import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "localInstalledPlugins")
class Plugin : Serializable {

    @PrimaryKey
    var dbID: Int = 0

    lateinit var name: String
    var authorName: String? = null
    var authorId: String? = null
    lateinit var versionName: String
    lateinit var pluginFileUri: String
}
