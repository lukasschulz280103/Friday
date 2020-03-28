package com.friday.ar.base.system.startup

import android.util.Log
import com.friday.ar.base.system.environment.SystemCallback

/**
 * This manager takes care of plugins that are listed in the registry.
 */
class StartupManager : SystemCallback {
    companion object {
        const val LOGTAG = "FridaySystemStartup"
    }

    override fun onStartSystem() {
        Log.i(LOGTAG, "System is calling StartupManager; running StartupManager")

    }

}