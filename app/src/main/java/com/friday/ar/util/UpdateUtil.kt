package com.friday.ar.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object UpdateUtil {
    private val LOGTAG = "UpdateUtil"
    private val database = FirebaseDatabase.getInstance()
    private val updateRef = database.getReference("version")

    fun checkForUpdate(context: Context) {
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
