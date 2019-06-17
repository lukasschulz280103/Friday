package com.friday.ar;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.friday.ar.plugin.application.PluginLoader;
import com.friday.ar.service.AccountSyncService;
import com.friday.ar.service.OnAccountSyncStateChanged;
import com.friday.ar.service.OnAccountSyncStateChangedList;
import com.friday.ar.service.PluginIndexer;
import com.friday.ar.util.UpdateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarFile;

import edu.cmu.pocketsphinx.SpeechRecognizer;
import io.fabric.sdk.android.Fabric;

/**
 * Application class
 */
public class FridayApplication extends Application implements OnAccountSyncStateChanged {
    /**
     * This is the global {@link SpeechRecognizer}.
     * Its purpose is to transform speech input from the voice assistant into text.
     * Loaded when {@link com.friday.ar.ui.MainActivity}'s onCreate() is called.
     */
    private SpeechRecognizer speechToTextRecognizer;

    private ArrayList<JarFile> indexedInstallablePluginFiles = new ArrayList<>();

    /**
     * This variable is the application global plugin loader.
     * Use this variable to get information about installed plugins, or to interact with them
     */
    private PluginLoader applicationPluginLoader;

    private OnAccountSyncStateChangedList<?> syncStateChangedNotifyList = new OnAccountSyncStateChangedList();
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationPluginLoader = new PluginLoader(this);
        applicationPluginLoader.startLoading();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Fabric.with(this, new Crashlytics());
        createNotificationChannels();
        UpdateUtil.checkForUpdate(this);
        PluginLoader loader = new PluginLoader(this);
        loader.startLoading();
        new Thread(this::runServices).start();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel updateChannel = new NotificationChannel(
                    Constants.NOTIF_CHANNEL_UPDATE_ID,
                    getString(R.string.notification_channel_update),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            updateChannel.setDescription(getString(R.string.notification_channel_update_description));
            NotificationChannel pluginInstallerChannel = new NotificationChannel(
                    Constants.NOTIF_CHANNEL_INSTALLER_ID,
                    getString(R.string.notification_channel_plugin_installer),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            pluginInstallerChannel.setDescription(getString(R.string.notification_channel_plugin_installer_description));
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannels(Arrays.asList(updateChannel, pluginInstallerChannel));
        }
    }


    public PluginLoader getApplicationPluginLoader() {
        return applicationPluginLoader;
    }

    public SpeechRecognizer getSpeechToTextRecognizer() {
        return speechToTextRecognizer;
    }


    public <Object extends OnAccountSyncStateChanged> void registerForSyncStateChange(Object context) {
        if (context != null) {
            syncStateChangedNotifyList.add(context);
        } else {
            throw new NullPointerException("");
        }
    }
    public void unregisterForSyncStateChange(Object context) {
        syncStateChangedNotifyList.remove(context);
    }

    private void runServices() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (preferences.getBoolean("sync_account_auto", true)) {
            JobInfo info = new JobInfo.Builder(FridayApplication.Jobs.JOB_SYNC_ACCOUNT, new ComponentName(this, AccountSyncService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(2 * 60000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .build();
            jobScheduler.schedule(info);
        }

        JobInfo jobIndexerInfo = new JobInfo.Builder(FridayApplication.Jobs.JOB_INDEX_PLUGINS, new ComponentName(this, PluginIndexer.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setOverrideDeadline(0)
                .setBackoffCriteria(30 * 60000, JobInfo.BACKOFF_POLICY_LINEAR)
                .build();
        jobScheduler.schedule(jobIndexerInfo);
    }

    @Override
    public void onSyncStateChanged() {
        for (OnAccountSyncStateChanged listener : syncStateChangedNotifyList) {
            listener.onSyncStateChanged();
        }
    }

    public ArrayList<JarFile> getIndexedInstallablePluginFiles() {
        return indexedInstallablePluginFiles;
    }

    public void setIndexedInstallablePluginFiles(ArrayList<JarFile> indexedFiles) {
        this.indexedInstallablePluginFiles = indexedFiles;
    }

    public class Jobs {
        public static final int JOB_SYNC_ACCOUNT = 8000;
        public static final int JOB_FEEDBACK = 8001;
        public static final int JOB_INDEX_PLUGINS = 8002;
    }

    public class Constants {
        public static final String NOTIF_CHANNEL_UPDATE_ID = "channel_update";
        public static final String NOTIF_CHANNEL_INSTALLER_ID = "channel_plugin_installer";
        public static final String LOGTAG_STORE = "FridayMarketplace";

        public class NotificationIDs {
            public static final int NOTIFICATION_INSTALL_SUCCESS = 200;
        }
    }
}
