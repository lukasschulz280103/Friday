package com.friday.ar.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedbackService extends JobService {
    public static final String LOGTAG = "FeedbackService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(LOGTAG, "Started Feedback service");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.document("feedback/account_deleted_feedback");
        PersistableBundle extras = jobParameters.getExtras();
        new Thread() {
            @Override
            public void run() {
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> childData = new HashMap<>();
                childData.put("reason_value", extras.getString("reason"));
                childData.put("timestamp", new Timestamp(new Date()));
                data.put(extras.getString("uid"), childData);
                Task updateFeedback = docRef.set(data);
                updateFeedback.addOnCompleteListener(task -> {
                    Log.d(LOGTAG, "Submitted feedback:" + task.isSuccessful());
                    jobFinished(jobParameters, !task.isSuccessful());
                });
            }
        }.start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(LOGTAG, "Stopped Feedback service");
        return false;
    }
}
