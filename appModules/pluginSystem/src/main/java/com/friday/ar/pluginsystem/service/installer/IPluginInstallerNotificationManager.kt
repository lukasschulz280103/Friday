package com.friday.ar.pluginsystem.service.installer

import android.app.NotificationManager
import android.content.Context

abstract class IPluginInstallerNotificationManager(val context: Context) {
    protected val pluginNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    abstract fun notificationShowError(e: Exception)
    abstract fun notificationUpdateProgress(message: String)
    abstract fun notificationShowSuccess(message: String)
}