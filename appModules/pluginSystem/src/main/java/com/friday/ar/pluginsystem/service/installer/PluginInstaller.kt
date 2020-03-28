package com.friday.ar.pluginsystem.service.installer

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.R
import com.friday.ar.pluginsystem.cache.PluginFileCacheUtil
import com.friday.ar.pluginsystem.db.LocalPluginsDB
import com.friday.ar.pluginsystem.file.PluginFile
import com.friday.ar.pluginsystem.file.ZippedPluginFile
import com.friday.ar.pluginsystem.security.PluginVerifier
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import extensioneer.files.toFile
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class is used to install plugins.
 * @see isSilent
 */
class PluginInstaller(private val context: Context) : KoinComponent {
    companion object {
        private const val LOGTAG = "PluginInstaller"
    }

    private var onInstallationStateChangedListenerList: ArrayList<OnInstallationStateChangedListener> = ArrayList()
    private val pluginsDB by inject<LocalPluginsDB>()

    /**
     * Determines whether the installation service will show a notification or not. Is false by default.
     */
    var isSilent = false

    /**
     * Install a archived pluginfile ([ZippedPluginFile])
     * @param pluginDir the plugin archive to install
     *
     * @see PluginFile
     * @see Plugin
     */
    @Throws(IOException::class, ZipException::class)
    fun installFrom(pluginDir: ZippedPluginFile) {
        if (!pluginDir.isValidZipFile) throw ZipException("Zip file is invalid")
        val notificationService = PluginInstallerNotificationService(context)
        val verifier = PluginVerifier()
        val cachedPluginFile = PluginFileCacheUtil.cachePluginFile(context, pluginDir)
        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
            override fun onSuccess() {
                if (!isSilent) notificationService.notificationShowSuccess(cachedPluginFile.manifest!!.pluginName)
                notifyInstallProgressChange(context.getString(R.string.pluginInstaller_progressMessage_installing))
                Files.move(cachedPluginFile.toPath(), Constant.getPluginDir(context, cachedPluginFile.name).toPath(), StandardCopyOption.REPLACE_EXISTING)

                DatabaseUpdateHelper().updatePlugins()

                notifyInstallSuccess()
                cachedPluginFile.deleteRecursively()
            }

            override fun onZipException(e: ZipException) {
                if (!isSilent) notificationService.notificationShowError(e)
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }

            override fun onIOException(e: IOException) {
                if (!isSilent) notificationService.notificationShowError(e)
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }

            override fun onJSONException(e: JSONException) {
                if (!isSilent) notificationService.notificationShowError(e)
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }

            override fun onVerificationFailed(e: VerificationSecurityException) {
                if (!isSilent) notificationService.notificationShowError(e)
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }
        })
        if (!isSilent) notificationService.notificationUpdateProgress(context.getString(R.string.pluginInstaller_progressMessage_verifying))
        notifyInstallProgressChange(context.getString(R.string.pluginInstaller_progressMessage_verifying))
        verifier.verify(cachedPluginFile, true)
    }

    /**
     * request installation of a plugin from an [InputStream]
     * @param inputStream source stream to decode
     */
    fun requestInstallFromInputStream(inputStream: InputStream) {
        val installer = PluginInstaller(context)
        installer.isSilent = false
        try {
            val cacheFile = inputStream.toFile(Constant.getPluginCacheDir(context).path + "/" + UUID.randomUUID().toString() + ".fpl")
            installer.installFrom(ZippedPluginFile(cacheFile))
            cacheFile.delete()
        } catch (e: IOException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        } catch (e: ZipException) {
            Log.e(LOGTAG, e.localizedMessage, e)
        }
    }

    /**
     * Uninstall specific plugin
     * @param plugin specififed plugin to uninstall
     */
    fun uninstallPlugin(plugin: Plugin) {
        Log.i(LOGTAG, "uninstalling plugin:${plugin.name}")
        val notificationService = UninstallNotificationHelper(context)
        if (!isSilent) notificationService.notificationUpdateProgress(context.getString(R.string.pluginInstaller_uninstall_progress_title))
        try {
            DatabaseUpdateHelper().remove(plugin)
            plugin.pluginFileUri.apply { PluginFile(this).deleteRecursively() }

            if (!isSilent) notificationService.notificationShowSuccess(context.getString(R.string.pluginInstaller_uninstall_success_ticker_text, plugin.name))

            val broadcastManager = LocalBroadcastManager.getInstance(context)
            val uninstallBroadCastIntent = Intent(Constant.BroadcastReceiverActions.BROADCAST_PLUGIN_UNINSTALLED)
            uninstallBroadCastIntent.putExtra("pluginId", plugin.dbID)
            broadcastManager.sendBroadcast(uninstallBroadCastIntent)

        } catch (e: Exception) {
            if (!isSilent) notificationService.notificationShowError(e)
        }
    }

    /**
     * register a custom [OnInstallationStateChangedListener] callback
     */
    fun addOnInstallationProgressChangedListener(onInstallationStateChangedListener: OnInstallationStateChangedListener) {
        onInstallationStateChangedListenerList.add(onInstallationStateChangedListener)
    }

    /**
     * remove a custom, already added [OnInstallationStateChangedListener] callback
     */
    fun removeOnInstallationStateChangedListener(onInstallationStateChangedListener: OnInstallationStateChangedListener) {
        onInstallationStateChangedListenerList.remove(onInstallationStateChangedListener)
    }

    /**
     * update the installation notification
     */
    private fun notifyInstallProgressChange(msg: String) {
        onInstallationStateChangedListenerList.forEach { listener ->
            listener.onProgressChanged(msg)
        }
    }

    /**
     * notify the listeners that the installation was succesful
     *
     * @see OnInstallationStateChangedListener
     * @see addOnInstallationProgressChangedListener
     * @see removeOnInstallationStateChangedListener
     */
    private fun notifyInstallSuccess() {
        onInstallationStateChangedListenerList.forEach { listener ->
            listener.onSuccess()
        }
    }

    /**
     * notifiy the listeners that the installlation failed
     *
     * @see OnInstallationStateChangedListener
     * @see addOnInstallationProgressChangedListener
     * @see removeOnInstallationStateChangedListener
     */
    private fun notifyInstallationFailed(e: Exception) {
        onInstallationStateChangedListenerList.forEach { listener ->
            listener.onFailure(e)
        }
    }

    interface OnInstallationStateChangedListener {
        fun onProgressChanged(progressMessage: String)
        fun onSuccess()
        fun onFailure(e: Exception)
    }
}
