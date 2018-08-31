package com.code_design_camp.client.rasberrypie.rbpieclient;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    private static final int ORIENTATION_0 = 0;
    private static final String LOGTAG = "FridayMainActivity";
    ViewFlipper vswitcher;
    BottomNavigationView main_nav;

    TextView warning_rotation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        warning_rotation = findViewById(R.id.rotation_warn);
        vswitcher = findViewById(R.id.main_view_flipper);
        main_nav = findViewById(R.id.main_bottom_nav);
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
        main_nav.setOnNavigationItemSelectedListener(navselected);
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(R.id.main_root_layout),getString(R.string.leave_app),Snackbar.LENGTH_SHORT)
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
    BottomNavigationView.OnNavigationItemSelectedListener navselected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.main_nav_dashboard:
                    vswitcher.setDisplayedChild(0);
                    break;
                case R.id.main_nav_history:
                    vswitcher.setDisplayedChild(2);
                    break;
                case R.id.main_nav_profile:
                    vswitcher.setDisplayedChild(1);
                    break;
            }
            return true;
        }
    };
}
