package com.code_design_camp.client.friday.HeadDisplayClient;

import android.app.Application;
import android.util.Log;

public class FridayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("friday", "Initializing firebaseApp");
    }
}
