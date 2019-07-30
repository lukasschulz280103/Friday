package com.friday.ar.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


/*
This file is necessary for analytics, in order to record usage statistics.
 */
abstract class FridayActivity : AppCompatActivity() {
    companion object {
        const val LOGTAG = "FridayActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOGTAG, "started activity")
    }

    override fun onStop() {
        Log.d(LOGTAG, "stopped activity")
        super.onStop()
    }
}
