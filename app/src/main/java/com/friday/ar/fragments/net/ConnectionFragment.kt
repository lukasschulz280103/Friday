package com.friday.ar.fragments.net

import android.util.Log

import androidx.fragment.app.Fragment

import com.friday.ar.fragments.store.ErrorFragment

/**
 * Extendable Fragment class to automatically implement [OnConnectionStateChangedListener]
 * This class is necessary to properly access unknown subclasses
 *
 * @see OnConnectionStateChangedListener
 *
 * @see ErrorFragment
 */
open class ConnectionFragment : Fragment(), OnConnectionStateChangedListener {

    override fun onConnected() {

    }

    override fun onError(e: Exception) {
        Log.e("ConnectionFragment", e.message)
    }
}

