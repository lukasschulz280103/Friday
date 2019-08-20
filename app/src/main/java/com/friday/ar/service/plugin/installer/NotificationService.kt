package com.friday.ar.service.plugin.installer

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.friday.ar.R
import com.friday.ar.core.Constant
import com.friday.ar.plugin.security.VerificationSecurityException
import org.json.JSONException
import java.io.IOException
import java.util.zip.ZipException

class PluginInstallerNotificationService(val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun notificationShowError(exception: Exception) {
        val message = when (exception) {
            is ZipException -> {
                context.getString(R.string.pluginInstaller_error_invalid_zip_file)
            }
            is IOException -> {
                context.getString(R.string.pluginInstaller_error_io_exception)
            }
            is JSONException -> {
                context.getString(R.string.pluginInstaller_error_could_not_parse)
            }
            is VerificationSecurityException -> {
                context.getString(R.string.pluginInstaller_error_manifest_security)
            }
            else -> context.getString(R.string.unknown_error)
        }
        val notification = NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
                .setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                .setStyle(NotificationCompat.BigTextStyle())
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .build()
        notificationManager.notify(Constant.Notification.NOTIFICATION_INSTALL_PROGRESS, notification)
    }

    fun notificationShowProgress(progressMessage: String) {
        val notification = NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
                .setProgress(1, 0, true)
                .setContentTitle(context.getString(R.string.pluginInstaller_progressMessage_installing))
                .setContentText(progressMessage)
                .setOngoing(true)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .build()
        notificationManager.notify(Constant.Notification.NOTIFICATION_INSTALL_PROGRESS, notification)
    }

    fun notificationShowSuccess(name: String) {
        val notification = NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_INSTALLER_ID)
                .setContentTitle(context.getString(R.string.pluginInstaller_success_install_title))
                .setContentText(context.getString(R.string.pluginInstaller_success_ticker_text, name))
                .setStyle(NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .build()
        notificationManager.notify(Constant.Notification.NOTIFICATION_INSTALL_PROGRESS, notification)

    }
}