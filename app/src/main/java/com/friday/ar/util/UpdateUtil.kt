package com.friday.ar.util

import android.content.Context
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UpdateUtil {
    private const val LOGTAG = "UpdateUtil"
    fun checkForUpdate(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val database = FirebaseDatabase.getInstance()
        val updateRef = if (sharedPreferences.getBoolean("pref_devmode_use_beta_channel", false)) database.getReference("versionPre") else database.getReference("version")
        updateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (context.packageManager.getPackageInfo(context.packageName, 0).versionName != dataSnapshot.value!!.toString()) {
                        NotificationUtil.notifyUpdateAvailable(context, dataSnapshot.value!!.toString())
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e(LOGTAG, e.localizedMessage, e)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(LOGTAG, "Could not check for update:" + databaseError.message + "\nDetails:" + databaseError.details, databaseError.toException())
                Crashlytics.logException(databaseError.toException())
            }
        })
    }
}
