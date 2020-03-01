package com.friday.ar.core.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.InflateException
import androidx.appcompat.app.AppCompatActivity
import com.friday.ar.core.Constant
import com.friday.ar.core.R
import com.friday.ar.core.Theme
import com.friday.ar.core.util.notifications.BaseNotification
import org.koin.android.ext.android.get

abstract class FridayActivity : AppCompatActivity() {
    lateinit var theme: Theme
    companion object {
        const val LOGTAG = "FridayActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences: SharedPreferences = get()
        theme = get()
        Log.i(LOGTAG, "theme is ${sharedPreferences.getInt("theme", 0)}")
        if (sharedPreferences.getInt("theme", 0) == 0) {
            Log.d(LOGTAG, "initializing theme")
            sharedPreferences.edit().putInt("theme", R.style.AppTheme).apply()
        }
        setTheme(theme.getCurrentAppTheme())
        super.onCreate(savedInstanceState)
        Log.d(LOGTAG, "started activity")
    }

    override fun onStop() {
        Log.d(LOGTAG, "stopped activity")
        super.onStop()
    }

    override fun setContentView(layoutResID: Int) {
        try {
            super.setContentView(layoutResID)
        } catch (e: InflateException) {
            Log.e(LOGTAG, "could not inflate contentView for activity $this! [${e.message}]", e)
            theme.reset()
            BaseNotification(Constant.Notification.NOTIFICATION_FRIDAYACTIVITY_CRASH, Constant.Notification.Channels.NOTIFICAITON_CHANNEL_APP_CRASH)
                    .set(R.string.app_crash_recognized, R.string.app_crash_theme_was_reset, R.drawable.twotone_failure_24dp)
                    .notifyNow()
        }
    }
}
