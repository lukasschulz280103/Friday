package com.code_design_camp.client.friday.HeadDisplayClient;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.AuthDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.ChangelogDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private static final int ORIENTATION_0 = 0;
    private static final String LOGTAG = "FridayMainActivity";
    boolean isSigninShown = false;
    ViewFlipper vswitcher_main;

    BottomNavigationView main_nav;
    FloatingActionButton lets_go;
    Button tosettings;
    Button tofeedback;
    AuthDialog authDialogFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SharedPreferences defaut_pref;
    PackageInfo pkgInf = new PackageInfo();
    BottomNavigationView.OnNavigationItemSelectedListener navselected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_nav_dashboard:
                    vswitcher_main.setDisplayedChild(0);
                    break;
                case R.id.main_nav_history:
                    vswitcher_main.setDisplayedChild(2);
                    break;
                case R.id.main_nav_profile:
                    vswitcher_main.setDisplayedChild(1);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vswitcher_main = findViewById(R.id.main_view_flipper);
        main_nav = findViewById(R.id.main_bottom_nav);
        lets_go = findViewById(R.id.start_actionmode);
        tosettings = findViewById(R.id.tosettings);
        tofeedback = findViewById(R.id.tofeedback);
        defaut_pref = PreferenceManager.getDefaultSharedPreferences(this);

        vswitcher_main.setDisplayedChild(0);
        main_nav.setOnNavigationItemSelectedListener(navselected);
        lets_go.setOnClickListener(startVR);
        checkForFirstUse();
        final Intent tosettingsintent = new Intent(MainActivity.this,SettingsActivity.class);
        final Intent tofeedbackintent = new Intent(MainActivity.this, FeedbackSenderActivity.class);
        if (!defaut_pref.getString("version", "1.0.0").equals(pkgInf.versionName)) {
            ChangelogDialogFragment changelogdialog = new ChangelogDialogFragment();
            changelogdialog.show(getSupportFragmentManager(), "ChangeLogDialog");
        }
        tosettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(tosettingsintent);
            }
        });
        tofeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(tofeedbackintent);
            }
        });
    }

    private void checkForFirstUse() {
        SharedPreferences settingsfile = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if(settingsfile.getBoolean("isFirstUse",true)){
            AlertDialog.Builder notifieffirstuse = new AlertDialog.Builder(MainActivity.this);
            notifieffirstuse.setTitle("Welcome to friday");
            notifieffirstuse.setMessage("Thank you for downloading friday.\n\nRemember that you are in a pre-release of our app - Some features may not work properly or this app will crash at some points.");
            notifieffirstuse.setPositiveButton(android.R.string.ok,null);
            notifieffirstuse.create().show();
            settingsfile.edit().putBoolean("isFirstUse",false).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(isSigninShown){
            dismissSinginPrompt();
        }
        else {
            Snackbar.make(findViewById(R.id.viewflipperparent), getString(R.string.leave_app), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action_leave), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finishAffinity();
                        }
                    }).show();
        }
    }
    FloatingActionButton.OnClickListener startVR = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(MainActivity.this,FullscreenActionActivity.class);
            startActivity(i);
        }
    };
    public void promptSignin(){
        Log.d("FirebaseAuth","showing auth dialog");
        fragmentManager = getSupportFragmentManager();
        authDialogFragment = new AuthDialog();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_up,R.anim.slide_down);
        fragmentTransaction.replace(android.R.id.content,authDialogFragment).commit();
        isSigninShown = true;
    }
    public void dismissSinginPrompt(){
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,R.anim.slide_down)
                .replace(android.R.id.content,new Fragment())
                .commit();
        isSigninShown = false;
    }
}
