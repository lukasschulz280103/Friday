package com.friday.ar.plugin

import android.media.Rating
import android.net.Uri

import com.friday.ar.plugin.file.PluginFile

class Plugin {

    var name: String? = null
    var iconURI: Uri? = null
    var authorName: String? = null
    var authorId: String? = null
    var versionName: String? = null
    var rating: Rating? = null
    var pluginFile: PluginFile? = null
}
