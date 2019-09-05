package com.friday.ar.core.util.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Helper class for creating basic notifications faster.
 * @param id id of the notification to send
 * @param channnelID channelID of the notification to send
 */
class BaseNotification(val id: Int, val channnelID: String) : IBaseNotification, KoinComponent {

    var notification: Notification.Builder
    val notificationManager: NotificationManager by inject()
    val context: Context by inject()

    init {
        notification = Notification.Builder(context, channnelID)
        notification.style = Notification.BigTextStyle()
    }

    override fun set(title: String, message: String, @DrawableRes icon: Int): Notification.Builder {
        notification.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
        return notification
    }

    override fun set(@StringRes title: Int, @StringRes message: Int, @DrawableRes icon: Int): BaseNotification {
        notification.setContentTitle(context.getString(title))
                .setContentText(context.getString(message))
                .setSmallIcon(icon)
        return this
    }

    /**
     * sends the notification
     */
    override fun notifyNow() {
        notificationManager.notify(id, notification.build())
    }
}