package com.friday.ar.base.system.environment

import android.content.Context

/**
 * <b>This interface describes a [Context] within the Friday-System!</b>
 * <i>Do not confuse this with [android.content.Context]
 */
interface Context {
    val pluginId: String

    val applicationContext: Context
}