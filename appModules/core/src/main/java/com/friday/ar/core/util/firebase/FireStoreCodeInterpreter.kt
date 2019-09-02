package com.friday.ar.core.util.firebase


import android.content.Context
import com.friday.ar.core.R

import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Converts [com.google.firebase.firestore.FirebaseFirestore]'s [FirebaseFirestoreException.getCode] to an multilingual information
 */
class FireStoreCodeInterpreter(c: Context, e: FirebaseFirestoreException) {
    /**
     * get the original [FirebaseFirestoreException]. Implemented to generify the codee where this method is used to fully use this class
     *
     * @return the original, in the constructor passed [FirebaseFirestoreException]
     */
    val exception: FirebaseFirestoreException? = null

    /**
     * @return get the integer casted error code
     */
    val code: Int = e.code.value()

    /**
     * Get the translated message
     *
     * @return translated [FirebaseFirestoreException]'s message
     */
    var message: String? = null
        private set

    /**
     * @return original [FirebaseFirestoreException]'s message
     */
    val exceptionMessage: String = e.message!!
    private var shouldShowRetryButton = true

    init {
        when (e.code) {
            FirebaseFirestoreException.Code.CANCELLED -> message = c.getString(R.string.firebase_code_cancelled)
            FirebaseFirestoreException.Code.UNKNOWN -> message = c.getString(R.string.unknown_error)
            FirebaseFirestoreException.Code.INVALID_ARGUMENT -> message = c.getString(R.string.firebase_invalid_arg)
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> message = c.getString(R.string.firebase_code_deadline_exceeded)
            FirebaseFirestoreException.Code.NOT_FOUND -> message = c.getString(R.string.firebase_code_not_found)
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> message = c.getString(R.string.firebase_code_perm_denied)
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> message = c.getString(R.string.firebase_code_exhausted)
            FirebaseFirestoreException.Code.ABORTED -> message = c.getString(R.string.firebase_code_aborted)
            FirebaseFirestoreException.Code.INTERNAL -> {
                message = c.getString(R.string.firebase_code_internal)
                shouldShowRetryButton = false
            }
            FirebaseFirestoreException.Code.UNAVAILABLE -> message = c.getString(R.string.firebase_code_unavailable)
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> message = c.getString(R.string.firebase_code_unauthenticated)
            else -> c.getString(R.string.unknown_error)
        }
    }
}
