package com.friday.ar.pluginsystem.service.installer

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.R
import com.friday.ar.pluginsystem.security.VerificationSecurityException
import org.json.JSONException
import java.io.IOException
import java.nio.file.DirectoryNotEmptyException
import java.util.zip.ZipException

class PluginInstallerNotificationService(val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        installerNotifId++
        val summaryNotification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_store_24px)
                //build summary info into InboxStyle template
                //specify which group this notification belongs to
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                //set this notification as the summary for the group
                .setGroupSummary(true)
                .build()
        notificationManager.notify(installerNotifId + 2, summaryNotification)
    }

    companion object {
        var installerNotifId = 0
    }
    fun notificationShowError(exception: Exception) {
        val message = when (exception) {
            is ZipException -> {
                context.getString(R.string.pluginInstaller_error_invalid_zip_file)
            }
            is DirectoryNotEmptyException -> {
                context.getString(R.string.pluginInstaller_error_plugin_already_is_installed)
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
        val notification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setContentTitle(context.getString(R.string.pluginInstaller_error_installation_failed))
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.ic_twotone_error_outline_24px)
                .setStyle(NotificationCompat.BigTextStyle())
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .build()
        notificationManager.notify(installerNotifId, notification)
    }

    fun notificationShowProgress(progressMessage: String) {
        val notification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setProgress(1, 0, true)
                .setContentTitle(context.getString(R.string.pluginInstaller_progressMessage_installing))
                .setContentText(progressMessage)
                .setOngoing(true)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .build()
        notificationManager.notify(installerNotifId, notification)
    }

    fun notificationShowSuccess(name: String) {
        val notification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setContentTitle(context.getString(R.string.pluginInstaller_success_install_title))
                .setContentText(context.getString(R.string.pluginInstaller_success_ticker_text, name))
                .setStyle(NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .build()
        notificationManager.notify(installerNotifId, notification)

    }
}