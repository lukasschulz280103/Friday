package com.friday.ar.base.system

import android.content.Context
import android.util.Log
import com.friday.ar.base.system.startup.StartupManager
import org.koin.core.KoinComponent
import org.koin.core.inject

open class FridaySystem(context: Context) : KoinComponent {
    companion object {
        const val LOGTAG = "FridayBaseSystem"
    }

    private val startupManager: StartupManager by inject()

    open fun runSystem() {
        Log.i(LOGTAG, "starting friday system")
        startupManager.onStartSystem()
    }
}