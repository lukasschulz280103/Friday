package com.code_design_camp.client.rasberrypie.rbpieclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {
    private static final int ORIENTATION_0 = 0;
    private static final String LOGTAG = "FridayMainActivity";
    ViewFlipper vswitcher;

    TextView warning_rotation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        warning_rotation = findViewById(R.id.rotation_warn);
        vswitcher = findViewById(R.id.main_view_flipper);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenRotation = display.getRotation();
        Log.d(LOGTAG,"screenRotation:"+screenRotation);
        vswitcher.setDisplayedChild(0);
        switch (screenRotation)
        {
            default:
                warning_rotation.setVisibility(View.GONE);
                break;
            case ORIENTATION_0:

                warning_rotation.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(getCurrentFocus(),getString(R.string.leave_app),Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.action_leave), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finishAffinity();
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                return true;
            }
        }
        return false;
    }
    NavigationView.OnNavigationItemSelectedListener draweritemlistener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.main_nav_dashboard:{
                    vswitcher.setDisplayedChild(0);
                }
                case R.id.main_nav_profile:{
                    vswitcher.setDisplayedChild(1);
                }
                case R.id.main_nav_history:{
                    vswitcher.setDisplayedChild(2);
                }
            }
            return true;
        }
    };
}
