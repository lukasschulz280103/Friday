package com.code_design_camp.client.friday.HeadDisplayClient.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.AuthDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.ChangelogDialogFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.UninstallOldAppDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;

public class MainActivity extends FridayActivity {
    private static final int FULLSCREEN_REQUEST_CODE = 22;
    private static final String LOGTAG = "FridayMainActivity";
    boolean isSigninShown = false;
    ViewFlipper vswitcher_main;

    BottomNavigationView main_nav;
    FloatingActionButton lets_go;
    AuthDialog authDialogFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SharedPreferences defaut_pref;
    PackageInfo pkgInf;
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
    FloatingActionButton.OnClickListener startVR = view -> {
        Intent i = new Intent(MainActivity.this, FullscreenActionActivity.class);
        startActivityForResult(i, FULLSCREEN_REQUEST_CODE);
    };
    private UninstallOldAppDialog uninstallOldDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        vswitcher_main = findViewById(R.id.main_view_flipper);
        main_nav = findViewById(R.id.main_bottom_nav);
        lets_go = findViewById(R.id.start_actionmode);
        defaut_pref = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            pkgInf = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(LOGTAG, "SharedPref versionName is " + defaut_pref.getString("version", "1.0.0"));
        vswitcher_main.setDisplayedChild(0);
        main_nav.setOnNavigationItemSelectedListener(navselected);
        lets_go.setOnClickListener(startVR);
        checkForFirstUse();
        if (!defaut_pref.getString("version", "0").equals(pkgInf.versionName)) {
            ChangelogDialogFragment changelogdialog = new ChangelogDialogFragment();
            changelogdialog.show(getSupportFragmentManager(), "ChangeLogDialog");
            SharedPreferences.Editor editor = defaut_pref.edit();
            editor.putString("version", pkgInf.versionName);
            editor.commit();
        }
        try {
            PackageManager isOldAppInstalled = getPackageManager();
            isOldAppInstalled.getPackageInfo("com.code_design_camp.client.rasberrypie.rbpieclient", PackageManager.GET_ACTIVITIES);
            fragmentManager = getSupportFragmentManager();
            uninstallOldDialogFragment = new UninstallOldAppDialog();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            fragmentTransaction.replace(android.R.id.content, uninstallOldDialogFragment).commit();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOGTAG, e.getLocalizedMessage(), e);
        }
        if (defaut_pref.getInt("theme", 0) == 0) {
            defaut_pref.edit().putInt("theme", R.style.AppTheme).apply();
        }
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isSupported()) {

        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        assert pm != null;
        if (pm.isPowerSaveMode()) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.energy_saver_warn_title);
            builder.setMessage(R.string.energy_saver_warn_msg);
            builder.setPositiveButton(R.string.deactivate, (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)));
            builder.setNegativeButton(R.string.later, null);
            builder.create().show();
        }
    }
    private void checkForFirstUse() {
        SharedPreferences settingsfile = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (settingsfile.getBoolean("isFirstUse", true)) {
            AlertDialog.Builder notifieffirstuse = new AlertDialog.Builder(MainActivity.this);
            notifieffirstuse.setTitle("Welcome to friday");
            notifieffirstuse.setMessage("Thank you for downloading friday.\n\nRemember that you are in a pre-release of our app - Some features may not work properly or this app will crash at some points.");
            notifieffirstuse.setPositiveButton(android.R.string.ok, null);
            notifieffirstuse.create().show();
            settingsfile.edit().putBoolean("isFirstUse", false).apply();
        }
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
        if (isSigninShown) {
            dismissSinginPrompt();
        } else {
            Snackbar.make(findViewById(R.id.viewflipperparent), getString(R.string.leave_app), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action_leave), view -> finishAffinity()).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FULLSCREEN_REQUEST_CODE && data != null) {
            String errtype = data.getStringExtra("errtype");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton(android.R.string.ok, null);
            alertDialog.setNeutralButton(R.string.app_feedback, (dialogInterface, i) -> startActivity(new Intent(MainActivity.this, FeedbackSenderActivity.class)));
            switch (errtype) {
                case "TYPE_NOT_INSTALLED": {
                    alertDialog.setMessage(R.string.errtype_not_installed);
                }
                case "TYPE_OLD_APK": {
                    alertDialog.setMessage(R.string.errtype_arcore_apk_too_old);
                }
                case "TYPE_OLD_SDK_TOOL": {
                    alertDialog.setMessage(R.string.errtype_sdk_too_old);
                }
                case "TYPE_DEVICE_INCOMPATIBLE": {
                    alertDialog.setMessage(R.string.errtype_device_incompatible);
                }
            }
            alertDialog.create().show();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void promptSignin() {
        Log.d("FirebaseAuth", "showing auth dialog");
        fragmentManager = getSupportFragmentManager();
        authDialogFragment = new AuthDialog();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        fragmentTransaction.replace(android.R.id.content, authDialogFragment).commit();
        isSigninShown = true;
    }

    public AuthDialog.OnAuthCompletedListener getSigninOnAuthCompletedListener() {
        return authDialogFragment.getOnAuthListener();
    }

    public void dismissSinginPrompt() {
        Log.d("ONAUTHCOMPLETED", "SIGNIN PROMPT DISMISSED");
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, new Fragment())
                .commitNow();
        isSigninShown = false;
    }

    public void dismissUninstallPrompt() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, new Fragment())
                .commit();
    }

    public AuthDialog getAuthDialogFragment() {
        return authDialogFragment;
    }

    public void goToStore(View v) {
        Intent i = new Intent(this, StoreDetailActivity.class);
        startActivity(i);
    }
}
