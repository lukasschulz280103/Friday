package com.friday.ar.pluginsystem.service.installer

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.R

class UninstallNotificationHelper(context: Context) : IPluginInstallerNotificationManager(context) {

    init {
        notificationUninstallerId++
        val uninstallerSummaryNotification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setGroupSummary(true)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_UNINSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_store_24px)
                .build()
        pluginNotificationManager.notify(notificationUninstallerId + 2, uninstallerSummaryNotification)
    }

    companion object {
        private var notificationUninstallerId = 0
    }

    override fun notificationShowError(e: Exception) {
        val notification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setProgress(1, 0, true)
                .setContentTitle(context.getString(R.string.pluginInstaller_error_uninstall_failed))
                .setContentText(e.message)
                .setOngoing(true)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_UNINSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .build()
        pluginNotificationManager.notify(notificationUninstallerId, notification)
    }

    override fun notificationShowSuccess(message: String) {
        //cancel progress notification
        val notification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setProgress(1, 0, true)
                .setContentTitle(context.getString(R.string.pluginInstaller_success_uninstall_title))
                .setContentText(message)
                .setOngoing(true)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_UNINSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setProgress(0, 0, false)
                .build()
        pluginNotificationManager.notify(notificationUninstallerId, notification)
    }

    override fun notificationUpdateProgress(message: String) {
        val notification = NotificationCompat.Builder(context, Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER)
                .setProgress(1, 0, true)
                .setContentTitle(context.getString(R.string.pluginInstaller_uninstall_progress_title))
                .setContentText(message)
                .setOngoing(true)
                .setGroup(Constant.Notification.Groups.NOTIFICATION_GROUP_UNINSTALLER)
                .setSmallIcon(R.drawable.ic_twotone_archive_24px)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .build()
        pluginNotificationManager.notify(notificationUninstallerId, notification)
    }
}