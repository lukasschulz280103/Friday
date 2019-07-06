package com.friday.ar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.friday.ar.plugin.application.PluginLoader
import com.friday.ar.plugin.file.ZippedPluginFile
import com.friday.ar.service.AccountSyncService
import com.friday.ar.service.OnAccountSyncStateChanged
import com.friday.ar.service.OnAccountSyncStateChangedList
import com.friday.ar.service.PluginIndexer
import com.friday.ar.util.UpdateUtil
import edu.cmu.pocketsphinx.SpeechRecognizer
import io.fabric.sdk.android.Fabric
import java.io.File
import java.util.*

/**
 * Application class
 */
class FridayApplication : Application(), OnAccountSyncStateChanged {
    /**
     * This is the global [SpeechRecognizer].
     * Its purpose is to transform speech input from the voice assistant into text.
     * Loaded when [com.friday.ar.ui.MainActivity]'s onCreate() is called.
     */
    val speechToTextRecognizer: SpeechRecognizer? = null

    var indexedInstallablePluginFiles = ArrayList<ZippedPluginFile>()

    /**
     * This variable is the application global plugin loader.
     * Use this variable to get information about installed plugins, or to interact with them
     */
    var applicationPluginLoader: PluginLoader? = null
        private set
    private val applicationPluginIndexer: PluginIndexer? = null


    private val syncStateChangedNotifyList = OnAccountSyncStateChangedList<OnAccountSyncStateChanged>()

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        if (!File(externalCacheDir!!.toString() + "/pluginZipCache").delete()) {
            File(externalCacheDir!!.toString() + "/pluginZipCache").deleteOnExit()
        }
        Constant.getPluginDir(this).delete()
        Thread {
            runServices()
            createNotificationChannels()
        }.start()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val updateChannel = NotificationChannel(
                    Constant.NOTIF_CHANNEL_UPDATE_ID,
                    getString(R.string.notification_channel_update),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            updateChannel.description = getString(R.string.notification_channel_update_description)
            val pluginInstallerChannel = NotificationChannel(
                    Constant.NOTIF_CHANNEL_INSTALLER_ID,
                    getString(R.string.notification_channel_plugin_installer),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            pluginInstallerChannel.description = getString(R.string.notification_channel_plugin_installer_description)
            val nm = getSystemService(NotificationManager::class.java)
            nm!!.createNotificationChannels(Arrays.asList(updateChannel, pluginInstallerChannel))
        }
    }


    fun <Object : OnAccountSyncStateChanged> registerForSyncStateChange(context: Object?) {
        if (context != null) {
            syncStateChangedNotifyList.add(context)
        } else {
            throw NullPointerException("")
        }
    }

    fun unregisterForSyncStateChange(context: Any) {
        syncStateChangedNotifyList.remove(context)
    }

    private fun runServices() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this@FridayApplication)
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if (preferences.getBoolean("sync_account_auto", true)) {
            val info = JobInfo.Builder(FridayApplication.Jobs.JOB_SYNC_ACCOUNT, ComponentName(this, AccountSyncService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria((2 * 60000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
                    .build()
            jobScheduler.schedule(info)
        }

        val jobIndexerInfo = JobInfo.Builder(FridayApplication.Jobs.JOB_INDEX_PLUGINS, ComponentName(this, PluginIndexer::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setOverrideDeadline(0)
                .setBackoffCriteria((30 * 60000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
                .build()
        jobScheduler.schedule(jobIndexerInfo)

        applicationPluginLoader = PluginLoader(this)
        applicationPluginLoader!!.startLoading()

        UpdateUtil.checkForUpdate(this)
    }

    override fun onSyncStateChanged() {
        for (listener in syncStateChangedNotifyList) {
            listener.onSyncStateChanged()
        }
    }

    object Jobs {
        val JOB_SYNC_ACCOUNT = 8000
        val JOB_FEEDBACK = 8001
        val JOB_INDEX_PLUGINS = 8002
    }

}
