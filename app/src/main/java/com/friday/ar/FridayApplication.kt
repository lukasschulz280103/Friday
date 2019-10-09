package com.friday.ar

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
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
            val appRelatedNotifChannelgroup = NotificationChannelGroup(
                    Constant.Notification.ChannelGroups.NOTIFICATION_CHANNEL_GROUP_APP_RELATED,
                    getString(R.string.notification_channel_group_app_related)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appRelatedNotifChannelgroup.description = getString(R.string.notification_channel_group_app_related_description)
            }

            val storeNotifChannelGroup = NotificationChannelGroup(
                    Constant.Notification.ChannelGroups.NOTIFICATION_CHANNEL_GROUP_STORE,
                    getString(R.string.notification_channel_group_store)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                storeNotifChannelGroup.description = getString(R.string.notification_channel_group_store_description)
            }

            val nm = getSystemService(NotificationManager::class.java)
            nm!!.createNotificationChannelGroups(listOf(appRelatedNotifChannelgroup, storeNotifChannelGroup))

            val updateChannel = NotificationChannel(
                    Constant.Notification.Channels.NOTIFICATION_CHANNEL_UPDATE,
                    getString(R.string.notification_channel_update),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            updateChannel.description = getString(R.string.notification_channel_update_description)
            updateChannel.group = Constant.Notification.ChannelGroups.NOTIFICATION_CHANNEL_GROUP_APP_RELATED

            val appCrashChannel = NotificationChannel(
                    Constant.Notification.Channels.NOTIFICAITON_CHANNEL_APP_CRASH,
                    getString(R.string.notification_channel_app_crash),
                    NotificationManager.IMPORTANCE_HIGH
            )
            appCrashChannel.description = getString(R.string.notification_channel_app_crash_description)
            appCrashChannel.group = Constant.Notification.ChannelGroups.NOTIFICATION_CHANNEL_GROUP_APP_RELATED

            val pluginInstallerChannel = NotificationChannel(
                    Constant.Notification.Channels.NOTIFICATION_CHANNEL_INSTALLER,
                    getString(R.string.notification_channel_plugin_installer),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            pluginInstallerChannel.description = getString(R.string.notification_channel_plugin_installer_description)
            appCrashChannel.group = Constant.Notification.ChannelGroups.NOTIFICATION_CHANNEL_GROUP_STORE

            nm.createNotificationChannels(listOf(updateChannel, pluginInstallerChannel, appCrashChannel))
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
