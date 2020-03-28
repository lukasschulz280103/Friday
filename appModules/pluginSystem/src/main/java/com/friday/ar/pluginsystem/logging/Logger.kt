package com.friday.ar.pluginsystem.logging

import android.util.Log
import com.ars.ar.sdk.sys.Context

class Logger(val context: Context) {
    fun log(tag: String, msg: String) {
        Log.d("PluginSys/${context.pluginName}", "$tag: $msg")
    }
}