package com.friday.ar;

import android.content.Context;

import java.io.File;

public class Constant {
    public static final String NOTIF_CHANNEL_UPDATE_ID = "channel_update";
    public static final String NOTIF_CHANNEL_INSTALLER_ID = "channel_plugin_installer";
    public static final String LOGTAG_STORE = "FridayMarketplace";

    /**
     * get the plugin storage directory.
     *
     * @param context application context
     * @return returns the cache directory to temporarily store zipped plugins.
     */
    public static File getPluginDir(Context context) {
        return new File(context.getExternalFilesDir(("/plugin/")).getPath());
    }

    /**
     * get the plugin storage directory with children path.
     * @param context application context
     * @param child child directory to return(if does not exist, it will be created)
     * @return returns a specified child directory of the applications plugin storage directory({@link #getPluginDir(Context)}).
     */
    public static File getPluginDir(Context context, String child) {
        File dir = new File(getPluginDir(context) + "/" + child);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * get the plugin cache directory to store plugins.
     *
     * @param context application context
     * @return cache directory for temprorarily store plugins.
     * @see com.friday.ar.plugin.security.PluginVerifier
     */
    public static File getPluginCacheDir(Context context) {
        return new File(context.getExternalCacheDir() + "/pluginZipCache/");
    }

    public static File getPluginCacheDir(Context context, String child) {
        File dir = new File(getPluginCacheDir(context) + "/" + child);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public class NotificationIDs {
        public static final int NOTIFICATION_INSTALL_SUCCESS = 200;
        public static final int NOTIFICATION_INSTALL_ERROR = 201;
    }
}