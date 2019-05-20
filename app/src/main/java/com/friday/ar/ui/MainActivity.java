package com.friday.ar.ui;


import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.friday.ar.fragments.dialogFragments.AuthDialog;
import com.friday.ar.fragments.dialogFragments.ChangelogDialogFragment;
import com.friday.ar.fragments.dialogFragments.UninstallOldAppDialog;
import com.friday.ar.fragments.interfaces.OnAuthCompletedListener;
import com.friday.ar.fragments.store.MainStoreFragment;
import com.friday.ar.fragments.store.ManagerBottomSheetDialogFragment;
import com.friday.ar.service.OnAccountSyncStateChanged;
import com.friday.ar.ui.store.StoreDetailActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.util.Arrays;

public class MainActivity extends FridayActivity implements OnAccountSyncStateChanged {
    public static final int FULLSCREEN_REQUEST_CODE = 22;
    private static final int REQUEST_PERMISSIONS_CODE = 900;
    private static final String LOGTAG = "FridayMainActivity";
    MainStoreFragment storeFragment = new MainStoreFragment();
    FridayApplication app;
    OnAuthCompletedListener mOnAuthCompleted;
    private ViewFlipper vswitcher_main;
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
            runOnUiThread(() -> {
                assetLoaderLayout.setTranslationY(-100);
                assetLoaderLayout.setVisibility(View.VISIBLE);
                assetLoaderLayout.animate().y(32f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
            });
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
        public void onError(Exception e) {
            loadedSpeechRecognizer = false;
            if (e instanceof SecurityException) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, REQUEST_PERMISSIONS_CODE);
            } else {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            }
            Toast.makeText(MainActivity.this, R.string.err_unable_to_load_speech_assets, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        vswitcher_main = findViewById(R.id.main_view_flipper);
        assetLoaderLayout = findViewById(R.id.asset_loading_indicator_conatiner);
        assetLoaderText = findViewById(R.id.asset_loading_text);
        loadingBar = findViewById(R.id.asset_loading_bar);
        defaut_pref = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            pkgInf = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Theme theme = new Theme(this);
        int appThemeIndex = theme.indexOf(Theme.getCurrentAppTheme(this));
        ((BottomNavigationView) findViewById(R.id.main_bottom_nav)).setOnNavigationItemSelectedListener(navselected);
        findViewById(R.id.start_actionmode).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FullscreenActionActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.mainTitleView).setBackground(theme.createGradient(appThemeIndex));
        findViewById(R.id.profileTitleViewContainer).setBackground(theme.createGradient(appThemeIndex));
        app = ((FridayApplication) getApplication());
        Log.d(LOGTAG, "SharedPref versionName is " + defaut_pref.getString("version", "1.0.0"));
        vswitcher_main.setDisplayedChild(0);
        checkForFirstUse();
        if (!defaut_pref.getString("version", "0").equals(pkgInf.versionName) || defaut_pref.getBoolean("pref_devmode_show_changelog", false)) {
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
            Log.i(LOGTAG, "no old version found");
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
            builder.setPositiveButton(R.string.deactivate, (dialogInterface, i) -> {

            });
            builder.setNegativeButton(R.string.later, null);
            builder.create().show();

        }
        app.setOnAssetsLoadedListener(mAssetsLoadedListener);
        assetLoaderText.setFactory(() -> new TextView(MainActivity.this));
        assetLoaderText.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        assetLoaderText.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        assetLoaderText.setCurrentText(getString(R.string.asset_loader_loading));
        app.loadSpeechRecognizer();

        app.registerForSyncStateChange(this);

        //Initialize Store
        getSupportFragmentManager().beginTransaction()
                .add(R.id.store_frag_container, storeFragment)
                .commit();
        ImageButton storeExpandManagerButton = findViewById(R.id.storeMore);
        storeExpandManagerButton.setOnClickListener(v -> {
            ManagerBottomSheetDialogFragment managerDialog = new ManagerBottomSheetDialogFragment(this);
            managerDialog.show(getSupportFragmentManager(), "ManagerBottomSheet");
        });
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
            Intent showWizard = new Intent(this, WizardActivity.class);
            startActivity(showWizard);
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //Fragments need to be removed before saving the instance state
        getSupportFragmentManager().beginTransaction()
                .remove(storeFragment)
                .commitAllowingStateLoss();
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
        if (authDialogFragment != null && authDialogFragment.isAdded()) {
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
                        data.putExtra("errtype", "TYPE_DEVICE_NOT_SUPPORTED");
                        onActivityResult(requestCode, resultCode, data);
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
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (!Arrays.equals(grantResults, new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED})) {
                MaterialAlertDialogBuilder permissionWarnDialog = new MaterialAlertDialogBuilder(this);
                permissionWarnDialog.setTitle(R.string.err_missing_permissions)
                        .setMessage(R.string.err_permission_need_explanation)
                        .setIcon(R.drawable.ic_twotone_security_24px)
                        .setCancelable(false)
                        .setPositiveButton(R.string.retry, (dialogInterface, which) -> requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS_CODE))
                        .create().show();
            } else {
                app.loadSpeechRecognizer();
            }
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

    @Override
    public void onSyncStateChanged() {

    }
}
