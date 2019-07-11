package com.friday.ar.plugin.security

import android.content.Context
import android.util.Log

import com.friday.ar.Constant
import com.friday.ar.plugin.file.Manifest
import com.friday.ar.plugin.file.Manifest.ManifestSecurityException
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.util.FileUtil
import com.friday.ar.util.cache.CacheUtil

import net.lingala.zip4j.exception.ZipException

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.NoSuchFileException

/**
 * Use this class to verify any kind of possible [PluginFile]
 * This class checks the following cases:
 *
 *  * The file contains a manifest.
 *
 *  * The manifest contains the required meta info
 *  * The manifests meta info contains the following keys:**applicationName**, **versionName**
 *
 *
 *
 *
 *
 * <h3>Warning policy</h3>
 * In case of missing and security relevant info, such as an identifier and/or name of the author of a plugin, a warning will be thrown.
 * **Plugins which are installed from the official Friday store are not allowed to have the required info missing and will be blocked from installing.**
 *
 */
class PluginVerifier {

    private var onVerificationCompleteListener: OnVerificationCompleteListener? = null

    /**
     * Verify a plugin which is zipped.
     * Throws an exception if an plugin file seems o be corrupted.
     *
     * @param plugin            [ZippedPluginFile] to run verification on
     * @param context           [Context] application context
     * @param deleteOnException boolean whether the cached file should be deleted if the verification fails.
     */
    fun verify(plugin: ZippedPluginFile, context: Context, deleteOnException: Boolean) {
        try {
            val cachedPluginFile = CacheUtil.cachePluginFile(context, plugin)
            for (file in Constant.getPluginCacheDir(context).listFiles()) {
                Log.d(LOGTAG, file.name)
            }
            val cacheFile = PluginFile(cachedPluginFile.path + "/" + cachedPluginFile.name)
            verify(cacheFile, deleteOnException)
        } catch (e: ZipException) {
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onZipException(e)
            }
        } catch (e: JSONException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onJSONException(e)
            }
        } catch (e: IOException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onIOException(e)
            }
        }

    }

    fun verify(pluginFile: PluginFile, deleteOnException: Boolean) {
        val manifestContentString: String
        try {
            manifestContentString = String(Files.readAllBytes(File(pluginFile.path + "/meta/manifest.json").toPath()), StandardCharsets.UTF_8)
            val manifestOject = JSONObject(manifestContentString)
            val meta = manifestOject.getJSONObject("meta")
            if (meta == null) {
                if (deleteOnException) FileUtil.deleteDirectory(pluginFile)
                throw Manifest.ManifestSecurityException("Manifest does not contain required meta info!")
            }
            if (meta.getString("applicationName") == null || meta.getString("applicationName").isEmpty()) {
                if (deleteOnException) FileUtil.deleteDirectory(pluginFile)
                throw ManifestSecurityException.MissingFieldException("The manifest meta info is missing an application name, or its value is empty.")
            }
            if (meta.getString("authorName") == null || meta.getString("authorName").isEmpty()) {
                Log.w(LOGTAG, "The plugin '" + pluginFile.name + "' has not provided an authors name and thus is untrusted.")
            }
            if (meta.getString("versionName") == null || meta.getString("versionName").isEmpty()) {
                if (deleteOnException) FileUtil.deleteDirectory(pluginFile)
                throw ManifestSecurityException.MissingFieldException("The manifest meta info is missing the versionName, or its value is empty.")
            }
            onVerificationCompleteListener!!.onSuccess()
        } catch (e: NoSuchFileException) {
            if (deleteOnException) FileUtil.deleteDirectory(pluginFile)
            if (onVerificationCompleteListener != null)
                onVerificationCompleteListener!!.onVerificationFailed(VerificationSecurityException("Could not find manifest file in the given plugin file."))
        } catch (e: IOException) {
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onIOException(e)
            }
        } catch (e: JSONException) {
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onJSONException(e)
            }
        } catch (e: ManifestSecurityException.MissingFieldException) {
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onVerificationFailed(e)
            }
        } catch (e: ManifestSecurityException) {
            if (onVerificationCompleteListener != null) {
                onVerificationCompleteListener!!.onVerificationFailed(e)
            }
        }

    }

    fun setOnVerificationCompleteListener(onVerificationCompleteListener: OnVerificationCompleteListener) {
        this.onVerificationCompleteListener = onVerificationCompleteListener
    }

    interface OnVerificationCompleteListener {
        fun onSuccess()
        fun onZipException(e: ZipException)
        fun onIOException(e: IOException)
        fun onJSONException(e: JSONException)
        fun onVerificationFailed(e: VerificationSecurityException)
    }

    companion object {
        private const val LOGTAG = "PluginVerifier"
    }
}
