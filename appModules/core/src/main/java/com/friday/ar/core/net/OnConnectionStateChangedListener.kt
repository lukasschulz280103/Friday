package com.friday.ar.core.net

interface OnConnectionStateChangedListener {
    fun onConnected()
    fun onError(e: Exception)
}
