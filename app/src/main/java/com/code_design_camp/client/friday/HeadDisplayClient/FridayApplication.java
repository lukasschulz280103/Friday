package com.code_design_camp.client.friday.HeadDisplayClient;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class FridayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("friday", "Initializing firebaseApp");
        FirebaseApp.initializeApp(this);
    }
}
