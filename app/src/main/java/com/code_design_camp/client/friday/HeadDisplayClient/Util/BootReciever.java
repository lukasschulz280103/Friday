package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent pIntent) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("check_update_auto", false)) {
            UpdateUtil updateUtil = new UpdateUtil(context);
            updateUtil.setListener(versionNumberServer -> NotificationUtil.notifyUpdateAvailable(context, versionNumberServer));
        }
    }
}
