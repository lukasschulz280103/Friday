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
    MainSettingsFragment mainSettingsFragment = new MainSettingsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Theme.getCurrentAppTheme(this));
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                mainSettingsFragment).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        if (getIntent().hasExtra("hasChanged")) {
            themeChanged = getIntent().getBooleanExtra("hasChanged", false);
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onSelectedTheme(boolean hasChanged) {
        Log.d(LOGTAG, "hasChanged:" + hasChanged);
        themeChanged = hasChanged;
        if (hasChanged) {
            getIntent().putExtra("hasChanged", true);
            recreate();
            Log.d(LOGTAG, "context:" + getBaseContext());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOGTAG, "themeChanged:" + themeChanged);
        if (item.getItemId() == android.R.id.home && themeChanged) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
        return true;
    }
}
