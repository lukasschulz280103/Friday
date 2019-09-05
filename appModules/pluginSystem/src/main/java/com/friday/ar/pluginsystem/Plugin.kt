@file:Suppress("unused")

package com.friday.ar.pluginsystem

import android.media.Rating
import android.net.Uri

import com.friday.ar.pluginsystem.file.PluginFile
import java.io.Serializable

class Plugin : Serializable {

    var name: String? = null
    var iconURI: Uri? = null
    var authorName: String? = null
    var authorId: String? = null
    var versionName: String? = null
    var rating: Rating? = null
    var pluginFile: PluginFile? = null
}
