package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent pIntent) {
        UpdateUtil updateUtil = new UpdateUtil(context);
        updateUtil.setListener(new UpdateUtil.OnStateChangedListener() {
            @Override
            public void onStateChanged(String versionNumberServer) {
                NotificationUtil.notifyUpdateAvailable(context, versionNumberServer);
            }
        });
    }
}
