package com.friday.ar.account.sync

import android.app.DownloadManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.friday.ar.account.data.UserStore
import com.friday.ar.core.Constant
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class AccountSyncService : JobService() {
    companion object {
        private const val LOGTAG = "SynchronizationService"
    }

    val userStore: UserStore by inject()

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(LOGTAG, "started synchronization service")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.photoUrl != null) {
            UserStore(applicationContext).avatarFile.delete()
            val request = DownloadManager.Request(firebaseUser.photoUrl)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setDestinationInExternalFilesDir(applicationContext, "profile", "/avatar.jpg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            registerReceiver(downloadFinishReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    private val downloadFinishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            context.unregisterReceiver(this)
            val avatarFile = userStore.avatarFile
            Log.d(LOGTAG, "Synchronized account avatar")
            try {
                Files.move(getExternalFilesDir(Constant.Account.AVATAR_FILE_URI)!!.toPath(), avatarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                val avatarSyncedIntent = Intent()
                avatarSyncedIntent.action = Constant.BroadcastReceiverActions.BROADCAST_ACCOUNT_SYNCED
                sendBroadcast(avatarSyncedIntent)
            } catch (e: IOException) {
                Log.e(LOGTAG, e.localizedMessage, e)
            }
        }
    }
}
