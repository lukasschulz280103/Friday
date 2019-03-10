package com.code_design_camp.client.friday.HeadDisplayClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.code_design_camp.client.friday.HeadDisplayClient.Util.NotificationUtil;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.UpdateUtil;
import com.code_design_camp.client.friday.HeadDisplayClient.service.AccountSyncService;
import com.code_design_camp.client.friday.HeadDisplayClient.service.OnAccountSyncStateChanged;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import io.fabric.sdk.android.Fabric;

/**
 * Application class
 */
public class FridayApplication extends Application implements OnAccountSyncStateChanged {
    public static final String NOTIF_CHANNEL_UPDATE_ID = "channel_update";
    public static final String LOGTAG_STORE = "FridayMarketplace";
    private ArrayList<Object> syncStateChangedNotifyList = new ArrayList<>();
    public SpeechRecognizer speechtotextrecognizer;
    public OnAssetsLoadedListener mOnAssetLoadedListener;
    public File assetsDir;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Fabric.with(this, new Crashlytics());
        createNotificationChannels();
        if (preferences.getBoolean("check_update_auto", false)) {
            UpdateUtil updateUtil = new UpdateUtil(this);
            updateUtil.setListener(versionNumberServer -> NotificationUtil.notifyUpdateAvailable(this, versionNumberServer));
        }
        if (preferences.getBoolean("sync_account_auto", true)) {
            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo info = new JobInfo.Builder(FridayApplication.Jobs.JOB_SYNC_ACCOUNT, new ComponentName(this, AccountSyncService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(2 * 60000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .build();
            scheduler.schedule(info);
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel update_channel = new NotificationChannel(
                    NOTIF_CHANNEL_UPDATE_ID,
                    "Update Checker",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            update_channel.setDescription(getString(R.string.update_notif_channel_descr));
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(update_channel);
        }
    }

    public void loadSpeechRecognizer() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask at = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if (mOnAssetLoadedListener != null) {
                        mOnAssetLoadedListener.onStartedLoadingAssets();
                    }
                    Assets assets = new Assets(FridayApplication.this);
                    assetsDir = assets.syncAssets();
                    speechtotextrecognizer = SpeechRecognizerSetup.defaultSetup()
                            .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                            .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                            .getRecognizer();
                    speechtotextrecognizer.addNgramSearch("input", new File(assetsDir, "en-70k-0.1.lm"));

                } catch (IOException e) {
                    if (mOnAssetLoadedListener != null) {
                        mOnAssetLoadedListener.onError(e);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.d("FRIDAYAPPLICATION", "Loaded asset data");
                if (mOnAssetLoadedListener != null) {
                    mOnAssetLoadedListener.onAssetLoaded();
                }
            }
        };
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED && mOnAssetLoadedListener != null) {
            mOnAssetLoadedListener.onError(new SecurityException("Could not load speech assets. Microphone permission not given."));
        } else {
            at.execute();
        }
    }

    public void setOnAssetsLoadedListener(OnAssetsLoadedListener l) {
        this.mOnAssetLoadedListener = l;
    }

    public SpeechRecognizer getSpeechtotextrecognizer() {
        return speechtotextrecognizer;
    }

    public File getAssetsDir() {
        return assetsDir;
    }

    public void registerForSyncStateChange(Object context) {
        if (context instanceof OnAccountSyncStateChanged) {
            syncStateChangedNotifyList.add(context);
        } else {
            throw new IllegalArgumentException(context + "must implement interface OnAccountSyncStateChanged");
        }
    }

    public void unregisterForSyncStateChange(Object context) {
        syncStateChangedNotifyList.remove(context);
    }

    @Override
    public void onSyncStateChanged() {

    }

    public interface OnAssetsLoadedListener {
        void onStartedLoadingAssets();

        void onAssetLoaded();

        void onError(Exception e);
    }


    public class Jobs {
        public static final int JOB_SYNC_ACCOUNT = 8000;
        public static final int JOB_FEEDBACK = 8001;
    }
}
