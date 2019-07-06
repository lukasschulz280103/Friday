package com.friday.ar.plugin.installer

import android.app.NotificationManager
import android.content.Context
import android.util.Log

import androidx.core.app.NotificationCompat

import com.friday.ar.Constant
import com.friday.ar.R
import com.friday.ar.plugin.file.PluginFile
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.plugin.security.PluginVerifier
import com.friday.ar.plugin.security.VerificationSecurityException

import net.lingala.zip4j.exception.ZipException

import org.json.JSONException

import java.io.File
import java.io.IOException

class PluginInstaller(private val context: Context) {
    private var onInstallProgressChangedListener: OnInstallProgressChangedListener? = null

    @Throws(IOException::class)
    fun installFrom(pluginDir: ZippedPluginFile) {
        val zippedPluginDefaultFile = pluginDir.file
        try {
            pluginDir.extractAll(Constant.getPluginCacheDir(context).path)
            val extractionTargetDirectory = PluginFile(Constant.getPluginCacheDir(context, zippedPluginDefaultFile.name.replace(".fpl", "")).path)
            val verifier = PluginVerifier()
            val notificationBuilder = NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
            verifier.setOnVerificationCompleteListener(object : PluginVerifier.OnVerificationCompleteListener {
                override fun onSuccess() {
                    try {
                        Log.e(LOGTAG, "pluginInstaller success")
                        File(zippedPluginDefaultFile.path).renameTo(Constant.getPluginDir(context, zippedPluginDefaultFile.name))
                        val successPluginManifest = PluginFile(Constant.getPluginDir(context, zippedPluginDefaultFile.name.replace(".fpl", "")).path).manifest
                        notificationBuilder.setContentTitle(context.getString(R.string.pluginInstaller_succes_install_title))
                                .setContentText(zippedPluginDefaultFile.name)
                                .setSubText(context.getString(R.string.pluginInstaller_name))
                                .setCategory(NotificationCompat.CATEGORY_STATUS)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setSmallIcon(R.drawable.ic_twotone_save_alt_24px)
                                .setTicker(context.getString(R.string.pluginInstaller_success_ticker_text, successPluginManifest.pluginName))
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_SUCCESS, notificationBuilder.build())
                    } catch (e: JSONException) {
                        Log.e(LOGTAG, e.localizedMessage, e)
                    } catch (e: IOException) {
                        Log.e(LOGTAG, e.localizedMessage, e)
                    }

                    onInstallProgressChangedListener!!.onSuccess()
                    extractionTargetDirectory.renameTo(Constant.getPluginDir(context, zippedPluginDefaultFile.name.replace(".fpl", "")))
                }

                override fun onZipException(e: ZipException) {
                    notificationBuilder.setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                            .setContentText(context.getString(R.string.pluginInstaller_error_could_not_parse))
                            .setContentInfo(zippedPluginDefaultFile.name)
                            .setSubText(context.getString(R.string.pluginInstaller_name))
                            .setCategory(NotificationCompat.CATEGORY_ERROR)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                            .setTicker(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_ERROR, notificationBuilder.build())
                    onInstallProgressChangedListener!!.onFailure()
                }

                override fun onIOException(e: IOException) {
                    notificationBuilder.setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                            .setContentText(context.getString(R.string.pluginInstaller_error_could_not_parse))
                            .setContentInfo(zippedPluginDefaultFile.name)
                            .setSubText(context.getString(R.string.pluginInstaller_name))
                            .setCategory(NotificationCompat.CATEGORY_ERROR)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                            .setTicker(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_ERROR, notificationBuilder.build())
                    onInstallProgressChangedListener!!.onFailure()
                }

                override fun onJSONException(e: JSONException) {
                    Log.e(LOGTAG, e.localizedMessage, e)
                    notificationBuilder.setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                            .setContentText(context.getString(R.string.pluginInstaller_error_could_not_parse))
                            .setContentInfo(zippedPluginDefaultFile.name)
                            .setSubText(context.getString(R.string.pluginInstaller_name))
                            .setCategory(NotificationCompat.CATEGORY_ERROR)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                            .setTicker(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_ERROR, notificationBuilder.build())
                    onInstallProgressChangedListener!!.onFailure()
                }

                override fun onVerificationFailed(e: VerificationSecurityException) {
                    Log.e(LOGTAG, e.localizedMessage, e)
                    notificationBuilder.setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                            .setContentText(context.getString(R.string.pluginInstaller_error_manifest_security))
                            .setContentInfo(zippedPluginDefaultFile.name)
                            .setSubText(context.getString(R.string.pluginInstaller_name))
                            .setCategory(NotificationCompat.CATEGORY_ERROR)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                            .setTicker(context.getString(R.string.pluginInstaller_error_could_not_parse))
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(Constant.NotificationIDs.NOTIFICATION_INSTALL_ERROR, notificationBuilder.build())
                    onInstallProgressChangedListener!!.onFailure()
                }
            })
            verifier.verify(extractionTargetDirectory, true)
        } catch (e: ZipException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            onInstallProgressChangedListener!!.onFailure()
        } catch (e: JSONException) {
            Log.e(LOGTAG, e.localizedMessage, e)
            onInstallProgressChangedListener!!.onFailure()
        }

    }

    fun setOnInstallProgressChangedListener(onInstallProgressChangedListener: OnInstallProgressChangedListener) {
        this.onInstallProgressChangedListener = onInstallProgressChangedListener
    }

    interface OnInstallProgressChangedListener {
        fun onProgressChanged(progressMessage: String)
        fun onSuccess()
        fun onFailure()
    }

    companion object {
        private const val LOGTAG = "PluginInstaller"
    }
}
