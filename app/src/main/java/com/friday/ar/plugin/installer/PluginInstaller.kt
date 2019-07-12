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
import com.friday.ar.util.cache.CacheUtil
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PluginInstaller(private val context: Context) {
    private var onInstallProgressChangedListener: OnInstallProgressChangedListener? = null

    @Throws(IOException::class)
    fun installFrom(pluginDir: ZippedPluginFile) {
        val verifier = PluginVerifier()
        verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
            override fun onSuccess() {
                val cachedPluginFile = CacheUtil.cachePluginFile(context, pluginDir)
                onInstallProgressChangedListener!!.onProgressChanged(context.getString(R.string.pluginInstaller_progressMessage_installing))
                Files.move(cachedPluginFile.toPath(), Constant.getPluginDir(context, cachedPluginFile.name).toPath(), StandardCopyOption.REPLACE_EXISTING)
                onInstallProgressChangedListener!!.onSuccess()
            }

            override fun onZipException(e: ZipException) {
                onInstallProgressChangedListener!!.onFailure(e)
            }

            override fun onIOException(e: IOException) {
                onInstallProgressChangedListener!!.onFailure(e)
            }

            override fun onJSONException(e: JSONException) {
                onInstallProgressChangedListener!!.onFailure(e)
            }

            override fun onVerificationFailed(e: VerificationSecurityException) {
                onInstallProgressChangedListener!!.onFailure(e)
            }
        })
        onInstallProgressChangedListener!!.onProgressChanged(context.getString(R.string.pluginInstaller_progressMessage_verifying))
        verifier.verify(pluginDir, context, true)

    }

    fun uninstallPlugin(plugin: Plugin) {
        Log.i(LOGTAG, "uninstalling plugin:${plugin.name}")
        FileUtil.deleteDirectory(plugin.pluginFile)
        (context.applicationContext as FridayApplication).applicationPluginLoader!!.indexedPlugins.remove(plugin)
    }

    fun setOnInstallProgressChangedListener(onInstallProgressChangedListener: OnInstallProgressChangedListener) {
        this.onInstallProgressChangedListener = onInstallProgressChangedListener
    }

    interface OnInstallProgressChangedListener {
        fun onProgressChanged(progressMessage: String)
        fun onSuccess()
        fun onFailure(e: Exception)
    }

    companion object {
        private const val LOGTAG = "PluginInstaller"
    }
}
