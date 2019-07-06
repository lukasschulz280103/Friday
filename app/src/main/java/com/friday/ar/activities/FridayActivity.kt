package com.friday.ar.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


/*
This file is necessarry for analytics, in order to record usage statistics.
 */
abstract class FridayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOGTAG, "started activity")
    }

    override fun onStop() {
        Log.d(LOGTAG, "stopped activity")
        super.onStop()
    }

    companion object {
        val LOGTAG = "FridayActivity"
    }
}
