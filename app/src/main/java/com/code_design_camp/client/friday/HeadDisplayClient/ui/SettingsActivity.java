package com.code_design_camp.client.friday.HeadDisplayClient.ui;

import android.os.Bundle;
import android.view.MenuItem;

import com.code_design_camp.client.friday.HeadDisplayClient.Theme;
import com.code_design_camp.client.friday.HeadDisplayClient.activities.FridayActivity;
import com.code_design_camp.client.friday.HeadDisplayClient.fragments.preferenceFragments.MainSettingsFragment;

public class SettingsActivity extends FridayActivity {
    private static final String LOGTAG = "SettingsActivity";
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                setResult(RESULT_OK);
                finish();
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
