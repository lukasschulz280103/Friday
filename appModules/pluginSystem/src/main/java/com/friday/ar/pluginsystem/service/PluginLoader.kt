package com.friday.ar.pluginsystem.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.file.PluginFile
import com.friday.ar.pluginsystem.security.PluginVerifier
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import extensioneer.notNull
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.File
import java.io.IOException
import java.util.*

/**
 * This class loads all plugins when the app starts.
 */
class PluginLoader : JobService(), KoinComponent {

    companion object {
        private const val LOGTAG = "PluginLoader"

        /**
         * @return List of all installed Plugins
         */
        val indexedPlugins = ArrayList<Plugin>()
    }

    private val pluginDir: File = Constant.getPluginDir(get())

    init {
        pluginDir.mkdirs()
    }

    /**
     * Load all plugins into cache.
     * This is basically indexes all installed Plugins.
     *
     * @return Returns a boolean value whether the plugins could be loaded or not.
     */
    private fun startLoading(): Boolean {
        Log.d(LOGTAG, "starting to load plugins...")
        indexedPlugins.clear()
        if (pluginDir.exists()) {
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
                pluginManifest.notNull {
                    indexedPlugins.add(this.toPlugin())
                    Log.d(LOGTAG, "plugin found:${author}/${pluginName}.${version}")
                }
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

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(LOGTAG, "starting PluginLoader...")
        startLoading()
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(Constant.BroadcastReceiverActions.BROADCAST_PLUGINS_LOADED))
        jobFinished(params, false)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(LOGTAG, "stopping PluginLoader...")
        return true
    }
}
