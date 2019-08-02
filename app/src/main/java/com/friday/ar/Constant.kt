package com.friday.ar

import android.content.Context
import java.io.File

class Constant {
    companion object {
        const val NOTIF_CHANNEL_UPDATE_ID = "channel_update"
        const val NOTIF_CHANNEL_INSTALLER_ID = "channel_plugin_installer"
        const val LOGTAG_STORE = "FridayMarketplace"
        /**
         * get the plugin storage directory.
         *
         * @param context application context
         * @return returns the cache directory to temporarily store zipped plugins.
         */
        fun getPluginDir(context: Context?): File {
            if (context == null) {
                throw IllegalArgumentException("context is null!")
            } else {
                return File(context.getExternalFilesDir("/plugin/")!!.path)
            }
        }

        /**
         * get the plugin storage directory with children path.
         * @param context application context
         * @param child child directory to return(if does not exist, it will be created)
         * @return returns a specified child directory of the applications plugin storage directory([.getPluginDir]).
         */
        fun getPluginDir(context: Context, child: String): File {
            val dir = File(getPluginDir(context).toString() + "/" + child)
            if (!dir.exists()) {
                dir.mkdir()
            }
            return dir
        }

        /**
         * get the plugin cache directory to store plugins.
         *
         * @param context application context
         * @return cache directory for temprorarily store plugins.
         * @see com.friday.ar.plugin.security.PluginVerifier
         */
        fun getPluginCacheDir(context: Context): File {
            return File(context.externalCacheDir!!.toString() + "/pluginZipCache/")
        }

        fun getPluginCacheDir(context: Context, child: String): File {
            val dir = File(getPluginCacheDir(context).toString() + "/" + child)
            if (!dir.exists()) {
                dir.mkdir()
            }
            return dir
        }
    }

    object NotificationIDs {
        const val NOTIFICATION_INSTALL_SUCCESS = 200
        const val NOTIFICATION_INSTALL_ERROR = 201
    }

    object BroadcasteceiverActions {
        /**
         * Use this broadcast receiver action to always be notified about synchronization changes of [com.friday.ar.service.AccountSyncService]
         *
         * @see android.content.BroadcastReceiver
         * @see com.friday.ar.service.AccountSyncService
         */

        const val BROADCAST_ACCOUNT_SYNCED = "BROADCAST_ACCOUNT_SYNCHRONIZED"
    }


    object AnalyticEvent {
        const val LOGIN_EVENT_EMAIL = "Email + password"
        const val CUSTOM_EVENT_ACTIONMODE = "ActionMode start"
    }
}