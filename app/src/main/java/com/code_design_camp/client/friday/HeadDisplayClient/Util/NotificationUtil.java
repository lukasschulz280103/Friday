package com.code_design_camp.client.friday.HeadDisplayClient.Util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.InfoActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtil {
    private static NotificationManagerCompat nmc;

    public static void notifyUpdateAvailable(Context context, String newVersion) {
        Intent toInfoPage = new Intent(context, InfoActivity.class);
        PendingIntent notif_click = PendingIntent.getActivity(context, 2, toInfoPage, 0);
        nmc = NotificationManagerCompat.from(context);
        Notification update_notif = new NotificationCompat.Builder(context, FridayApplication.NOTIF_CHANNEL_UPDATE_ID)
                .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_update_description, newVersion))
                .setContentIntent(notif_click)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .build();
        nmc.notify(1, update_notif);
    }
}
