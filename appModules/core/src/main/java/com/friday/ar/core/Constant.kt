package com.friday.ar.core

import android.content.Context
import java.io.File

class Constant {
    companion object {
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

    object Notification {
        const val NOTIFICATION_FRIDAYACTIVITY_CRASH = 300
        //Store
        const val NOTIFICATION_INSTALL_SUCCESS = 200
        const val NOTIFICATION_INSTALL_ERROR = 201
        const val NOTIFICATION_INSTALL_PROGRESS = 202

        object Channels {
            const val NOTIFICATION_CHANNEL_UPDATE = "channel_update"
            const val NOTIFICATION_CHANNEL_INSTALLER = "channel_plugin_installer"
            const val NOTIFICAITON_CHANNEL_APP_CRASH = "channel_app_crash"
        }

        object ChannelGroups {
            const val NOTIFICATION_CHANNEL_GROUP_APP_RELATED = "channel_group_app"
            const val NOTIFICATION_CHANNEL_GROUP_STORE = "channel_group_store"
        }
        object Groups {
            const val NOTIFICATION_GROUP_INSTALLER = "NOTIF_GROUP_INSTALLER"
            const val NOTIFICATION_GROUP_UNINSTALLER = "NOTIF_GROUP_UNINSTALLER"
        }
    }

    object BroadcastReceiverActions {
        /**
         * Use this broadcast receiver action to always be notified about synchronization changes of [com.friday.ar.service.AccountSyncService]
         *
         * @see android.content.BroadcastReceiver
         * @see com.friday.ar.service.AccountSyncService
         */
        const val BROADCAST_ACCOUNT_SYNCED = "ACCOUNT_SYNCHRONIZED"

        /**
         * Use this broadcast receiver action to always be notified when the PluginIndexer service is done indexing plugins.
         *
         * @see android.content.BroadcastReceiver
         * @see com.friday.ar.service.plugin.PluginLoader
         */
        const val BROADCAST_PLUGINS_LOADED = "PLUGINS_LOADED"

        const val BROADCAST_PLUGIN_UNINSTALLED = "PLUGIN_UNINSTALLED"
    }

    object Jobs {
        const val JOB_SYNC_ACCOUNT = 8000
        const val JOB_FEEDBACK = 8001
        const val JOB_INDEX_PLUGINS = 8002
        const val JOB_INDEX_INSTALLED_PLUGINS = 8003
    }

    object AnalyticEvent {
        const val LOGIN_EVENT_EMAIL = "Email + password"
        const val CUSTOM_EVENT_ACTIONMODE = "ActionMode start"
    }

    object PreferenceKeys {
        object Store {
            const val INSTALLER_SHOW_DISK_INSTALL_WARNING = "storeInstallationsManager_showSecurityWarning"
        }
    }

    object OldPacakgeNames {
        const val RBPIECLIENT = "com.code_design_camp.client.rasberrypie.rbpieclient"
        const val HEAD_DISPLAY_CLIENT = "com.code_design_camp.client.friday.HeadDisplayClient"
    }

    object Server {
        object Database {
            const val STORE_REFERENCE = "/store"
            const val STORE_GENERATED_REFERENCE = STORE_REFERENCE + "/generated"
            const val STORE_FEED_BASE_STRUCTURE_REFERENCE = STORE_GENERATED_REFERENCE + "/structure-base"
        }
    }

    object Account {

        /**
         * Returns URI of users avatar file
         * <i>This URI may not be valid if the user does not exist.</i>
         */
        const val AVATAR_FILE_URI = "profile/avatar.jpg"
    }

    object AR {
        object SystemBroadcast {
            const val AR_SCENE_UPDATED = "AR_PLANE_UPDATED"
        }
    }
}