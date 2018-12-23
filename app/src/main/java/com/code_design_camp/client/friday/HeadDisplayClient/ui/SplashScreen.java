package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.code_design_camp.client.friday.HeadDisplayClient.R;

public class SplashScreen extends FridayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
        }, 3000);
    }

}
