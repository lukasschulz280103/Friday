package com.friday.ar.core.util.notifications

import android.app.Notification
import androidx.annotation.DrawableRes

interface IBaseNotification {
    fun set(title: String, message: String, @DrawableRes icon: Int): Notification.Builder
    fun set(@DrawableRes title: Int, @DrawableRes message: Int, @DrawableRes icon: Int): IBaseNotification
    fun notifyNow()
}