package com.friday.ar.plugin.installer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class PluginInstaller {
    public static final String LOGTAG = "PluginInstaller";
    private Context context;

    public PluginInstaller(Context context) {
        this.context = context;
    }

    public void installFrom(File pluginDir) throws IOException, IllegalFileException {
        Log.d(LOGTAG, FileUtil.getFileExtension(pluginDir));
        if (pluginDir.isDirectory())
            throw new IllegalArgumentException("A Friday plugin package cannot be a directory.");
        else if (!FileUtil.getFileExtension(pluginDir).equals(".jar"))
            throw new IllegalFileException("This file has an inappropriate ending");
        else {
            FileUtil.moveFile(pluginDir, new File(context.getFilesDir() + "/plugin/" + pluginDir.getName()));
            Notification notification = new NotificationCompat.Builder(context, FridayApplication.Constants.NOTIF_CHANNEL_INSTALLER_ID)
                    .setContentTitle(context.getString(R.string.pluginInstaller_succes_install_title))
                    .setContentText(pluginDir.getName())
                    .setSubText(context.getString(R.string.pluginInstaller_name))
                    .setCategory(NotificationCompat.CATEGORY_STATUS)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_twotone_save_alt_24px)
                    .setTicker(context.getString(R.string.pluginInstaller_success_ticker_text, pluginDir.getName()))
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(FridayApplication.Constants.NotificationIDs.NOTIFICATION_INSTALL_SUCCESS, notification);
        }
    }

    public class IllegalFileException extends IllegalStateException {
        public IllegalFileException(String message) {
            super(message);
        }
    }
}
