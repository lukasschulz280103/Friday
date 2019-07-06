package com.friday.ar.fragments.net

interface OnConnectionStateChangedListener {
    fun onConnected()
    fun onError(e: Exception)
}
