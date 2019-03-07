package com.code_design_camp.client.friday.HeadDisplayClient.ui;


import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.code_design_camp.client.friday.HeadDisplayClient.FridayApplication;
import com.code_design_camp.client.friday.HeadDisplayClient.R;
import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.code_design_camp.client.friday.HeadDisplayClient.activities.FridayActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.AuthDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.ChangelogDialogFragment;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.dialogFragments.UninstallOldAppDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.interfaces.OnAuthCompletedListener;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.store.MainStoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

public class MainActivity extends FridayActivity {
    public static final int FULLSCREEN_REQUEST_CODE = 22;
    private static final String LOGTAG = "FridayMainActivity";
    //Store Fragment
    MainStoreFragment storeFragment = new MainStoreFragment();
    private ViewFlipper vswitcher_main;

    private AuthDialog authDialogFragment;
    private SharedPreferences defaut_pref;
    private PackageInfo pkgInf;
    private LinearLayout assetLoaderLayout;
    private TextSwitcher assetLoaderText;
    private ProgressBar loadingBar;
    private boolean loadedSpeechRecognizer = false;
    FridayApplication.OnAssetsLoadedListener mAssetsLoadedListener = new FridayApplication.OnAssetsLoadedListener() {
        @Override
        public void onStartedLoadingAssets() {
            assetLoaderLayout.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
            assetLoaderLayout.startAnimation(anim);
        }

        @Override
        public void onAssetLoaded() {
            loadedSpeechRecognizer = true;
            loadingBar.setIndeterminateDrawable(getDrawable(R.drawable.ic_cloud_done_black_24dp));
            assetLoaderText.setText(getString(R.string.asset_loader_loading_success));
            Handler h = new Handler();
            h.postDelayed(() -> assetLoaderLayout.animate()
                    .alpha(0)
                    .setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            assetLoaderLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    })
                    .start(), 2000);
        }

        @Override
        public void onError() {
            loadedSpeechRecognizer = false;
            Toast.makeText(MainActivity.this,R.string.err_unable_to_load_speech_assets,Toast.LENGTH_LONG).show();
        }
    };
    OnAuthCompletedListener mOnAuthCompleted;
    BottomNavigationView.OnNavigationItemSelectedListener navselected = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_nav_dashboard:
                    vswitcher_main.setDisplayedChild(0);
                    break;
                case R.id.main_nav_store:
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
        Intent intent = new Intent(MainActivity.this, FullscreenActionActivity.class);
        if (loadedSpeechRecognizer && defaut_pref.getBoolean("showHeatWarn", true)) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle("Device health protection")
                    .setMessage("You are running a beta version of friday. This version contains known performance issues causing the device to heat up to an unsafe temperature. To prevent device damage, the AR-activity will be automatically finished after 3 minutes.")
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, int_which) -> {
                        startActivityForResult(intent, FULLSCREEN_REQUEST_CODE);
                    })
                    .setNeutralButton("start without limit", (dialogInterface, wich) -> {
                        intent.putExtra("noLimit", true);
                        startActivityForResult(intent, FULLSCREEN_REQUEST_CODE);
                    })
                    .setNeutralButtonIcon(getResources().getDrawable(R.drawable.ic_twotone_security_24px))
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().show();
            return;
        } else if (loadedSpeechRecognizer) {
            startActivity(intent);
            return;
        }
        Snackbar.make(findViewById(R.id.viewflipperparent), R.string.err_assets_not_loaded, Snackbar.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Theme theme = new Theme(this);
        int appThemeIndex = theme.indexOf(Theme.getCurrentAppTheme(this));
        findViewById(R.id.mainTitleView).setBackground(theme.createGradient(appThemeIndex));
        findViewById(R.id.profileTitleViewContainer).setBackground(theme.createGradient(appThemeIndex));
        vswitcher_main = findViewById(R.id.main_view_flipper);
        BottomNavigationView main_nav = findViewById(R.id.main_bottom_nav);
        FloatingActionButton lets_go = findViewById(R.id.start_actionmode);
        assetLoaderLayout = findViewById(R.id.asset_loading_indicator_conatiner);
        assetLoaderText = findViewById(R.id.asset_loading_text);
        loadingBar = findViewById(R.id.asset_loading_bar);
        defaut_pref = PreferenceManager.getDefaultSharedPreferences(this);
        FridayApplication app = ((FridayApplication) getApplication());
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
        if (!defaut_pref.getString("version", "0").equals(pkgInf.versionName)|| defaut_pref.getBoolean("pref_devmode_show_changelog", false)) {
            ChangelogDialogFragment changelogdialog = new ChangelogDialogFragment();
            changelogdialog.show(getSupportFragmentManager(), "ChangeLogDialog");
            SharedPreferences.Editor editor = defaut_pref.edit();
            editor.putString("version", pkgInf.versionName);
            editor.apply();
        }
        try {
            PackageManager isOldAppInstalled = getPackageManager();
            isOldAppInstalled.getPackageInfo("com.code_design_camp.client.rasberrypie.rbpieclient", PackageManager.GET_ACTIVITIES);
            UninstallOldAppDialog uninstallOldDialogFragment = new UninstallOldAppDialog();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    .replace(android.R.id.content, uninstallOldDialogFragment)
                    .commit();
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(LOGTAG,"no old version found");
        }
        if (defaut_pref.getInt("theme", 0) == 0) {
            defaut_pref.edit().putInt("theme", R.style.AppTheme).apply();
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        assert pm != null;
        if (pm.isPowerSaveMode()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(R.string.energy_saver_warn_title);
            builder.setMessage(R.string.energy_saver_warn_msg);
            builder.setPositiveButton(R.string.deactivate, (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)));
            builder.setNegativeButton(R.string.later, null);
            builder.create().show();
        }
        app.setOnAssetsLoadedListener(mAssetsLoadedListener);
        assetLoaderLayout.setVisibility(View.VISIBLE);
        assetLoaderText.setFactory(() -> new TextView(MainActivity.this));
        assetLoaderText.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        assetLoaderText.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        assetLoaderText.setText(getString(R.string.asset_loader_loading));
        //Initialize Store
        getSupportFragmentManager().beginTransaction()
                .add(R.id.store_frag_container,storeFragment)
                .commit();
    }

    private void checkForFirstUse() {
        SharedPreferences settingsfile = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (settingsfile.getBoolean("isFirstUse", true)) {
            MaterialAlertDialogBuilder notifieffirstuse = new MaterialAlertDialogBuilder(MainActivity.this);
            notifieffirstuse.setTitle("Welcome to friday");
            notifieffirstuse.setMessage("Thank you for downloading friday.\n\nRemember that you are in a pre-release of our app - Some features may not work properly or this app will crash at some points.");
            notifieffirstuse.setPositiveButton(android.R.string.ok, null);
            notifieffirstuse.create().show();
            settingsfile.edit().putBoolean("isFirstUse", false).apply();
            Intent showWizard = new Intent(this,WizardActivity.class);
            startActivity(showWizard);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //Fragments need to be removed before saving the instance state
        getSupportFragmentManager().beginTransaction()
                .remove(storeFragment)
                .commit();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!storeFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.store_frag_container, storeFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (authDialogFragment.isAdded()) {
            authDialogFragment.dismissDialog();
        } else {
            Snackbar.make(findViewById(R.id.viewflipperparent), getString(R.string.leave_app), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.action_leave), view -> finishAffinity()).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FULLSCREEN_REQUEST_CODE && data != null) {
            String errtype = data.getStringExtra("errtype");
            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
            alertDialog.setPositiveButton(android.R.string.ok, null);
            alertDialog.setNeutralButton(R.string.app_feedback, (dialogInterface, i) -> startActivity(new Intent(MainActivity.this, FeedbackSenderActivity.class)));
            switch (errtype) {
                case "TYPE_NOT_INSTALLED": {
                    alertDialog.setMessage(R.string.errtype_not_installed);
                    ArCoreApk apk = ArCoreApk.getInstance();
                    try {
                        apk.requestInstall(this, true);
                    } catch (UnavailableDeviceNotCompatibleException e) {
                        data.putExtra("errtype","TYPE_DEVICE_NOT_SUPPORTED");
                        onActivityResult(requestCode,resultCode,data);
                    } catch (UnavailableUserDeclinedInstallationException e) {
                        e.printStackTrace();
                    }
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

    public void setmOnAuthCompleted(OnAuthCompletedListener mOnAuthCompleted) {
        this.mOnAuthCompleted = mOnAuthCompleted;
        authDialogFragment.setOnAuthListener(mOnAuthCompleted);
    }

    public void promptSignin() {
        Log.d("FirebaseAuth", "showing auth dialog");
        authDialogFragment = new AuthDialog();
        authDialogFragment.setOnAuthListener(mOnAuthCompleted);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .replace(android.R.id.content, authDialogFragment)
                .commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_default_toolbar, menu);
        return true;
    }
}
