package com.friday.ar.util

import android.content.Context
import android.net.ConnectivityManager

object Connectivity {
    fun isConnected(c: Context): Boolean {
        val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
