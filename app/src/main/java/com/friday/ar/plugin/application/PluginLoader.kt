package com.friday.ar.plugin.application

import android.content.Context
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.plugin.Plugin
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.security.PluginVerifier
import com.friday.ar.plugin.security.VerificationSecurityException
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.*

/**
 * This class loads all plugins when the app starts.
 */
class PluginLoader(context: Context) {
    companion object {
        private const val LOGTAG = "PluginLoader"
    }

    /**
     * @return List of all installed Plugins
     */
    val indexedPlugins = ArrayList<Plugin>()
    private val pluginDir: File = Constant.getPluginDir(context)

    init {
        pluginDir.mkdirs()
    }

    /**
     * Load all plugins into cache.
     * This is basically indexes all installed Plugins.
     *
     * @return Returns a boolean value whether the plugins could be loaded or not.
     */
    fun startLoading(): Boolean {
        indexedPlugins.clear()
        if (pluginDir.exists()) {
            Log.d(LOGTAG, pluginDir.listFiles().toString())
            for (pluginFile in pluginDir.listFiles()) {
                try {
                    val plugin = PluginFile(pluginFile.path)
                    Log.d(LOGTAG, "Loading " + plugin.name)
                    loadPackage(plugin)
                    Log.d(LOGTAG, "Loaded plugins:$indexedPlugins")
                } catch (e: IOException) {
                    Log.e(LOGTAG, e.message, e)
                } catch (e: JSONException) {
                    Log.e(LOGTAG, e.message, e)
                } catch (e: IllegalArgumentException) {
                    pluginFile.delete()
                    Log.e(LOGTAG, e.localizedMessage, e)
                }

            }
            return true
        }
        return false
    }

    private fun loadPackage(packageDir: PluginFile) {
        val verifier = PluginVerifier()
        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
            override fun onSuccess() {
                val pluginManifest = packageDir.manifest
                indexedPlugins.add(pluginManifest.toPlugin())
            }

            override fun onZipException(e: ZipException) {

            }

            override fun onIOException(e: IOException) {

            }

            override fun onJSONException(e: JSONException) {

            }

            override fun onVerificationFailed(e: VerificationSecurityException) {

            }
        })
        verifier.verify(packageDir, false)
    }
}
