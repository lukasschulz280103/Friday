package com.friday.ar.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.friday.ar.Constant
import com.friday.ar.R
import com.friday.ar.ui.InfoActivity

object NotificationUtil {

    fun notifyUpdateAvailable(context: Context, newVersion: String) {
        val toInfoPage = Intent(context, InfoActivity::class.java)
        val notificationClick = PendingIntent.getActivity(context, 2, toInfoPage, 0)
        val nmc = NotificationManagerCompat.from(context)
        val updateAvailableNotification = NotificationCompat.Builder(context, Constant.NOTIF_CHANNEL_UPDATE_ID)
                .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_update_description, newVersion))
                .setContentIntent(notificationClick)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .build()
        nmc.notify(1, updateAvailableNotification)
    }
}
