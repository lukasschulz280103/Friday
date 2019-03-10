package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.service.AccountSyncService;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent pIntent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("check_update_auto", false)) {
            UpdateUtil updateUtil = new UpdateUtil(context);
            updateUtil.setListener(versionNumberServer -> NotificationUtil.notifyUpdateAvailable(context, versionNumberServer));
        }
        if (preferences.getBoolean("sync_account_auto", true)) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo info = new JobInfo.Builder(FridayApplication.Jobs.JOB_SYNC_ACCOUNT, new ComponentName(context, AccountSyncService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(2 * 60000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .build();
            scheduler.schedule(info);
        }
    }
}
