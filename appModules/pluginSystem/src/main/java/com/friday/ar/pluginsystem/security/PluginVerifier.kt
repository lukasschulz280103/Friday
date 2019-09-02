package com.friday.ar.pluginsystem.security

import android.util.Log
import com.friday.ar.pluginsystem.file.Manifest.ManifestSecurityException
import com.friday.ar.pluginsystem.file.PluginFile
import extensioneer.notNull
import net.lingala.zip4j.exception.ZipException
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.NoSuchFileException

/**
 * <h1>WIP</h1>
 *
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
 */
class PluginVerifier {
    companion object {
        private const val LOGTAG = "PluginVerifier"
    }
    private var onVerificationCompleteListener: OnVerificationCompleteListener? = null


    fun verify(pluginFile: PluginFile, deleteOnException: Boolean) {
        val manifestContentString: String
        try {
            if (pluginFile.manifest == null) throw VerificationSecurityException("No manifest found!")

            manifestContentString = String(Files.readAllBytes(File(pluginFile.path + "/meta/manifest.json").toPath()), StandardCharsets.UTF_8)

            val manifestObject = JSONObject(manifestContentString)
            val meta = manifestObject.getJSONObject("meta")
            if (manifestObject.isNull("meta")) {
                if (deleteOnException) pluginFile.deleteRecursively()
                throw ManifestSecurityException("Manifest does not contain required meta info!")
            }
            if (meta.isNull("applicationName") || meta.getString("applicationName").isEmpty()) {
                if (deleteOnException) pluginFile.deleteRecursively()
                throw ManifestSecurityException.MissingFieldException("The manifest meta info is missing an application name, or its value is empty.")
            }
            if (meta.isNull("authorName") || meta.getString("authorName").isEmpty()) {
                Log.w(LOGTAG, "The plugin '" + pluginFile.name + "' has not provided an authors name and thus is untrusted.")
            }
            if (meta.isNull("versionName") || meta.getString("versionName").isEmpty()) {
                if (deleteOnException) pluginFile.deleteRecursively()
                throw ManifestSecurityException.MissingFieldException("The manifest meta info is missing the versionName, or its value is empty.")
            }
            onVerificationCompleteListener!!.onSuccess()
        } catch (e: NoSuchFileException) {
            if (deleteOnException) pluginFile.deleteRecursively()
            onVerificationCompleteListener.notNull { onVerificationFailed(VerificationSecurityException("Could not find manifest file in the given plugin file.")) }
        } catch (e: IOException) {
            onVerificationCompleteListener.notNull { onIOException(e) }
        } catch (e: JSONException) {
            onVerificationCompleteListener.notNull { onJSONException(e) }
        } catch (e: ManifestSecurityException.MissingFieldException) {
            onVerificationCompleteListener.notNull { onVerificationFailed(e) }
        } catch (e: ManifestSecurityException) {
            onVerificationCompleteListener.notNull { onVerificationFailed(e) }
        } catch (e: VerificationSecurityException) {
            onVerificationCompleteListener.notNull { onVerificationFailed(e) }
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
}
