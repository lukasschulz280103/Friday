package com.friday.ar.pluginsystem.service.installer

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Build
import com.friday.ar.core.Constant
import com.friday.ar.pluginsystem.Plugin
import com.friday.ar.pluginsystem.db.LocalPluginsDB
import com.friday.ar.pluginsystem.service.PluginLoader
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class DatabaseUpdateHelper : KoinComponent {
    val pluginDB by inject<LocalPluginsDB>()
    val jobScheduler by inject<JobScheduler>()

    fun updatePlugins() {
        val jobPluginloaderInfo = JobInfo.Builder(Constant.Jobs.JOB_INDEX_INSTALLED_PLUGINS, ComponentName(get(), PluginLoader::class.java))
                .setBackoffCriteria(60000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setOverrideDeadline(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            jobPluginloaderInfo
                    .setImportantWhileForeground(true)
                    .setPrefetch(true)
        }
        jobScheduler.schedule(jobPluginloaderInfo.build())
    }

    fun remove(plugin: Plugin) {
        pluginDB.indexedPluginsDAO().removePlugin(plugin)
    }
}