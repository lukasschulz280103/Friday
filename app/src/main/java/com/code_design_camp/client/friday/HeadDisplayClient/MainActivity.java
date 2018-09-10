package com.code_design_camp.client.friday.HeadDisplayClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.code_design_camp.client.friday.HeadDisplayClient.fragments.AuthDialog;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final int ORIENTATION_0 = 0;
    private static final String LOGTAG = "FridayMainActivity";
    boolean isSigninShown = false;
    ViewFlipper vswitcher;
    BottomNavigationView main_nav;
    FloatingActionButton lets_go;

    Button sigininbtn;
    Button tosettings;
    Button tofeedback;


    AuthDialog authDialogFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView warning_rotation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        warning_rotation = findViewById(R.id.rotation_warn);
        vswitcher = findViewById(R.id.main_view_flipper);
        main_nav = findViewById(R.id.main_bottom_nav);
        lets_go = findViewById(R.id.start_actionmode);
        sigininbtn = findViewById(R.id.sign_in_user_btn);
        tosettings = findViewById(R.id.tosettings);
        tofeedback = findViewById(R.id.tofeedback);

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
        lets_go.setOnClickListener(startVR);
        checkForFirstUse();
        sigininbtn.setOnClickListener(showSigninDialog);
        final Intent tosettingsintent = new Intent(MainActivity.this,SettingsActivity.class);
        final Intent tofeedbackintent = new Intent(MainActivity.this,Feedback.class);
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
        if(fauth.getCurrentUser() != null){
            sigininbtn.setVisibility(View.GONE);
        }
        else{
            promptSignin();
        }
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
    public void onBackPressed() {
        if(isSigninShown){
            dismissSinginPrompt();
        }
        else {
            Snackbar.make(findViewById(R.id.main_root_layout), getString(R.string.leave_app), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action_leave), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finishAffinity();
                        }
                    }).show();
        }
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
    FloatingActionButton.OnClickListener startVR = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(MainActivity.this,FullscreenActionActivity.class);
            startActivity(i);
        }
    };
    Button.OnClickListener showSigninDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            promptSignin();
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
