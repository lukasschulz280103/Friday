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
import kotlin.reflect.full.memberProperties

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
            updateDatabase()
            return true
        }
        return false
    }

    private fun updateDatabase() {
        val localInstalledPluginsList = localInstalledPluginsDB.indexedPluginsDAO().getCurrentInstalledPlugins()
        Log.d(LOGTAG, "updating database with newly loaded plugins. Old database list size: ${localInstalledPluginsList.size} / new list size ${newLoadedPlugins.size}")
        //Check if a plugin, which is still in the database, really is installed
        val tempPluginList = ArrayList<Plugin>()
        for (plugin in localInstalledPluginsList) {
            Log.d(LOGTAG, "updating LocalPluginsDB entry ${plugin.dbID}/${plugin.name}")
            tempPluginList.add(plugin)
            for (plugin_newLoaded in newLoadedPlugins) {
                for (plugin2 in localInstalledPluginsList) {
                    Log.d(LOGTAG, comparePlugins(plugin_newLoaded, plugin2))
                }
                if (plugin.pluginFileUri == plugin_newLoaded.pluginFileUri) {
                    tempPluginList.remove(plugin)
                }
            }
        }
        for (plugin in tempPluginList) {
            Log.d(LOGTAG, "removing non-existant plugin ${plugin.dbID}/${plugin.name}")
            localInstalledPluginsDB.indexedPluginsDAO().removePlugin(plugin.dbID)
        }
        Log.d(LOGTAG, "database updated. Size of database: ${localInstalledPluginsList.size}")
    }

    private fun comparePlugins(plugin1: Plugin, plugin2: Plugin): String {
        var str = "plugin1----\n"
        for (prop in Plugin::class.memberProperties) {
            str += "${prop.name} = ${prop.get(plugin1)}\n"
        }
        str += "plugin2----\n"
        for (prop in Plugin::class.memberProperties) {
            str += "${prop.name} = ${prop.get(plugin2)}\n"
        }
        return str
    }

    //TODO: BUG: The loader adds the plugin to the database and removes it afterwards(or the other way maybe)
    private fun loadPackage(packageDir: PluginFile) {
        val verifier = PluginVerifier()
        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
            override fun onSuccess() {
                val plugin = packageDir.manifest.notNullWithResult { toPlugin() }
                plugin.notNull plugin@{
                    newLoadedPlugins.add(this)
                    val currentLoadedPluginsList = localInstalledPluginsDB.indexedPluginsDAO().getCurrentInstalledPlugins()
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
            val currentLoadedPluginsList = localInstalledPluginsDB.indexedPluginsDAO().getCurrentInstalledPlugins()
            Log.d(LOGTAG, "length of the list of currently installed plugins(pre-loading): ${currentLoadedPluginsList.size}")
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
