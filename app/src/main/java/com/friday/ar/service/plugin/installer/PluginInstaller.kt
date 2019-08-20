package com.friday.ar.service.plugin.installer

import android.content.Context
import android.util.Log
import com.friday.ar.R
import com.friday.ar.core.Constant
import com.friday.ar.plugin.Plugin
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.security.PluginVerifier
import com.friday.ar.plugin.security.VerificationSecurityException
import com.friday.ar.ui.store.storeInstallationManagerActivity.StoreInstallationsManagerViewModel
import com.friday.ar.util.FileUtil
import com.friday.ar.util.cache.PluginFileCacheUtil
import extensioneer.files.toFile
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.collections.ArrayList

class PluginInstaller(private val context: Context) {
    companion object {
        private const val LOGTAG = "PluginInstaller"
    }

    private var onInstallationStateChangedListenerList: ArrayList<OnInstallationStateChangedListener> = ArrayList()

    /**
     * Determines whether the installation service will show a notification or not. Is false by default.
     */
    var isSilent = false

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
        if (!isSilent) notificationService.notificationShowProgress(context.getString(R.string.pluginInstaller_progressMessage_verifying))
        notifyInstallProgressChange(context.getString(R.string.pluginInstaller_progressMessage_verifying))
        verifier.verify(cachedPluginFile, true)
    }

    fun requestInstallFromInputStream(inputStream: InputStream) {
        val installer = PluginInstaller(context)
        installer.isSilent = false
        try {
            val cacheFile = inputStream.toFile(Constant.getPluginCacheDir(context).path + "/" + UUID.randomUUID().toString() + ".fpl")
            installer.installFrom(ZippedPluginFile(cacheFile))
            cacheFile.delete()
        } catch (e: IOException) {
            Log.e(StoreInstallationsManagerViewModel.LOGTAG, e.localizedMessage, e)
        } catch (e: ZipException) {
            Log.e(StoreInstallationsManagerViewModel.LOGTAG, e.localizedMessage, e)
        }
    }

    fun uninstallPlugin(plugin: Plugin) {
        Log.i(LOGTAG, "uninstalling plugin:${plugin.name}")
        FileUtil.deleteDirectory(plugin.pluginFile)
        //TODO remove plugin from list
    }

    fun addOnInstallationProgressChangedListener(onInstallationStateChangedListener: OnInstallationStateChangedListener) {
        onInstallationStateChangedListenerList.add(onInstallationStateChangedListener)
    }

    fun removeOnInstallationStateChangedListener(onInstallationStateChangedListener: OnInstallationStateChangedListener) {
        onInstallationStateChangedListenerList.remove(onInstallationStateChangedListener)
    }

    private fun notifyInstallProgressChange(msg: String) {
        onInstallationStateChangedListenerList.forEach { listener ->
            listener.onProgressChanged(msg)
        }
    }

    private fun notifyInstallSuccess() {
        onInstallationStateChangedListenerList.forEach { listener ->
            listener.onSuccess()
        }
    }

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
