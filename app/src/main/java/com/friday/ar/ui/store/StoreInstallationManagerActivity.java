package com.friday.ar.ui.store;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.friday.ar.list.store.PluginListAdapter;
import com.friday.ar.plugin.application.PluginLoader;
import com.friday.ar.plugin.file.ZippedPluginFile;
import com.friday.ar.plugin.installer.PluginInstaller;
import com.friday.ar.ui.FileSelectorActivity;
import com.friday.ar.util.DisplayUtil;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StoreInstallationManagerActivity extends FridayActivity {
    private static final String LOGTAG = "StoreInstallations";
    public static final int OPEN_PLUGIN_INTENT_CODE = 733;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store_installation_manager);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView list = findViewById(R.id.appList);
        PluginLoader pluginLoader = ((FridayApplication) getApplication()).getApplicationPluginLoader();
        list.setLayoutManager(new LinearLayoutManager(this));
        if (pluginLoader.getIndexedPlugins().size() == 0) {
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyView).setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(new PluginListAdapter(this, pluginLoader.getIndexedPlugins()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_installed_plugins, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finishAfterTransition();
                break;
            }
            case R.id.install_from_disk: {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (sharedPreferences.getBoolean("storeInstallationsManager_showSecurityWarning", true)) {
                    MaterialCheckBox dontShowAgainCheck = new MaterialCheckBox(this);
                    FrameLayout contentFrame = new FrameLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMarginStart(DisplayUtil.dpToPx(16));
                    layoutParams.setMarginEnd(DisplayUtil.dpToPx(16));
                    dontShowAgainCheck.setText(R.string.dont_show_again);
                    dontShowAgainCheck.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPreferences.edit().putBoolean("storeInstallationsManager_showSecurityWarning", !isChecked).apply());
                    contentFrame.addView(dontShowAgainCheck, layoutParams);
                    AlertDialog warnDialog = new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.storeInstallationsManager_diskInstallWarningTitle)
                            .setMessage(R.string.storeInstallationsManager_diskInstallWarningMessage)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                Intent selectPluginIntent = new Intent(StoreInstallationManagerActivity.this, FileSelectorActivity.class);
                                startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE);
                            })
                            .setView(contentFrame)
                            .setCancelable(false)
                            .create();
                    warnDialog.show();

                } else {
                    Intent selectPluginIntent = new Intent(this, FileSelectorActivity.class);
                    startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE);
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_PLUGIN_INTENT_CODE && resultCode == RESULT_OK && data != null) {
            Log.d(LOGTAG, "data:" + data.getData().toString());
            File openedFile = new File(Objects.requireNonNull(data.getData().getPath()));
            PluginInstaller installer = new PluginInstaller(this);
            try {
                installer.installFrom(new ZippedPluginFile(new File(openedFile.getPath())));
            } catch (IOException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            } catch (ZipException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
            }
        }
    }
}
