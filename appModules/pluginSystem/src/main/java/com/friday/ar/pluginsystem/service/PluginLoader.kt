package com.friday.ar.pluginsystem.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.db.LocalPluginsDB
import com.friday.ar.pluginsystem.file.PluginFile
import com.friday.ar.pluginsystem.security.PluginVerifier
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import extensioneer.notNull
import extensioneer.notNullWithResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import java.io.File
import java.io.IOException

/**
 * This class loads all plugins when the app starts.
 * This is not a model class but a android service which regularly updates the plugin-database if any change to the installed plugins occurs.
 */
class PluginLoader : JobService(), KoinComponent {

    companion object {
        private const val LOGTAG = "PluginLoader"
    }

    private val pluginDir: File = Constant.getPluginDir(get())
    private val localInstalledPluginsDB: LocalPluginsDB by inject()
    private val newLoadedPlugins = ArrayList<Plugin>()

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
        if (pluginDir.exists()) {
            for (pluginFile in pluginDir.listFiles()) {
                try {
                    //Load plugin directory
                    val plugin = PluginFile(pluginFile.path)
                    Log.d(LOGTAG, "Loading " + plugin.name)
                    loadPackage(plugin)
                } catch (e: IOException) {
                    Log.e(LOGTAG, e.message, e)
                } catch (e: JSONException) {
                    Log.e(LOGTAG, e.message, e)
                } catch (e: IllegalArgumentException) {
                    pluginFile.delete()
                    Log.e(LOGTAG, e.localizedMessage, e)
                }

            }
            GlobalScope.launch {
                //Check if a plugin, which is still in the database, really is installed
                for (plugin in newLoadedPlugins) {
                    localInstalledPluginsDB.indexedPluginsDAO().getCurrentInstalledPlugins().value.notNull {
                        if (!contains(plugin)) localInstalledPluginsDB.indexedPluginsDAO().removePlugin(plugin)
                    }
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
                val plugin = packageDir.manifest.notNullWithResult { toPlugin() }
                plugin.notNull plugin@{
                    newLoadedPlugins.add(this)
                    val currentLoadedPluginsList = localInstalledPluginsDB.indexedPluginsDAO().getCurrentInstalledPlugins().value
                    currentLoadedPluginsList.notNull {
                        if (!contains(this@plugin)) localInstalledPluginsDB.indexedPluginsDAO().insertPlugin(this@plugin)
                    }
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
        GlobalScope.launch {
            startLoading()
            LocalBroadcastManager.getInstance(this@PluginLoader).sendBroadcast(Intent(Constant.BroadcastReceiverActions.BROADCAST_PLUGINS_LOADED))
            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(LOGTAG, "stopping PluginLoader...")
        return true
    }
}
