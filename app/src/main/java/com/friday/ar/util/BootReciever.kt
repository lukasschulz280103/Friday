package com.friday.ar.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import com.friday.ar.account.sync.AccountSyncService
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.service.PluginIndexer
import com.friday.ar.pluginsystem.service.PluginLoader
import org.koin.core.KoinComponent
import org.koin.core.get

class BootReciever : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, pIntent: Intent) {
        if (pIntent.action != Intent.ACTION_BOOT_COMPLETED) return
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val preferences: SharedPreferences = get()
        if (preferences.getBoolean("check_update_auto", false)) {

        }
        if (preferences.getBoolean("sync_account_auto", true)) {
            val info = JobInfo.Builder(Constant.Jobs.JOB_SYNC_ACCOUNT, ComponentName(context, AccountSyncService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria((2 * 60000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
                    .build()
            jobScheduler.schedule(info)
        }
        val pluginLoader = PluginLoader(context)
        if (pluginLoader.startLoading()) {

        }

        val jobIndexerInfo = JobInfo.Builder(Constant.Jobs.JOB_INDEX_PLUGINS, ComponentName(context, PluginIndexer::class.java))
                .setOverrideDeadline(0)
                .setBackoffCriteria((30 * 60000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
                .build()
        jobScheduler.schedule(jobIndexerInfo)
    }
}
