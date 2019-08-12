package com.friday.ar.service.plugin.installer

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.friday.ar.Constant
import com.friday.ar.R

class PluginInstallerNotificationService(val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    fun notificationShowError(message: String) {
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
                .setSubText(context.getString(R.string.pluginInstaller_success_ticker_text, name))
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_INSTALLER)
                .build()
        notificationManager.notify(Constant.Notification.NOTIFICATION_INSTALL_PROGRESS, notification)

    }
}