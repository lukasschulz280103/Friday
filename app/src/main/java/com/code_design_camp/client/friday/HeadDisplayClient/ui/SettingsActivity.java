package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.code_design_camp.client.friday.HeadDisplayClient.activities.FridayActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.dialog.ThemeDialog;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.preferenceFragments.MainSettingsFragment;

public class SettingsActivity extends FridayActivity implements ThemeDialog.OnSelectedTheme {
    private static final String LOGTAG = "SettingsActivity";
    private boolean themeChanged = false;
    MainSettingsFragment msf = new MainSettingsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                msf).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onSelectedTheme(boolean hasChanged) {
        Log.d(LOGTAG, "hasChanged:" + hasChanged);
        themeChanged = hasChanged;
        if (hasChanged) {
            recreate();
            Log.d(LOGTAG, "context:" + getBaseContext());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (themeChanged) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
