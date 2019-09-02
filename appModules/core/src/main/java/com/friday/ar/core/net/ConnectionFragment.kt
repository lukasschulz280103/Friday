package com.friday.ar.core.net

import android.util.Log

import androidx.fragment.app.Fragment
import extensioneer.isNull
import extensioneer.notNull

/**
 * Extendable Fragment class to automatically implement [OnConnectionStateChangedListener]
 * This class is necessary to properly access unknown subclasses
 *
 * @see OnConnectionStateChangedListener
 */
open class ConnectionFragment : Fragment(), OnConnectionStateChangedListener {

    override fun onConnected() {

    }

    override fun onError(e: Exception) {
        e.message.notNull {
            Log.d("ConnectionFragment", "ErrorCallback error:$this in fragment ${this@ConnectionFragment}")
        }.isNull {
            Log.d("ConnectionFragment", "error in ConnectionFragment: ${this@ConnectionFragment}")
        }
    }
}

