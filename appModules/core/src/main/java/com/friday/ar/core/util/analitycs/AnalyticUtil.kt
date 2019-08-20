package com.friday.ar.core.util.analitycs

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticUtil {
    companion object {
        fun logSuccessfulAuthentication(context: Context, method: String) {
            val bundle = Bundle()
            bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, true)
            bundle.putString(FirebaseAnalytics.Param.METHOD, method)
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        }

        fun logFailedAuthentication(context: Context, method: String, errorCode: String?, errorMessage: String?) {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, method)
            bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, false)
            bundle.putString("errorCode", errorCode)
            bundle.putString("cause", errorMessage)
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        }

        //we currently can't track google Sign-Ups, firebase is not telling us about these. Backend may need to take over this job.
        fun logUserCreation(context: Context) {
            val bundle = Bundle()
            bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, true)
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        }

        fun logFailedUserCreation(context: Context, errorCode: String?, errorMessage: String?) {
            val bundle = Bundle()
            bundle.putBoolean(FirebaseAnalytics.Param.SUCCESS, false)
            bundle.putString("errorCode", errorCode)
            bundle.putString("cause", errorMessage)
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        }
    }
}