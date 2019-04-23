package com.friday.ar.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.friday.ar.list.store.PluginListAdapter;
import com.friday.ar.plugin.application.PluginLoader;
import com.friday.ar.plugin.installer.PluginInstaller;
import com.friday.ar.ui.FileSelectorActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;

public class StoreInstallationManagerActivity extends FridayActivity {
    public static final String LOGTAG = "StoreInstallations";
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
                Intent selectPluginIntent = new Intent(this, FileSelectorActivity.class);
                startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(LOGTAG, "data:" + data.getData().toString());
        if (requestCode == OPEN_PLUGIN_INTENT_CODE && resultCode == RESULT_OK) {
            File openedFile = new File(data.getData().toString());
            PluginInstaller installer = new PluginInstaller(this);
            try {
                installer.installFrom(new File(openedFile.getPath()));
            } catch (IOException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
            } catch (PluginInstaller.IllegalFileException e) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
                alertDialogBuilder.setIcon(R.drawable.file_cancel)
                        .setTitle(R.string.err_inappropriate_file_extension)
                        .setMessage(R.string.err_inappropriate_file_extension_msg)
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
            }
        }
    }
}
