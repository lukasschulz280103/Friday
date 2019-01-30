package com.code_design_camp.client.friday.HeadDisplayClient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.code_design_camp.client.friday.HeadDisplayClient.Util.NotificationUtil;
import com.code_design_camp.client.friday.HeadDisplayClient.Util.UpdateUtil;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/*
 * (C) Copyright 2018 Lukas Faber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Lukas Faber
 */

public class FridayApplication extends Application {
    public static final String NOTIF_CHANNEL_UPDATE_ID = "channel_update";
    public SpeechRecognizer speechtotextrecognizer;
    public OnAssetsLoadedListener mOnAssetLoadedListener;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("friday", "Initializing firebaseApp");
        createNotificationChannels();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("check_update_auto", false)) {
            UpdateUtil updateUtil = new UpdateUtil(this);
            updateUtil.setListener(versionNumberServer -> NotificationUtil.notifyUpdateAvailable(this, versionNumberServer));
        }
        loadSpeechRecognizer();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Drawable warnicon = getDrawable(R.drawable.ic_warning_black_24dp);
        warnicon.setTint(getResources().getColor(R.color.highlight_orange));
        builder.setIcon(warnicon);
        builder.setTitle(R.string.low_memory_title);
        builder.setMessage(R.string.low_memory_message);
        builder.setNeutralButton(android.R.string.ok, null);
        builder.create().show();
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
                    Assets assets = new Assets(FridayApplication.this);
                    File assetsDir = assets.syncAssets();
                    speechtotextrecognizer = SpeechRecognizerSetup.defaultSetup()
                            .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                            .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                            .getRecognizer();
                    speechtotextrecognizer.addNgramSearch("input", new File(assetsDir, "en-70k-0.1.lm"));
                } catch (IOException e) {
                    e.printStackTrace();
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
        at.execute();
    }

    public void setOnAssetsLoadedListener(OnAssetsLoadedListener l) {
        this.mOnAssetLoadedListener = l;
    }

    public interface OnAssetsLoadedListener {
        void onAssetLoaded();
    }

    public class Jobs {
        public static final int JOB_FEEDBACK = 1;
    }
}
