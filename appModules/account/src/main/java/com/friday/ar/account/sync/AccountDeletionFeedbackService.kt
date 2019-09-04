package com.friday.ar.account.sync

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AccountDeletionFeedbackService : JobService() {
    companion object {
        private const val LOGTAG = "AccountDeletionFeedbackService"
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.d(LOGTAG, "Started Feedback service")
        val firestore = FirebaseFirestore.getInstance()
        val docRef = firestore.document("feedback/account_deleted_feedback")
        val extras = jobParameters.extras
        object : Thread() {
            override fun run() {
                val data = HashMap<String, Any>()
                val childData = HashMap<String, Any>()
                childData["reason_value"] = extras.getString("reason")!!
                childData["timestamp"] = Timestamp(Date())
                data[extras.getString("uid")!!] = childData
                val updateFeedback = docRef.set(data)
                updateFeedback.addOnCompleteListener { task ->
                    Log.d(LOGTAG, "Submitted feedback:" + task.isSuccessful)
                    jobFinished(jobParameters, !task.isSuccessful)
                }
            }
        }.start()

        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.d(LOGTAG, "Stopped Feedback service")
        return false
    }
}
