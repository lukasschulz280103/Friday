package com.friday.ar.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.friday.ar.FridayApplication;
import com.friday.ar.plugin.application.PluginLoader;
import com.friday.ar.service.AccountSyncService;
import com.friday.ar.service.PluginIndexer;

public class BootReciever extends BroadcastReceiver {
    private static final String LOGTAG = "BootReciever";

    @Override
    public void onReceive(final Context context, Intent pIntent) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("check_update_auto", false)) {
            UpdateUtil.checkForUpdate(context);
        }
        if (preferences.getBoolean("sync_account_auto", true)) {
            JobInfo info = new JobInfo.Builder(FridayApplication.Jobs.JOB_SYNC_ACCOUNT, new ComponentName(context, AccountSyncService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(2 * 60000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .build();
            jobScheduler.schedule(info);
        }
        PluginLoader pluginLoader = new PluginLoader(context);
        if (pluginLoader.startLoading()) {

        }

        JobInfo jobIndexerInfo = new JobInfo.Builder(FridayApplication.Jobs.JOB_INDEX_PLUGINS, new ComponentName(context, PluginIndexer.class))
                .setBackoffCriteria(30 * 60000, JobInfo.BACKOFF_POLICY_LINEAR)
                .build();
        jobScheduler.schedule(jobIndexerInfo);
    }
}
