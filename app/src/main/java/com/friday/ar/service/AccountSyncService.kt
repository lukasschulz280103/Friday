package com.friday.ar.service

import android.app.DownloadManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.friday.ar.util.UserUtil
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class AccountSyncService : JobService() {
    var jobParameters: JobParameters? = null
    private val downloadFinishReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val util = UserUtil(applicationContext)
            val avatarFile = util.avatarFile
            context.unregisterReceiver(this)
            Log.d(LOGTAG, "synchronized account avatar")
            try {
                Files.move(getExternalFilesDir("profile/avatar.jpg")!!.toPath(), avatarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                jobFinished(jobParameters, false)
            } catch (e: IOException) {
                Log.e(LOGTAG, e.localizedMessage, e)
            } finally {
                jobFinished(jobParameters, false)
            }
        }
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        this.jobParameters = jobParameters
        Log.d(LOGTAG, "started synchronization service")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            jobFinished(jobParameters, false)
            return false
        }
        if (firebaseUser.photoUrl != null) {
            UserUtil(applicationContext).avatarFile.delete()
            val request = DownloadManager.Request(firebaseUser.photoUrl)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setDestinationInExternalFilesDir(applicationContext, "profile", File.separator + "avatar.jpg")
                    .setVisibleInDownloadsUi(false)
            registerReceiver(downloadFinishReciever, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.d(LOGTAG, "finished synchronization service")
        return false
    }

    companion object {
        private const val LOGTAG = "SynchronizationService"
    }
}
