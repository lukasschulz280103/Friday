package com.friday.ar;

import android.content.Context;

import java.io.File;

public class Constant {
    public static final String NOTIF_CHANNEL_UPDATE_ID = "channel_update";
    public static final String NOTIF_CHANNEL_INSTALLER_ID = "channel_plugin_installer";
    public static final String LOGTAG_STORE = "FridayMarketplace";

    public static File getPluginDir(Context context) {
        return new File(context.getFilesDir().getPath() + "/plugin");
    }

    public static File getPluginDir(Context context, String child) {
        File dir = new File(context.getFilesDir().getPath() + "/plugin/" + child);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public class NotificationIDs {
        public static final int NOTIFICATION_INSTALL_SUCCESS = 200;
        public static final int NOTIFICATION_INSTALL_ERROR = 201;
    }
}