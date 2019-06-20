package com.friday.ar.service;

import android.app.DownloadManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.friday.ar.util.FileUtil;
import com.friday.ar.util.UserUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;

public class AccountSyncService extends JobService {
    private static final String LOGTAG = "SynchronizationService";

    public AccountSyncService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(LOGTAG, "started synchronization service");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserUtil util = new UserUtil(getApplicationContext());
        if (firebaseUser == null) {
            jobFinished(jobParameters, false);
            return false;
        }
        if (firebaseUser.getPhotoUrl() != null) {
            File avatarFile = util.getAvatarFile();
            avatarFile.delete();
            DownloadManager.Request request = new DownloadManager.Request(firebaseUser.getPhotoUrl());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setDestinationInExternalFilesDir(getApplicationContext(), "profile", File.separator + "avatar.jpg")
                    .setVisibleInDownloadsUi(false);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    Log.d(LOGTAG, "synchronized account avatar");
                    try {
                        FileUtil.moveFile(new File(getExternalFilesDir("profile") + File.separator + "avatar.jpg"), avatarFile);
                    } catch (IOException e) {
                        Log.e(LOGTAG, e.getLocalizedMessage(), e);
                    } finally {
                        jobFinished(jobParameters, false);
                    }
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(LOGTAG, "finished synchronization service");
        return false;
    }
}
