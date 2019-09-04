package com.friday.ar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Build
import com.crashlytics.android.Crashlytics
import com.friday.ar.account.sync.AccountSyncService
import com.friday.ar.core.Constant
import com.friday.ar.di.moduleList
import com.friday.ar.pluginsystem.file.ZippedPluginFile
import com.friday.ar.pluginsystem.service.PluginIndexer
import com.friday.ar.pluginsystem.service.PluginLoader
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.File

//TODO remove data handling from application class to sperate models
/**
 * Application class
 */
class FridayApplication : Application() {
    companion object{
        lateinit var pluginLoader: PluginLoader
        lateinit var indexedPlugins: ArrayList<ZippedPluginFile>
    }

    val pluginIndexerReciever = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            if(p1 != null && p1.extras != null){
                indexedPlugins = p1.extras!!.getSerializable("indexList") as ArrayList<ZippedPluginFile>
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@FridayApplication)
            modules(moduleList())
        }

        Fabric.with(this, Crashlytics())

        if (!File(externalCacheDir!!.toString() + "/pluginZipCache").delete()) {
            File(externalCacheDir!!.toString() + "/pluginZipCache").deleteOnExit()
        }
        Constant.getPluginDir(this).delete()
        Thread {
            registerReceiver(pluginIndexerReciever, IntentFilter(Constant.BroadcastReceiverActions.BROADCAST_PLUGINS_INDEXED))
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
            nm!!.createNotificationChannels(listOf(updateChannel, pluginInstallerChannel))
        }
    }

    private fun runServices() {
        val preferences: SharedPreferences = get()

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if (preferences.getBoolean("sync_account_auto", true)) {
            val info = JobInfo.Builder(Constant.Jobs.JOB_SYNC_ACCOUNT, ComponentName(this, AccountSyncService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria((2 * 60000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
                    .build()
            jobScheduler.schedule(info)
        }

        val jobIndexerInfo = JobInfo.Builder(Constant.Jobs.JOB_INDEX_PLUGINS, ComponentName(this, PluginIndexer::class.java))
                .setBackoffCriteria((30 * 60000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
                .setOverrideDeadline(0)
                .build()
        jobScheduler.schedule(jobIndexerInfo)

        pluginLoader = PluginLoader(this)
        pluginLoader.startLoading()
    }
}
