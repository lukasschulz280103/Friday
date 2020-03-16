@file:Suppress("unused")

package com.friday.ar.pluginsystem

import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "localInstalledPlugins")
class Plugin : Serializable {

    /**
     * plugin's ID in the plugin database.
     */
    @PrimaryKey
    var dbID: Int = 0

    lateinit var name: String
    var authorName: String? = null

    /**
     * unique author id in order to collect more infos about an author.
     * This id is given by the server and is being transmitted when downloading a plugin from ARS' marketplace.
     *
     * <b>If this field is null, this plugin was not installed from ARS' secure marketplace.</b>
     */
    var authorId: String? = null

    /**
     * plugins version name, e.g. "1.3.0-alpha03"
     */
    lateinit var versionName: String

    /**
     * [String] uri to plugin directory on the disk.
     */
    lateinit var pluginFileUri: String
}
