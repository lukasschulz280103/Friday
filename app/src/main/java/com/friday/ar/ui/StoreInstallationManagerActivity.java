package com.friday.ar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.friday.ar.FridayApplication;
import com.friday.ar.R;
import com.friday.ar.Theme;
import com.friday.ar.activities.FridayActivity;
import com.friday.ar.dialog.ErrorDialog;
import com.friday.ar.list.store.PluginListAdapter;
import com.friday.ar.plugin.application.PluginLoader;
import com.friday.ar.plugin.installer.PluginInstaller;

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
            //TODO:Add empty view handling
        } else {
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
            }
            case R.id.install_from_disk: {
                Intent selectPluginIntent = new Intent();
                selectPluginIntent.setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(selectPluginIntent, OPEN_PLUGIN_INTENT_CODE);
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == OPEN_PLUGIN_INTENT_CODE && resultCode == RESULT_OK) {
            File openedFile = new File(data.getData().toString());
            PluginInstaller installer = new PluginInstaller(this);
            try {
                installer.installFrom(openedFile);
            } catch (IOException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
            } catch (PluginInstaller.IllegalFileException e) {
                Log.e(LOGTAG, e.getLocalizedMessage(), e);
                ErrorDialog errorDialog = new ErrorDialog(this, R.drawable.ic_warning_black_24dp, e);
                errorDialog.show();
            }
        }
    }
}
