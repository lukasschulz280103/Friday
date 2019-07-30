package com.friday.ar.service

import android.app.DownloadManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.friday.ar.Constant
import com.friday.ar.util.UserUtil
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class AccountSyncService : Service() {
    companion object {
        private const val LOGTAG = "SynchronizationService"
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(LOGTAG, "started synchronization service")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.photoUrl != null) {
            UserUtil(applicationContext).avatarFile.delete()
            val request = DownloadManager.Request(firebaseUser.photoUrl)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setDestinationInExternalFilesDir(applicationContext, "profile", "/avatar.jpg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            registerReceiver(downloadFinishReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
        return SyncServiceBinder()
    }

    private val downloadFinishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            context.unregisterReceiver(this)
            val util = UserUtil(applicationContext)
            val avatarFile = util.avatarFile
            Log.d(LOGTAG, "synchronized account avatar")
            try {
                Files.move(getExternalFilesDir("profile/avatar.jpg")!!.toPath(), avatarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                val avatarSyncedIntent = Intent()
                avatarSyncedIntent.action = Constant.BroadcasteceiverActions.BROADCAST_ACCOUNT_SYNCED
                sendBroadcast(avatarSyncedIntent)
            } catch (e: IOException) {
                Log.e(LOGTAG, e.localizedMessage, e)
            }
        }
    }

    inner class SyncServiceBinder : Binder() {
        fun getService(): AccountSyncService = this@AccountSyncService
    }
}
