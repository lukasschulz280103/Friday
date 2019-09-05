package com.friday.ar.core.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.friday.ar.core.R
import com.friday.ar.core.Theme
import org.koin.android.ext.android.get


/*
This file is necessary for analytics, in order to record usage statistics.
 */
abstract class FridayActivity : AppCompatActivity() {
    companion object {
        const val LOGTAG = "FridayActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences: SharedPreferences = get()
        if (sharedPreferences.getInt("theme", 0) == 0) {
            sharedPreferences.edit().putInt("theme", R.style.AppTheme).apply()
        }
        setTheme(get<Theme>().getCurrentAppTheme())
        super.onCreate(savedInstanceState)
        Log.d(LOGTAG, "started activity")
    }

    override fun onStop() {
        Log.d(LOGTAG, "stopped activity")
        super.onStop()
    }
}
