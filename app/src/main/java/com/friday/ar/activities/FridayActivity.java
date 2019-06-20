package com.friday.ar.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/*
This file is necessarry for analytics, in order to record usage statistics.
 */
public abstract class FridayActivity extends AppCompatActivity {
    private static final String LOGTAG = "FridayActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "started activity");
    }

    @Override
    protected void onStop() {
        Log.d(LOGTAG, "stopped activity");
        super.onStop();
    }
}
