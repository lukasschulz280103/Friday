package com.friday.ar.plugin.file


import com.friday.ar.plugin.Plugin
import com.friday.ar.plugin.security.VerificationSecurityException

class Manifest(
        /**
         * * source file of the plugin
         * */
        private val sourceFile: PluginFile, val pluginName: String, val author: String?, var version: String?) {

    fun toPlugin(): Plugin {
        val returnPlugin = Plugin()
        returnPlugin.name = pluginName
        returnPlugin.authorName = author
        returnPlugin.versionName = version
        returnPlugin.pluginFile = sourceFile
        return returnPlugin
    }

    /**
     * Represents an exception thrown when a plugins' manifest is invalid causing security issues
     *
     * @see Manifest
     */
    open class ManifestSecurityException(message: String) : VerificationSecurityException("The given manifest is not valid:$message") {

        /**
         * This exception indicates that an important field is missing in the manifest and the plugin could not be loaded.
         */
        class MissingFieldException(message: String) : ManifestSecurityException("A field is missing:$message")
    }
}
