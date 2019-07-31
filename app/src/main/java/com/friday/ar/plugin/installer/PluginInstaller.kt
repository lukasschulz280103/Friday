package com.friday.ar.plugin.installer

import android.content.Context
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.FridayApplication
import com.friday.ar.R
import com.friday.ar.plugin.Plugin
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.security.PluginVerifier
import com.friday.ar.plugin.security.VerificationSecurityException
import com.friday.ar.util.FileUtil
import com.friday.ar.util.cache.PluginFileCacheUtil
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PluginInstaller(private val context: Context) {
    companion object {
        private const val LOGTAG = "PluginInstaller"
    }

    private var onInstallationStateChangedListenerList: ArrayList<OnInstallationStateChangedListener> = ArrayList()

    @Throws(IOException::class)
    fun installFrom(pluginDir: ZippedPluginFile) {
        val verifier = PluginVerifier()
        val cachedPluginFile = PluginFileCacheUtil.cachePluginFile(context, pluginDir)
        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
            override fun onSuccess() {
                notifyInstallProgressChange(context.getString(R.string.pluginInstaller_progressMessage_installing))
                Files.move(cachedPluginFile.toPath(), Constant.getPluginDir(context, cachedPluginFile.name).toPath(), StandardCopyOption.REPLACE_EXISTING)
                notifyInstallSuccess()
                cachedPluginFile.deleteRecursively()
            }

            override fun onZipException(e: ZipException) {
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }

            override fun onIOException(e: IOException) {
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }

            override fun onJSONException(e: JSONException) {
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }

            override fun onVerificationFailed(e: VerificationSecurityException) {
                Log.e(LOGTAG, e.message, e)
                notifyInstallationFailed(e)
            }
        })
        notifyInstallProgressChange(context.getString(R.string.pluginInstaller_progressMessage_verifying))
        verifier.verify(cachedPluginFile, true)
    }

    fun uninstallPlugin(plugin: Plugin) {
        Log.i(LOGTAG, "uninstalling plugin:${plugin.name}")
        FileUtil.deleteDirectory(plugin.pluginFile)
        (context.applicationContext as FridayApplication).applicationPluginLoader!!.indexedPlugins.remove(plugin)
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
